package pt.tecnico.distledger.server.domain;

import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.distledger.server.domain.exceptions.*;
import pt.tecnico.distledger.sharedutils.SharedUtils;
import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.CrossServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


// pensar em criar uma notExecutedUpdates que utiliza PriorityQueue ordenada pelo prevTS

public class ServerState {
    private boolean debug;
    private boolean active;
    private Lock lockWaitNotify;
    private Condition prevTSHappensBeforeValueTS;
    private String qualifier;
    private int indexTS;
    private List<Integer> valueTS;
    private List<Integer> replicaTS;
    private ConcurrentMap<String,Integer> accounts;
    private Queue<Operation> notExecutedOperations;
    private Set<List<Integer>> executedOperations;
    private ConcurrentMap<String, List<Integer>> timeStampTable;
    private List<Operation> ledger;
    private ConcurrentMap<String, String> servers;
    private ConcurrentMap<String, ManagedChannel> channels;
    private ConcurrentMap<String, DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub> blockingStubs;


    /**
     * Constructor for the ServerState class.
     *
     * @param qualifier Identifies the server.
     * @param debug Indicates whether debug information should be printed.
     */
    public ServerState(String qualifier, boolean debug) {
        this.debug = debug;
        this.lockWaitNotify = new ReentrantLock();
        this.prevTSHappensBeforeValueTS = lockWaitNotify.newCondition();
        this.active = true;
        this.qualifier = qualifier;
        this.indexTS = getIndexTS(this.qualifier);
        this.valueTS = initializeTS(3);
        this.replicaTS = initializeTS(3);
        this.notExecutedOperations = new PriorityQueue<>(ServerState::compareNotExecutedOperations);
        this.executedOperations = new HashSet<>();
        this.timeStampTable = new ConcurrentHashMap<>();
        this.servers = new ConcurrentHashMap<>();
        this.channels = new ConcurrentHashMap<>();
        this.blockingStubs = new ConcurrentHashMap<>();
        this.ledger = new ArrayList<>(); // Initialize ledger as an empty ArrayList
        this.accounts = new ConcurrentHashMap<>(); // Initialize accounts as a new ConcurrentHashMap
        accounts.put("broker",1000); // Set the initial balance of the "broker" account to 1000
    }

    /**
     * Activates the server.
     */
    public void activate(){
        SharedUtils.debug("Activate acquiring lock of server state.", debug);
        SharedUtils.debug("Activating the server.", debug); // Print debug message
        SharedUtils.debug("Activate releasing lock of server state.", debug);
        this.active = true; // Set active field to true
    }

    /**
     * Deactivates the server.
     */
    public void deactivate(){
        SharedUtils.debug("Deactivate acquiring lock of server state.", debug);
        SharedUtils.debug("Deactivating the server.", debug); // Print debug message
        SharedUtils.debug("Deactivate releasing lock of server state.", debug);
        this.active = false; // Set active field to false
    }

    /**
     * Gets the ledger.
     *
     * @return The ledger.
     */
    public List<Operation> getLedger(){
        SharedUtils.debug("getLedger acquiring lock of server state.", debug);
        SharedUtils.debug("Displaying ledger content.", debug); // Print debug message
        SharedUtils.debug("getLedger releasing lock of server state.", debug);
        return this.ledger; // Return the ledger
    }

