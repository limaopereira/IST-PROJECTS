package pt.tecnico.distledger.userclient;

import pt.tecnico.distledger.sharedutils.SharedUtils;
import pt.tecnico.distledger.userclient.grpc.UserService;


public class UserClientMain {

    public static void main(String[] args) {

        // Check if the number of arguments passed is greater than 1.
        if (args.length > 1) {
            // Print an error message to the error stream.
            System.err.println("[ERROR]: Too many Arguments!");
            System.err.println("[ERROR]: Usage: mvn exec:java -Dexec.args=[-debug]");
            // Exit the program.
            return;
        }

        // Declare and initialize a boolean flag for debugging.

        final boolean debugFlag = (args.length == 1 ? args[0].equals("-debug") : false);

        // Log that the UserClientMain class is starting.
        SharedUtils.debug("Starting " + UserClientMain.class.getSimpleName() + ".", debugFlag);

        // Log the number of arguments received.
        SharedUtils.debug("Received " + args.length + " arguments.", debugFlag);

        // Log each argument passed to the program.
        for (int i = 0; i < args.length; i++) {
            SharedUtils.debug("arg[" + i + "] = " + args[i], debugFlag);
        }

        // Log that a CommandParser object is being created.
        SharedUtils.debug("Creating Command Parser.", debugFlag);

        // Instantiate a CommandParser object with a new UserService object.
        CommandParser parser = new CommandParser(new UserService(debugFlag));

        // Parse the user's input.
        parser.parseInput();

        // Close the parser.
        parser.close();
    }
}
