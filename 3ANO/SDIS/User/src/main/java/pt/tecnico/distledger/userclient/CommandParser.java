package pt.tecnico.distledger.userclient;

import pt.tecnico.distledger.sharedutils.SharedUtils;
import pt.tecnico.distledger.userclient.grpc.UserService;
import pt.ulisboa.tecnico.distledger.contract.user.*;

import java.util.Scanner;

public class CommandParser {

    private static final String SPACE = " ";
    private static final String CREATE_ACCOUNT = "createAccount";
    private static final String DELETE_ACCOUNT = "deleteAccount";
    private static final String TRANSFER_TO = "transferTo";
    private static final String BALANCE = "balance";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    private final UserService userService;
    private boolean debug;

    /**

     Constructs a new CommandParser with a given UserService.

     @param userService the UserService to use
     */
    public CommandParser(UserService userService) {
        this.userService = userService;
        this.debug = userService.getDebug();
    }


    /**

     Returns the current debug flag.

     @return the current debug flag
     */
    public boolean getDebug() {
        return debug;
    }

    /**

     Sets the debug flag to the given value.

     @param debug the value to set the debug flag to
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**

     Parses user input and executes commands.
     */
    void parseInput() {

        SharedUtils.debug("Parsing input.", debug);
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String cmd = line.split(SPACE)[0];

            try{
                switch (cmd) {
                    case CREATE_ACCOUNT:
                        this.createAccount(line);
                        break;

                    case TRANSFER_TO:
                        this.transferTo(line);
                        break;

                    case BALANCE:
                        this.balance(line);
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
            catch (Exception e){
                SharedUtils.debug("Caught following exception: " + e.getClass().getCanonicalName(), debug);
                System.out.println(e.getMessage());
            }
        }
    }

    /**

     Executes the createAccount command with the given input.

     @param line the input to execute the command with
     */
    private void createAccount(String line){
        String[] split = line.split(SPACE);

        if (split.length != 3){
            SharedUtils.debug("Wrong usage, invalid number of arguments.", debug);
            this.printUsage();
            return;
        }
        String server = split[1];
        String username = split[2];
        SharedUtils.debug("Selected server: "+server, debug);
        SharedUtils.debug("Selected username: "+username, debug);

        userService.createAccount(server, username);

        System.out.println("OK");
    }


    /**
     * Retrieves the balance for a user account in a given server and prints it to the console.

     * @param line A string containing the command line input.
     */
    private void balance(String line) {
        String[] split = line.split(SPACE);

        if (split.length != 3) {
            SharedUtils.debug("Wrong usage, invalid number of arguments.", debug);
            this.printUsage();
            return;
        }
        String server = split[1];
        String username = split[2];
        SharedUtils.debug("Selected server: " + server, debug);
        SharedUtils.debug("Selected username: " + username, debug);

        UserDistLedger.BalanceResponse balanceResponse = userService.balance(server, username);

        System.out.println("OK");
        if (balanceResponse != null && balanceResponse.getValue() != 0) {
            System.out.println(balanceResponse.getValue());
        }
    }

    /**
     * Transfers an amount from one user account to another in a given server and prints a success message to the console.

     * @param line A string containing the command line input.
     */
    private void transferTo(String line){
        String[] split = line.split(SPACE);

        if (split.length != 5){
            SharedUtils.debug("Wrong usage, invalid number of arguments.", debug);
            this.printUsage();
            return;
        }
        String server = split[1];
        String from = split[2];
        String dest = split[3];
        Integer amount = Integer.valueOf(split[4]);
        SharedUtils.debug("Selected server: "+server, debug);
        SharedUtils.debug("Selected from: "+from, debug);
        SharedUtils.debug("Selected dest: "+dest, debug);
        SharedUtils.debug("Selected amount: "+amount, debug);

        userService.transferTo(server, from, dest, amount);

        System.out.println("OK");
    }

    /**
     * Prints the usage instructions to the console.
     */
    private void printUsage() {
        SharedUtils.debug("Printing usage.", debug);
        System.out.println("Usage:\n" +
                "- createAccount <server> <username>\n" +
                "- balance <server> <username>\n" +
                "- transferTo <server> <username_from> <username_to> <amount>\n" +
                "- exit\n");
    }

    /**

     Closes the CommandParser and the UserService instance that it uses.
     Calls the UserService's close() method.
     */
    public void close() {

        SharedUtils.debug("Closing CommandParser.", debug);
        userService.close();
    }
}