    /**
     * Creates an account for a user with a given name.
     *
     * @param account the name of the user account to be created
     * @param prevTS the previous timestamp vector
     * @throws InactiveServerException if the server is not active
     * @return The timestamp vector for the createAccount operation
     */
    public List<Integer> createAccount(String account, List<Integer> prevTS) throws InactiveServerException{
        SharedUtils.debug("createAccount acquiring lock of server state.", debug);
        SharedUtils.debug("prevTS = " + prevTS + " || Creating account for user '" + account + "'.", debug);
        if(!this.active){
            SharedUtils.debug("Creating account for user '" + account + "' failed because server is down.", debug);
            SharedUtils.debug("createAccount releasing lock of server state.", debug);
            throw new InactiveServerException("The server is down.");
        }
        SharedUtils.debug("Increment replicaTS = " + replicaTS + ".", debug);
        incrementReplicaTS();
        SharedUtils.debug("Incremented replicaTS = " + replicaTS + ".", debug);
        List<Integer> TS = createTS(prevTS);
        SharedUtils.debug("Created TS = " + TS + ".", debug);
        SharedUtils.debug("Create new CreateAccount Operation", debug);
        Operation operation = new CreateOp(account, prevTS, TS);
        SharedUtils.debug("Add CreateAccount Operation to Ledger", debug);
        ledger.add(operation);
        SharedUtils.debug("Add CreateAccount Operation to notExecutedOperations", debug);
        notExecutedOperations.add(operation);
        SharedUtils.debug("createAccount releasing lock of server state.", debug);
        return TS;
    }

    /**
     * Transfers a specified amount from one account to another.
     *
     * @param fromAccount the name of the source account
     * @param destAccount the name of the destination account
     * @param amount the amount to be transferred
     * @param prevTS the previous timestamp vector
     * @throws InactiveServerException if the server is not active
     * @return The timestamp vector for the transferTo operation
     */
    public List<Integer> transferTo(String fromAccount, String destAccount, int amount, List<Integer> prevTS) throws  InactiveServerException {
        SharedUtils.debug("transferTo acquiring lock of server state.", debug);
        SharedUtils.debug("prevTS = " + prevTS + " || Transferring " + amount + " from user '" + fromAccount + "' to user '" + destAccount + "'.", debug);
        if (!this.active) {
            SharedUtils.debug("Transferring " + amount + " from user '" + fromAccount + "' to user '" + destAccount + "' failed because the server is down.", debug);
            SharedUtils.debug("transferTo releasing lock of server state.", debug);
            throw new InactiveServerException("The server is down.");
        }
        SharedUtils.debug("prevTS=" + prevTS, debug);

        SharedUtils.debug("Increment replicaTS = " + replicaTS + ".", debug);
        incrementReplicaTS();
        SharedUtils.debug("Incremented replicaTS = " + replicaTS + ".", debug);
        List<Integer> TS = createTS(prevTS);
        SharedUtils.debug("Created TS = " + TS + ".", debug);
        SharedUtils.debug("Create new TransferTo Operation", debug);
        Operation operation = new TransferOp(fromAccount, destAccount, amount, prevTS, TS);
        SharedUtils.debug("Add TransferTo Operation to Ledger", debug);
        ledger.add(operation);
        SharedUtils.debug("Add TransferTo Operation to notExecutedOperations", debug);
        notExecutedOperations.add(operation);
        SharedUtils.debug("transferTo releasing lock of server state.", debug);
        return TS;
    }

    /**
     * Retrieves the balance of a specified account.
     *
     * @param account the name of the account to retrieve the balance of
     * @param prevTS the previous timestamp vector
     * @throws InactiveServerException if the server is not active
     * @throws AccountNotFoundException if the specified account does not exist
     * @return The BalanceResult object containing the account balance and the associated timestamp vector
     */
    public BalanceResult getBalance(String account, List<Integer> prevTS) throws InactiveServerException, AccountNotFoundException, InterruptedException{
        SharedUtils.debug("getBalance acquiring lock of server state.", debug);
        SharedUtils.debug("prevTS = " + prevTS + " || Getting account balance of user '" + account + "'.", debug);
        if(!this.active){
            SharedUtils.debug("Getting account balance of user '" + account + "' failed because the server is down.", debug);
            SharedUtils.debug("getBalance releasing lock of server state.", debug);
            throw new InactiveServerException("The server is down.");
        }
        SharedUtils.debug("getBalance acquiring lockWaitNotify.", debug);
        lockWaitNotify.lock();
        while (!ServerState.happensBefore(prevTS, valueTS)) {
            SharedUtils.debug("prevTs still doesn't happen before valueTS", debug);
            SharedUtils.debug("getBalance releasing lockWaitNotify.", debug);
            SharedUtils.debug("getBalance releasing lock of server state.", debug);
            prevTSHappensBeforeValueTS.await();
            SharedUtils.debug("getBalance acquiring lock of server state.", debug);
            SharedUtils.debug("getBalance acquiring lockWaitNotify.", debug);
            SharedUtils.debug("Checking if prevTS happens before valueTS", debug);
        }
        SharedUtils.debug("getBalance releasing lockWaitNotify.", debug);
        lockWaitNotify.unlock();

        if(!accounts.containsKey(account)){
            SharedUtils.debug("Getting account balance of user '" + account + "' failed because the user does not exist.", debug);
            SharedUtils.debug("getBalance releasing lock of server state.", debug);
            throw new AccountNotFoundException("The user '" + account + "' does not exist.");
        }
        SharedUtils.debug("Getting account balance of user '" + account + "' completed successfully.", debug);
        SharedUtils.debug("getBalance releasing lock of server state.", debug);
        return new BalanceResult(accounts.get(account),valueTS);
    }

