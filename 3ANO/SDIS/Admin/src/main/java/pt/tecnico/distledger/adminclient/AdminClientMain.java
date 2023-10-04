package pt.tecnico.distledger.adminclient;

import pt.tecnico.distledger.adminclient.grpc.AdminService;
import pt.tecnico.distledger.sharedutils.SharedUtils;

public class AdminClientMain {

    public static void main(String[] args) {

        // Check if too many arguments were provided
        if (args.length > 1) {
            System.err.println("[ERROR]: Too many arguments!");
            System.err.println("[ERROR]: Usage: mvn exec:java -Dexec.args=[-debug]");
            return;
        }

        // Determine if debug mode should be enabled based on the presence of the "-debug" flag
        final boolean debugFlag = (args.length == 1 ? args[0].equals("-debug") : false);

        // Print a debug message indicating that the AdminClientMain class is starting
        SharedUtils.debug("Starting " + AdminClientMain.class.getSimpleName() + ".",debugFlag);

        // Print a debug message indicating how many arguments were received, if any
        SharedUtils.debug("Received " + args.length + " arguments.", debugFlag);

        // Loop through the arguments and print each one, if debug mode is enabled
        for (int i = 0; i < args.length; i++) {
            SharedUtils.debug("arg[" + i + "] = " + args[i], debugFlag);
        }

        // Create a CommandParser object and parse the user's input
        SharedUtils.debug("Creating Command Parser.",debugFlag);
        CommandParser parser = new CommandParser(new AdminService(debugFlag));
        parser.parseInput();
        parser.close();
    }
}
