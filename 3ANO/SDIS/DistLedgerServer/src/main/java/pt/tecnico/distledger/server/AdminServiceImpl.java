package pt.tecnico.distledger.server;

import io.grpc.stub.StreamObserver;

import pt.tecnico.distledger.server.domain.operation.*;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.admin.AdminServiceGrpc;

public class AdminServiceImpl extends AdminServiceGrpc.AdminServiceImplBase {
    private ServerState serverState;

    /**
     * Constructor for AdminServiceImpl that takes a ServerState object as a parameter.
     *
     * @param serverState The ServerState object that holds the current state of the server.
     */
    public AdminServiceImpl(ServerState serverState){
        this.serverState = serverState;
    }

    /**
     * Activates the server.
     *
     * @param activateRequest The ActivateRequest object containing the request details.
     * @param activateObserver The StreamObserver for the ActivateResponse object.
     */
    @Override
    public void activate(ActivateRequest activateRequest, StreamObserver<ActivateResponse> activateObserver){
        synchronized (serverState) {
            serverState.activate();
        }
        activateObserver.onNext(ActivateResponse.newBuilder().build());
        activateObserver.onCompleted();
    }

    /**
     * Deactivates the server.
     *
     * @param deactivateRequest The DeactivateRequest object containing the request details.
     * @param deactivateObserver The StreamObserver for the DeactivateResponse object.
     */
    @Override
    public void deactivate(DeactivateRequest deactivateRequest, StreamObserver<DeactivateResponse> deactivateObserver){
        synchronized (serverState){
            serverState.deactivate();
        }
        deactivateObserver.onNext(DeactivateResponse.newBuilder().build());
        deactivateObserver.onCompleted();
    }

    /**
     * Retrieves the current state of the ledger.
     *
     * @param getLedgerStateRequest The getLedgerStateRequest object containing the request details.
     * @param getLedgerStateObserver The StreamObserver for the getLedgerStateResponse object.
     */
    @Override
    public void getLedgerState(getLedgerStateRequest getLedgerStateRequest, StreamObserver<getLedgerStateResponse> getLedgerStateObserver) {
        // Create a builder for the LedgerState object
        DistLedgerCommonDefinitions.LedgerState.Builder ledgerStateBuilder = DistLedgerCommonDefinitions.LedgerState.newBuilder();

        synchronized (serverState) {
            // Loop through the list of operations in the server state's ledger
            serverState.getLedger().forEach(
                    operation -> ledgerStateBuilder.addLedger(operation.toGrpc())
            );
        }

        // Build the LedgerState object
        ledgerStateBuilder.build();
        // Send the LedgerState object to the client
        getLedgerStateObserver.onNext(getLedgerStateResponse.newBuilder().setLedgerState(ledgerStateBuilder).build());
        getLedgerStateObserver.onCompleted();
    }

    /**
     * Initiates gossiping to propagate server state to other servers.
     *
     * @param gossipRequest The GossipRequest object containing the request details.
     * @param gossipObserver The StreamObserver for the GossipResponse object.
     */
    @Override
    public void gossip(GossipRequest gossipRequest, StreamObserver<GossipResponse> gossipObserver){
        synchronized (serverState){
            serverState.gossip();
        }
        gossipObserver.onNext(GossipResponse.newBuilder().build());
        gossipObserver.onCompleted();
    }
}