    /**
     * Updates the server state by executing pending operations.
     */
    public void update(){
        SharedUtils.debug("update acquiring lockWaitNotify.", debug);
        lockWaitNotify.lock();
        SharedUtils.debug("Check if there are updates that can be applied", debug);
        while (notExecutedOperations.size() != 0 && ServerState.happensBefore(notExecutedOperations.peek().getPrevTS(), valueTS)) {
            Operation op = notExecutedOperations.poll();
            try {
                SharedUtils.debug("Updating update with TS = " + op.getTS() + ".", debug);
                if(!executedOperations.contains(op.getTS())) {
                    op.update(accounts);
                    executedOperations.add(op.getTS());
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            SharedUtils.debug("Merge valueTS = " + valueTS + " with Operation TS = " + op.getTS() + ".", debug);
            valueTS = mergeTS(valueTS, op.getTS());
            SharedUtils.debug("Merged valueTS = " + valueTS + ".", debug);
            SharedUtils.debug("Signal pending readings of the existence of a new update.",debug);
            prevTSHappensBeforeValueTS.signal();
        }
        SharedUtils.debug("update releasing lockWaitNotify.", debug);
        lockWaitNotify.unlock();
    }

    /**

     Propagates a specified operation to all connected servers.

     @param otherLedger the operation to propagate
     @throws InactiveServerException if the server is not active
     */
    public void propagateState(DistLedgerCommonDefinitions.LedgerState otherLedger, List<Integer> otherReplicaTS, String qualifier) throws InactiveServerException{
        SharedUtils.debug("Received new gossip message from replica  '" + qualifier + "'.", debug);
        if(!this.active){
            SharedUtils.debug("Receiving new gossip message failed because the server is down.", debug);
            throw new InactiveServerException("The server is down.");
        }
        SharedUtils.debug("Updating replicaTS = " + otherReplicaTS + " from replica '" + qualifier +  "'.", debug);
        timeStampTable.put(qualifier,otherReplicaTS);
        SharedUtils.debug("Merging ledger with replica '"+ qualifier +"' ledger.", debug);
        mergeLedger(otherLedger, otherReplicaTS);
        update();
        SharedUtils.debug("Gossip message reception completed successfully.", debug);
    }

    /**
     * Updates the blocking stub for the specified server qualifier.
     *
     * @param qualifier The server qualifier (e.g., "A", "B", "C")
     * @throws NoServerException if no server of the specified type is found
     */
    public void updateBlockingStub(String qualifier) throws NoServerException{
        if(servers.get(qualifier) == null){
            List<String> lookupServers = SharedUtils.lookup("DistLedger", qualifier).get(qualifier);
            if(lookupServers == null){
                SharedUtils.debug("There is no server of type '" + qualifier + "'.", debug);
                return;
            }
            else{
                servers.put(qualifier, lookupServers.get(0));
            }
            if(channels.get(qualifier)!=null){
                channels.get(qualifier).shutdownNow();
            }
            channels.put(qualifier,ManagedChannelBuilder.forTarget(servers.get(qualifier)).usePlaintext().build());
            blockingStubs.put(qualifier, DistLedgerCrossServerServiceGrpc.newBlockingStub(channels.get(qualifier)));
        }
    }

    /**
     * Initiates gossip between servers to propagate state.
     */
    public void gossip(){
        switch (this.qualifier){
            case "A":
                sendGossipMessage("B");
                sendGossipMessage("C");
                break;
            case "B":
                sendGossipMessage("A");
                sendGossipMessage("C");
                break;
            case "C":
                sendGossipMessage("A");
                sendGossipMessage("B");
                break;
        }
    }

    /**
     * Sends a gossip message to the server with the specified qualifier.
     *
     * @param otherQualifier The server qualifier (e.g., "A", "B", "C") of the target server for gossip message
     */
    public void sendGossipMessage(String otherQualifier){
        SharedUtils.debug("Sending new gossip message to replica '" + otherQualifier + "'.", debug);
        boolean success = false;
        while(!success) {
            try {
                updateBlockingStub(otherQualifier);
                if(servers.get(otherQualifier) == null){
                    SharedUtils.debug("Sending new gossip message to replica '" + otherQualifier + "' failed because there are no servers of this qualifier active.", debug);
                    return;
                }
                SharedUtils.debug("Creating new gossip message.", debug);
                DistLedgerCommonDefinitions.LedgerState.Builder ledgerStateBuilder = DistLedgerCommonDefinitions.LedgerState.newBuilder();
                getLedgerQualifier(otherQualifier).forEach(
                        op -> ledgerStateBuilder.addLedger(op.toGrpc())
                );
                ledgerStateBuilder.build();
                blockingStubs.get(otherQualifier).propagateState(
                        PropagateStateRequest.newBuilder()
                                .setState(ledgerStateBuilder)
                                .addAllReplicaTS(replicaTS)
                                .setQualifier(qualifier)
                                .build()
                );
                success = true;
            } catch (Exception e){
                if (channels.get(otherQualifier)!=null && channels.get(otherQualifier).getState(true) == ConnectivityState.SHUTDOWN) {
                    servers.put(otherQualifier, null);
                } else {
                    return;
                }
            }
        }
    }

    /**
     * Retrieves a list of operations from the ledger based on a specified server qualifier.
     *
     * @param qualifier The server qualifier (e.g., "A", "B", "C")
     * @return A list of operations from the ledger that occurred after the last known timestamp for the specified server qualifier
     */
    public List<Operation> getLedgerQualifier(String qualifier){
        SharedUtils.debug("Estimating missing updates of replica '" + qualifier + "'.", debug);
        List<Operation> ledgerQualifier = new ArrayList<>();
        if(!timeStampTable.containsKey(qualifier)){
            return getLedger();
        }
        else{
            List<Integer> lastQualifierReplicaTS = timeStampTable.get(qualifier);
            for(Operation op : getLedger()){
                List<Integer> opTS = op.getTS();
                boolean success = false;
                for(int i = 0 ; i < 3; i++){
                    if(opTS.get(i)>lastQualifierReplicaTS.get(i)){
                        success = true;
                        break;
                    }
                }
                if(success){
                    ledgerQualifier.add(op);
                }
            }
            return ledgerQualifier;
        }
    }

    /**
     * Gets the index of the replica timestamp based on the server qualifier.
     *
     * @param qualifier The server qualifier (e.g., "A", "B", "C")
     * @return The index of the replica timestamp corresponding to the server qualifier
     */
    public int getIndexTS(String qualifier){
        switch (qualifier){
            case "A":
                return 0;
            case "B":
                return 1;
            case "C":
                return 2;
            default:
                return -1;
        }
    }

    /**
     * Checks if a timestamp (TS1) happens before another timestamp (TS2).
     *
     * @param TS1 The first timestamp to compare
     * @param TS2 The second timestamp to compare
     * @return true if TS1 happens before TS2, false otherwise
     */
    public static boolean happensBefore(List<Integer> TS1, List<Integer> TS2){
        for(int i = 0; i < 3; i++){
            if(TS1.get(i) > TS2.get(i)){
                return false;
            }
        }
        return true;
    }

    /**
     * Increments the replica timestamp at the server's index.
     */
    public void incrementReplicaTS(){
        this.replicaTS.set(this.indexTS, this.replicaTS.get(this.indexTS) + 1);
    }


    /**
     * Merges two timestamps (TS1 and TS2) into a single timestamp.
     *
     * @param TS1 The first timestamp to merge
     * @param TS2 The second timestamp to merge
     * @return A new timestamp resulting from the merge of TS1 and TS2
     */
    public List<Integer> mergeTS(List<Integer> TS1, List<Integer> TS2){
        List<Integer> mergedTS = new ArrayList<>(3);
        for (int i = 0; i<3; i++){
            if(TS1.get(i)>TS2.get(i)){
                mergedTS.add(TS1.get(i));
            }
            else{
                mergedTS.add(TS2.get(i));
            }
        }
        return mergedTS;
    }

    /**
     * Merges the specified ledger and replica timestamp with the current state.
     *
     * @param otherLedger     The ledger to merge with the current state
     * @param otherReplicaTS  The replica timestamp to merge with the current state
     */
    public void mergeLedger(DistLedgerCommonDefinitions.LedgerState otherLedger, List<Integer> otherReplicaTS){
        for(DistLedgerCommonDefinitions.Operation op : otherLedger.getLedgerList()){
            if(!ServerState.happensBefore(op.getTSList(),replicaTS)){
                Operation newOp;
                switch (op.getType()){
                    case OP_CREATE_ACCOUNT:
                        newOp = new CreateOp(
                                op.getUserId(),
                                op.getPrevTSList(),
                                op.getTSList()
                        );
                        break;
                    case OP_TRANSFER_TO:
                        newOp = new TransferOp(
                                op.getUserId(),
                                op.getDestUserId(),
                                op.getAmount(),
                                op.getPrevTSList(),
                                op.getTSList()
                        );

                        break;
                    default:
                        newOp = new Operation(
                                op.getUserId(),
                                op.getPrevTSList(),
                                op.getTSList()
                        );
                        break;
                }
                ledger.add(newOp);
                notExecutedOperations.add(newOp);
            }
        }
        mergeTS(replicaTS, otherReplicaTS);
    }

    /**
     * Initializes a new timestamp with the specified size, and sets all values to 0.
     *
     * @param size The size of the timestamp to be initialized
     * @return A new timestamp of the specified size with all values set to 0
     */
    public List<Integer> initializeTS(int size){
       List<Integer> newTS = new ArrayList<>(size);
       for(int i = 0; i<size; i++){
           newTS.add(0);
       }
       return newTS;
    }

    /**
     * Creates a new timestamp based on the previous timestamp (prevTS) and the current replica timestamp.
     *
     * @param prevTS The previous timestamp to be used as a base for the new timestamp
     * @return A new timestamp based on prevTS and the current replica timestamp
     */
    public List<Integer> createTS(List<Integer> prevTS){
        List<Integer> TS = new ArrayList<>(prevTS);
        SharedUtils.debug("TS=" + TS + " prevTS=" + prevTS, debug);
        TS.set(indexTS, replicaTS.get(indexTS));
        return TS;
    }

    /**
     * Compares two not executed operations (op1 and op2) based on their timestamps.
     *
     * @param op1 The first operation to compare
     * @param op2 The second operation to compare
     * @return -1 if op1 happens before op2, 1 otherwise
     */
    public static int compareNotExecutedOperations(Operation op1, Operation op2){
        if (ServerState.happensBefore(op1.getTS(),op2.getTS())){
            return -1;
        }
        else{
            return 1;
        }
    }
}
