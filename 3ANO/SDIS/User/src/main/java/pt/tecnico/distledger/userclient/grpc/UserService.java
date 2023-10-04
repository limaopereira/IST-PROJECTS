package pt.tecnico.distledger.userclient.grpc;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.tecnico.distledger.sharedutils.SharedUtils;
import pt.tecnico.distledger.userclient.grpc.exceptions.NoServerException;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserService {

	private List<Integer> prevTS;
	private ConcurrentMap<String, String> servers;
	private ConcurrentMap<String, UserServiceGrpc.UserServiceBlockingStub> blockingStubs;
	private ConcurrentMap<String, ManagedChannel> channels;
	private boolean debug;

	/**

	 Constructs a new UserService object with the given debug flag set to false.
	 Initializes the ConcurrentHashMaps for servers, blockingStubs, and channels.

	 @param debug specifies whether or not debug output should be printed to the console.
	 */
	public UserService(boolean debug) {
		this.debug = debug;
		this.servers = new ConcurrentHashMap<>();
		this.blockingStubs = new ConcurrentHashMap<>();
		this.channels = new ConcurrentHashMap<>();
		this.prevTS = initializeTS(3);
	}

	public List<Integer> initializeTS(int size){
		List<Integer> newTS = new ArrayList<>(size);
		for(int i = 0; i<size; i++){
			newTS.add(0);
		}
		return newTS;
	}


	/**

	Sets the debug flag for this UserService object.

	@param debug specifies whether or not debug output should be printed to the console.
	*/
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**

	 Returns the debug flag for this UserService object.

	 @return the debug flag for this UserService object.
	 */
	public boolean getDebug() {
		return debug;
	}

	/**

	 Updates the blocking stub for the given qualifier.

	 @param qualifier the qualifier for which the blocking stub should be updated.
	 @throws NoServerException if there are no servers available for the given qualifier.
	 */
	public void updateBlockingStub(String qualifier) throws  NoServerException{
		if(servers.get(qualifier) == null){
			List<String> lookupServers = SharedUtils.lookup("DistLedger", qualifier).get(qualifier);
			if(lookupServers == null){
				throw new NoServerException("There is no server of type '" + qualifier + "'.");
			}
			else{
				servers.put(qualifier, lookupServers.get(0));
			}
			if(channels.get(qualifier)!=null){
				channels.get(qualifier).shutdownNow();
			}
			channels.put(qualifier,ManagedChannelBuilder.forTarget(servers.get(qualifier)).usePlaintext().build());
			blockingStubs.put(qualifier, UserServiceGrpc.newBlockingStub(channels.get(qualifier)));
		}
	}

	/**

	 Sends a gRPC request to create a new account with the given user ID and qualifier.

	 @param qualifier the qualifier for the user service.
	 @param userId the ID of the user to create.
	 @return a CreateAccountResponse object containing information about the new account.
	 @throws StatusRuntimeException if the gRPC request fails.
	 */
	public CreateAccountResponse createAccount(String qualifier, String userId) throws StatusRuntimeException{
		CreateAccountResponse createAccountResponse = null;
		boolean success = false;
		while (!success) {
			try {
				updateBlockingStub(qualifier);
				SharedUtils.debug("Requesting account creation for user " + userId, debug);
				createAccountResponse =
						blockingStubs.get(qualifier).createAccount(CreateAccountRequest.newBuilder()
								.setUserId(userId).addAllPrevTS(prevTS).build());
				SharedUtils.debug("Account created for user " + userId + ", prevTS = " + prevTS + ".", debug);
				success = true;
			} catch (Exception e) {
				if (channels.get(qualifier)!=null && channels.get(qualifier).getState(true) == ConnectivityState.SHUTDOWN) {
					servers.put(qualifier, null);
				} else {
					throw e;
				}
			}
		}
		prevTS = createAccountResponse.getTSList();
		SharedUtils.debug("New prevTS  = " + prevTS, debug);
		return createAccountResponse;
	}

	/**

	 Deletes an account with the given userId from the server with the given qualifier.

	 @param qualifier the qualifier of the server to delete the account from
	 @param userId the id of the user whose account will be deleted
	 @return the response from the server after deleting the account
	 @throws StatusRuntimeException if there is an error during the RPC call
	 */
	public BalanceResponse balance(String qualifier, String userId) {
		BalanceResponse balanceResponse = null;
		boolean success = false;
		while(!success) {
			try {
				updateBlockingStub(qualifier);
				SharedUtils.debug("Requesting balance for user " + userId, debug);
				balanceResponse =
						blockingStubs.get(qualifier).balance(BalanceRequest.newBuilder()
								.setUserId(userId).addAllPrevTS(prevTS).build());
				SharedUtils.debug("Balance request successful, prevTS = " + prevTS + "." , debug);
				success = true;
			} catch (Exception e) {
				if (channels.get(qualifier) != null && channels.get(qualifier).getState(true) == ConnectivityState.SHUTDOWN) {
					servers.put(qualifier, null);
				} else {
					throw e;
				}
			}
		}
		prevTS = balanceResponse.getValueTSList();
		SharedUtils.debug("New prevTS  = " + prevTS, debug);
		return balanceResponse;
	}


	/**

	 Transfers the given amount from one account to another.

	 @param qualifier the qualifier of the server to use
	 @param accountFrom the account ID to transfer from
	 @param accountTo the account ID to transfer to
	 @param amount the amount to transfer
	 @return the response from the server
	 @throws StatusRuntimeException if there was an error during the RPC call
	 */
 	public TransferToResponse transferTo(String qualifier, String accountFrom, String accountTo, int amount) {
		TransferToResponse transferToResponse = null;
		boolean success = false;
		while(!success){
			try{
				updateBlockingStub(qualifier);
				SharedUtils.debug("Requesting transfer of " + amount + " from " + accountFrom + " to " + accountTo, debug);
				transferToResponse =
						blockingStubs.get(qualifier).transferTo(TransferToRequest.newBuilder()
								.setAccountFrom(accountFrom)
								.setAccountTo(accountTo)
								.setAmount(amount)
								.addAllPrevTS(prevTS)
								.build());
				SharedUtils.debug("Transfer successful, prevTS = " + prevTS+ ".", debug);
				success = true;
			}
			catch (Exception e){
				if (channels.get(qualifier) != null && channels.get(qualifier).getState(true) == ConnectivityState.SHUTDOWN) {
					servers.put(qualifier, null);
				} else {
					throw e;
				}
			}
		}
		prevTS = transferToResponse.getTSList();
		SharedUtils.debug("New prevTS  = " + prevTS, debug);
		return transferToResponse;
	}

	/**

	 Closes all gRPC channels created by this service.
	 This method iterates over all the channels present in the channels map and shuts down each of them.
	 If a channel is already shutdown, it will be skipped. The debug flag is used to enable or disable logging
	 of debug messages.
	 */
	public void close() {
		SharedUtils.debug("Closing Channels", debug);
		channels.forEach((qualifier, channel) -> {
			if (channel != null && !channel.isShutdown()) {
				channel.shutdownNow();
			}
		});
	}
}
