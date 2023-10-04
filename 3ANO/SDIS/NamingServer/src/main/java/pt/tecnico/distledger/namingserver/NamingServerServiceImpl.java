package pt.tecnico.distledger.namingserver;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import pt.tecnico.distledger.namingserver.exceptions.ServerAddressAlreadyExistsException;
import pt.tecnico.distledger.namingserver.exceptions.InvalidServiceArgumentsException;
import pt.tecnico.distledger.namingserver.exceptions.ServerAddressNotFoundException;
import pt.tecnico.distledger.namingserver.exceptions.ServiceNameNotFoundException;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;


public class NamingServerServiceImpl extends NamingServerServiceGrpc.NamingServerServiceImplBase {
    private NamingServerServices namingServerServices;

    /**

     Constructs a NamingServerServiceImpl object.

     @param namingServerServices a NamingServerServices object representing the naming server's services.
     */
    public NamingServerServiceImpl(NamingServerServices namingServerServices) {
        this.namingServerServices = namingServerServices;
    }

    /**

     Registers a server in the naming server.

     @param registerRequest a RegisterRequest object representing the request to register a server.
     @param registerObserver a StreamObserver of RegisterResponse objects representing the response to the registration request.
     */
    public void register(RegisterRequest registerRequest, StreamObserver<RegisterResponse> registerObserver) {
        try {
            synchronized (namingServerServices) {
                namingServerServices.register(
                        registerRequest.getServiceName(),
                        registerRequest.getQualifier(),
                        registerRequest.getAddress()
                );
            }
            registerObserver.onNext(RegisterResponse.newBuilder().build());
            registerObserver.onCompleted();
        }
        catch (InvalidServiceArgumentsException e){
            registerObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
        catch (ServerAddressAlreadyExistsException e){
            registerObserver.onError(Status.ALREADY_EXISTS.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    /**

     Looks up server addresses associated with a specified service and qualifier combination.

     @param lookupRequest request containing the service name and qualifier to look up server addresses for
     @param lookupObserver observer to send the lookup response to the client
     */
    public void lookup(LookupRequest lookupRequest, StreamObserver<LookupResponse> lookupObserver){
        LookupResponse.Builder lookupResponseBuilder = LookupResponse.newBuilder();

        synchronized (namingServerServices) {
            namingServerServices.lookup(
                    lookupRequest.getRequestedService(),
                    lookupRequest.getQualifier()
            ).forEach(
                    serverEntry -> lookupResponseBuilder.addServerInfo(serverEntry.toGrpc())
            );
        }

        lookupObserver.onNext(lookupResponseBuilder.build());
        lookupObserver.onCompleted();
    }

    /**

     Deletes a server address associated with a specified service and server address combination.

     @param deleteRequest request containing the service name and server address to delete
     @param deleteObserver observer to send the deletion response to the client
     */
    public void delete(DeleteRequest deleteRequest, StreamObserver<DeleteResponse> deleteObserver) {
        try {
            synchronized (namingServerServices) {
                namingServerServices.delete(
                        deleteRequest.getServiceName(),
                        deleteRequest.getAddress()
                );
            }
            deleteObserver.onNext(DeleteResponse.newBuilder().build());
            deleteObserver.onCompleted();
        } catch (ServiceNameNotFoundException e) {
            deleteObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (ServerAddressNotFoundException e) {
            deleteObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
