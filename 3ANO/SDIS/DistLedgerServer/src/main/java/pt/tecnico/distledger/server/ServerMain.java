package pt.tecnico.distledger.server;

import io.grpc.*;
import pt.tecnico.distledger.sharedutils.SharedUtils;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;

import java.io.IOException;


public class ServerMain {

    // Debug flag to control debug messages
    private static boolean debugFlag;
    // Server port number
    private static int port;
    // Server qualifier
    private static String qualifier;
    // Server instance
    private static Server server;
    // ManagedChannel to communicate with the NamingServer
    private static ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();
    // Blocking stub for the NamingServer
    private static NamingServerServiceGrpc.NamingServerServiceBlockingStub namingServerBlockingStub = NamingServerServiceGrpc.newBlockingStub(channel);

    // Shutdown method to gracefully stop the server and unregister from the NamingServer
    private static void shutdown(){
        SharedUtils.debug("Shutting down the server.", debugFlag);
        try {
            SharedUtils.debug("Delete server record from NamingServer.", debugFlag);
            namingServerBlockingStub.delete(
                    DeleteRequest.newBuilder()
                            .setServiceName("DistLedger")
                            .setAddress("localhost:" + port)
                            .build()
            );
            SharedUtils.debug("Server record from NamingServer deleted.", debugFlag);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        server.shutdown();
    }

    public static void main(String[] args) throws IOException{
        // Check if the required arguments are provided
        if (args.length < 2) {
            System.err.println("[ERROR]: Argument(s) missing!");
            System.err.printf("[ERROR]: Usage: mvn exec:java -Dexec.args=\"<host> <port> [-debug]\"\n");
            return;
        }

        // Process the command-line arguments
        debugFlag = (args.length == 3 ? args[2].equals("-debug") : false);
        port = Integer.parseInt(args[0]);
        qualifier = args[1];

        SharedUtils.debug("Starting " + ServerMain.class.getSimpleName() + ".",debugFlag);

        SharedUtils.debug("Received " + args.length + " arguments.", debugFlag);
        for (int i = 0; i < args.length; i++) {
            SharedUtils.debug("arg[" + i + "] = " + args[i], debugFlag);
        }

        SharedUtils.debug("Creating Server State.",debugFlag);
        ServerState serverState = new ServerState(qualifier, debugFlag);

        try {
            // Create service implementations
            SharedUtils.debug("Creating CrossServer service implementation.",debugFlag);
            final BindableService crossServerImpl = new CrossServerServiceImpl(serverState);
            SharedUtils.debug("Creating Admin service implementation.",debugFlag);
            final BindableService adminImpl = new AdminServiceImpl(serverState);
            SharedUtils.debug("Creating User service implementation.",debugFlag);
            final BindableService userImpl = new UserServiceImpl(serverState);

            // Build the server with the service implementations
            SharedUtils.debug("Creating DistLedgerServer and adding its services.",debugFlag);
            server = ServerBuilder
                    .forPort(port)
                    .addService(crossServerImpl)
                    .addService(adminImpl)
                    .addService(userImpl)
                    .build();

            // Start the server
            server.start();
            SharedUtils.debug("DistLedgerServer started.",debugFlag);



            // Register the server with the NamingServer
            try {
                namingServerBlockingStub.register(
                        RegisterRequest.newBuilder()
                                .setServiceName("DistLedger")
                                .setQualifier(qualifier)
                                .setAddress("localhost:" + port)
                                .build()
                );
            }
            catch (Exception e){
                System.out.println(e.getMessage());
                server.shutdown();
                System.exit(0);
            }
            SharedUtils.debug("Adding DistLedgerServer ShutDownHook.",debugFlag);
            // Add a shutdown hook to handle graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(ServerMain::shutdown));
            // Start a new thread to listen for the user to press Enter to shutdown the server
            new Thread(() -> {
                try {
                    System.out.print("Press enter to shutdown.");
                    System.in.read();
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                System.exit(0);
            }).start();

            // Wait for the server to terminate
            server.awaitTermination();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
