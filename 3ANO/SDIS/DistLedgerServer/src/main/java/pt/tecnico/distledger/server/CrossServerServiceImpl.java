package pt.tecnico.distledger.server;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import pt.tecnico.distledger.server.domain.ServerState;

import pt.tecnico.distledger.server.domain.exceptions.InactiveServerException;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;

public class CrossServerServiceImpl extends DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceImplBase {

    private ServerState serverState;

    /**
     * Constructor for the CrossServerServiceImpl class.
     *
     * @param serverState The state of the server.
     */
    public CrossServerServiceImpl(ServerState serverState){
        this.serverState = serverState;
    }

    /**
     * This method propagates the state of the server to other servers in the cluster.
     *
     * @param propagateStateRequest The request message containing the operation to propagate.
     * @param propagateStateObserver The response observer to send the result of the operation to.
     */
    @Override
    public void propagateState(PropagateStateRequest propagateStateRequest, StreamObserver<PropagateStateResponse> propagateStateObserver) {
        try {
            // Call the propagateState() method of the ServerState class to propagate the state to other servers.
            serverState.propagateState(
                    propagateStateRequest.getState(),
                    propagateStateRequest.getReplicaTSList(),
                    propagateStateRequest.getQualifier()
            );
            // Send a response to the client with an empty PropagateStateResponse message.
            propagateStateObserver.onNext(PropagateStateResponse.newBuilder().build());
            propagateStateObserver.onCompleted();
        }
        catch (InactiveServerException e){
            // Send an error message to the client if the server is inactive.
            propagateStateObserver.onError(Status.UNAVAILABLE.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}