package pt.tecnico.distledger.adminclient.grpc;

import io.grpc.*;
import pt.tecnico.distledger.adminclient.grpc.exceptions.NoServerException;
import pt.tecnico.distledger.sharedutils.SharedUtils;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class AdminService {

    // Maps a server qualifier to its address
    private ConcurrentMap<String, String> servers;
    // Maps a server qualifier to its blocking stub
    private ConcurrentMap<String, AdminServiceGrpc.AdminServiceBlockingStub> blockingStubs;
    // Maps a server qualifier to its channel
    private ConcurrentMap<String, ManagedChannel> channels;
    // Flag indicating whether debug mode is on or off
    private boolean debug;

    /**
     * Constructor for the AdminService class.
     * @param debug Flag indicating whether debug mode is on or off.
     */
    public AdminService(boolean debug) {
        this.debug = debug;
        this.servers = new ConcurrentHashMap<>();
        this.blockingStubs = new ConcurrentHashMap<>();
        this.channels = new ConcurrentHashMap<>();
    }

    /**
     * Getter for the debug flag.
     * @return The debug flag.
     */
    public boolean getDebug(){
        return debug;
    }

    /**
     * Updates the blocking stub for a given server qualifier.
     * @param qualifier The server qualifier.
     * @throws NoServerException If there is no server of the given qualifier.
     */
    public void updateBlockingStub(String qualifier){
        if(servers.get(qualifier) == null){
            // Lookup the servers for the given qualifier
            List<String> lookupServers = SharedUtils.lookup("DistLedger", qualifier).get(qualifier);
            if(lookupServers == null){
                throw new NoServerException("There is no server of type '" + qualifier + "'.");
            }
            else{
                // Use the first server in the list
                servers.put(qualifier,lookupServers.get(0));
            }
            // Shutdown the old channel, if any
            if(channels.get(qualifier)!=null){
                channels.get(qualifier).shutdownNow();
            }
            // Create a new channel and blocking stub
            channels.put(qualifier,ManagedChannelBuilder.forTarget(servers.get(qualifier)).usePlaintext().build());
            blockingStubs.put(qualifier, AdminServiceGrpc.newBlockingStub(channels.get(qualifier)));
        }
    }

    /**
     * Activates a server of a given qualifier.
     * @param qualifier The server qualifier.
     */
    public void activate(String qualifier){
        boolean success = false;
        while(!success){
            try {
                updateBlockingStub(qualifier);
                ActivateResponse response = blockingStubs.get(qualifier).activate(ActivateRequest.getDefaultInstance());
                SharedUtils.debug("Activate: response = " + response, debug);
                System.out.println("OK");
                success = true;
            }
            catch (Exception e){
                // If the channel was shut down, mark the server as inactive
                if(channels.get(qualifier)!=null && channels.get(qualifier).getState(true) == ConnectivityState.SHUTDOWN){
                    servers.put(qualifier,null);
                }
                else{
                    SharedUtils.debug("Activate Error: "+ e.getMessage(), debug);
                    System.out.println("Caught exception with description: " + e.getMessage());
                    success = true;
                }
            }
        }
        return;

    }

    /**
     * Deactivates a server of a given qualifier.
     * @param qualifier The server qualifier.
     */
    public void deactivate(String qualifier){
        boolean success = false;
        while(!success) {
            try {
                updateBlockingStub(qualifier);
                DeactivateResponse response = blockingStubs.get(qualifier).deactivate(DeactivateRequest.getDefaultInstance());
                SharedUtils.debug("Deactivate: response = " + response, debug);
                System.out.println("OK");
                success = true;
            } catch (Exception e) {
                // If channel was shutdown, update servers map to null
                if (channels.get(qualifier) != null && channels.get(qualifier).getState(true) == ConnectivityState.SHUTDOWN) {
                    servers.put(qualifier, null);
                } else {
                    // If there was an error, log it and print a message to the user
                    SharedUtils.debug("Deactivate Error: " + e.getMessage(), debug);
                    System.out.println("Caught exception with description: " + e.getMessage());
                    success = true;
                }
            }
        }
        return;
    }

    /**
     * This method sends a request to the server to dump its ledger state.
     * It updates the blocking stub associated with the provided qualifier, then sends a
     * getLedgerState request to the server and prints the server's response.
     *
     * If there is an error, it logs the error and prints a message to the user.
     *
     * @param qualifier the qualifier of the server to dump
     */
    public void dump(String qualifier) {
        boolean success = false;
        while (!success) {
            try {
                updateBlockingStub(qualifier);
                getLedgerStateResponse response = blockingStubs.get(qualifier).getLedgerState(getLedgerStateRequest.getDefaultInstance());
                SharedUtils.debug("Dump: response = " + response, debug);
                System.out.println("OK");
                System.out.println(response);
                success = true;
            } catch (Exception e) {
                // If channel was shutdown, update servers map to null
                if (channels.get(qualifier) != null && channels.get(qualifier).getState(true) == ConnectivityState.SHUTDOWN) {
                    servers.put(qualifier, null);
                } else {
                    // If there was an error, log it and print a message to the user
                    SharedUtils.debug("Dump Error: " + e.getMessage(), debug);
                    System.out.println("Caught exception with description: " + e.getMessage());
                    success = true;
                }
            }
        }
        return;
    }

    /**
     * This method closes all the channels in the channels map.
     * It is called when the CommandParser instance is closed.
     */
    public void close() {
        SharedUtils.debug("Closing Channels", debug);
        channels.forEach((qualifier, channel) -> {
            if (channel != null && !channel.isShutdown()) {
                channel.shutdownNow();
            }
        });
    }

    /**
     * This method forces a propagation of updates from a replica to all others.
     *
     * If there is an error, it logs the error and prints a message to the user.
     *
     * @param qualifier the qualifier of the server to dump
     */
    public void gossip(String qualifier){
        boolean success = false;
        while(!success){
            try {
                updateBlockingStub(qualifier);
                GossipResponse response = blockingStubs.get(qualifier).gossip(GossipRequest.getDefaultInstance());
                SharedUtils.debug("Activate: response = " + response, debug);
                System.out.println("OK");
                success = true;
            }
            catch (Exception e){
                // If the channel was shut down, mark the server as inactive
                if(channels.get(qualifier)!=null && channels.get(qualifier).getState(true) == ConnectivityState.SHUTDOWN){
                    servers.put(qualifier,null);
                }
                else{
                    SharedUtils.debug("Activate Error: "+ e.getMessage(), debug);
                    System.out.println("Caught exception with description: " + e.getMessage());
                    success = true;
                }
            }
        }
        return;
    }
}



