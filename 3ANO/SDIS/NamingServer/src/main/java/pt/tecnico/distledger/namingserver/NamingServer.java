package pt.tecnico.distledger.namingserver;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.distledger.sharedutils.SharedUtils;

import java.io.IOException;

public class NamingServer {
    private static boolean debugFlag;
    private static int port;
    private static Server server;

    private static void shutdown(){
        SharedUtils.debug("Shutting down the naming server.", debugFlag);
        server.shutdown();
    }
    public static void main(String[] args) {

        // Get the port for the server
        port = Integer.parseInt(args[0]);
        debugFlag = (args.length == 2 ? args[1].equals("-debug") : false);

        SharedUtils.debug("Received " + args.length + " arguments.", debugFlag);
        for (int i = 0; i < args.length; i++) {
            SharedUtils.debug("arg[" + i + "] = " + args[i], debugFlag);
        }

        SharedUtils.debug("Creating NamingServerServices.", debugFlag);
        // Create a new instance of NamingServerServices
        NamingServerServices namingServerServices = new NamingServerServices(debugFlag);

        try {
            // Create a new instance of NamingServerImpl
            SharedUtils.debug("Creating NamingServer service implementation.",debugFlag);
            final BindableService namingServerImpl = new NamingServerServiceImpl(namingServerServices);

            // Create a new server to listen on the specified port and add the namingServerImpl
            SharedUtils.debug("Creating NamingServer and adding its services.",debugFlag);
            server = ServerBuilder
                    .forPort(port)
                    .addService(namingServerImpl)
                    .build();

            // Start the server
            SharedUtils.debug("NamingServer Started.", debugFlag);
            server.start();

            // Server threads are running in the background.
            SharedUtils.debug("Adding NamingServer ShutDownHook.",debugFlag);
            Runtime.getRuntime().addShutdownHook(new Thread(NamingServer::shutdown));
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

            // Do not exit the main thread. Wait until server is terminated.
            server.awaitTermination();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

}
