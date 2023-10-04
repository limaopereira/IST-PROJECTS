package pt.tecnico.distledger.adminclient;

import pt.tecnico.distledger.sharedutils.SharedUtils;
import pt.tecnico.distledger.adminclient.grpc.AdminService;

import java.util.Scanner;

public class CommandParser {

    // Constants used to identify the command entered by the user
    private static final String SPACE = " ";
    private static final String ACTIVATE = "activate";
    private static final String DEACTIVATE = "deactivate";
    private static final String GET_LEDGER_STATE = "getLedgerState";
    private static final String GOSSIP = "gossip";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    private final AdminService adminService;

    /**
     * Constructs a new CommandParser object with the given AdminService instance.
     *
     * @param adminService an instance of the AdminService class
     */
    public CommandParser(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * This method reads the user's input from the command line, and calls the appropriate method on the
     * AdminService object based on the command entered. If an invalid command is entered, the usage
     * information is printed to the console.
     */
    void parseInput() {

        Scanner scanner = new Scanner(System.in);
        SharedUtils.debug("Start Scanner", adminService.getDebug());
        boolean exit = false;

        // Loop until the user enters the "exit" command
        while (!exit) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String cmd = line.split(SPACE)[0];

            // Determine which command was entered and call the appropriate method
            switch (cmd) {
                case ACTIVATE:
                    this.activate(line);
                    break;

                case DEACTIVATE:
                    this.deactivate(line);
                    break;

                case GET_LEDGER_STATE:
                    this.dump(line);
                    break;

                case GOSSIP:
                    this.gossip(line);
                    break;

                case HELP:
                    this.printUsage();
                    break;

                case EXIT:
                    exit = true;
                    break;

                default:
                    break;
            }

        }
    }

    /**
     * This method activates a server by calling the activate() method on the AdminService object with the
     * server name as a parameter.
     *
     * @param line a string containing the user's input
     */
    private void activate(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        SharedUtils.debug("Parser.Activate: server = " + server, adminService.getDebug());

        adminService.activate(server);
    }

    /**
     * This method deactivates a server by calling the deactivate() method on the AdminService object with the
     * server name as a parameter.
     *
     * @param line a string containing the user's input
     */
    private void deactivate(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        SharedUtils.debug("Parser.Deactivate: server = " + server, adminService.getDebug());

        adminService.deactivate(server);
    }

    /**
     * This method dumps the state of a server by calling the dump() method on the AdminService object with the
     * server name as a parameter.
     *
     * @param line a string containing the user's input
     */
    private void dump(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        SharedUtils.debug("Parser.Dump: server = " + server, adminService.getDebug());

        adminService.dump(server);
    }

    /**
     * This method prints the usage information to the console.
     */
    private void printUsage() {
        System.out.println("Usage:\n" +
                "- activate <server>\n" +
                "- deactivate <server>\n" +
                "- getLedgerState <server>\n" +
                "- gossip <server>\n" +
                "- exit\n");
    }

    /**
     * This method closes the CommandParser and the AdminService objects.
     */
    public void close(){
        SharedUtils.debug("Closing CommandParser.", adminService.getDebug());
        adminService.close();
    }

    /**
     * This method processes the gossip command by calling the gossip() method on the AdminService object with the
     * server name as a parameter.
     *
     * @param line a string containing the user's input
     */
    private void gossip(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        SharedUtils.debug("Parser.Gossip: server = " + server, adminService.getDebug());

        adminService.gossip(server);
    }
}
