package pt.tecnico.distledger.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import pt.tecnico.distledger.server.domain.BalanceResult;
import pt.tecnico.distledger.server.domain.exceptions.*;
import pt.tecnico.distledger.server.domain.ServerState;

import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import java.util.List;


public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private ServerState serverState;

    /**
     * Constructor for the UserServiceImpl class.
     *
     * @param serverState the server state to be used by the service.
     */
    public UserServiceImpl(ServerState serverState){
        this.serverState = serverState;
    }

    /**
     * Implementation of the createAccount RPC method.
     *
     * @param createAccountRequest the request message containing the user ID of the account to be created.
     * @param createAccountObserver the response observer for the createAccount method.
     */
    @Override
    public void createAccount(CreateAccountRequest createAccountRequest, StreamObserver<CreateAccountResponse> createAccountObserver){
        try {
            // Create the account with the provided user ID.
            List<Integer> TS;
            synchronized (serverState) {
                TS = serverState.createAccount(
                        createAccountRequest.getUserId(),
                        createAccountRequest.getPrevTSList()
                );
            }
            // Send a response message containing the timestamp to the client.
            createAccountObserver.onNext(CreateAccountResponse.newBuilder().addAllTS(TS).build());
            // Notify the client that the response is complete.
            createAccountObserver.onCompleted();


            serverState.update();

        }
        catch(InactiveServerException e){
            // Send an error message to the client if the server is inactive.
            createAccountObserver.onError(Status.UNAVAILABLE.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /**
     * Retrieves the balance of the account with the specified user ID.
     *
     * @param balanceRequest The request containing the user ID.
     * @param balanceObserver The observer to notify of the response.
     */
    @Override
    public void balance(BalanceRequest balanceRequest, StreamObserver<BalanceResponse> balanceObserver){
        try {
            // Retrieve the balance of the account.

            BalanceResult balanceResult = serverState.getBalance(
                    balanceRequest.getUserId(),
                    balanceRequest.getPrevTSList()
            );

            // Send the balance and the timestamp to the client.
            balanceObserver.onNext(BalanceResponse.newBuilder()
                    .setValue(balanceResult.getBalance())
                    .addAllValueTS(balanceResult.getValueTS())
                    .build()
            );

            // Notify the client that the request is complete.
            balanceObserver.onCompleted();
        }
        catch(InactiveServerException e){
            // Send an error message to the client if the server is inactive.
            balanceObserver.onError(Status.UNAVAILABLE.withDescription(e.getMessage()).asRuntimeException());
        }
        catch (AccountNotFoundException e){
            // Send an error message to the client if the account does not exist.
            balanceObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
        catch (InterruptedException e){
            balanceObserver.onError(Status.CANCELLED.withDescription("The request was interrupted.").asRuntimeException());
        }
    }

    /**
     * Transfers the specified amount from one account to another.
     *
     * @param transferToRequest The request containing the accounts and amount to transfer.
     * @param transferToObserver The observer to notify of the response.
     */
    @Override
    public void transferTo(TransferToRequest transferToRequest, StreamObserver<TransferToResponse> transferToObserver){
        try {
            // Transfer the specified amount from one account to another.
            List<Integer> TS;
            synchronized (serverState) {
                TS = serverState.transferTo(
                        transferToRequest.getAccountFrom(),
                        transferToRequest.getAccountTo(),
                        transferToRequest.getAmount(),
                        transferToRequest.getPrevTSList()
                );
            }
            // Send a response message containing the timestamp to the client.
            transferToObserver.onNext(TransferToResponse.newBuilder().addAllTS(TS).build());

            // Notify the client that the request is complete.
            transferToObserver.onCompleted();

            serverState.update();

        }
        catch (InactiveServerException e){
            // Send an error message to the client if the server is inactive.
            transferToObserver.onError(Status.UNAVAILABLE.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
