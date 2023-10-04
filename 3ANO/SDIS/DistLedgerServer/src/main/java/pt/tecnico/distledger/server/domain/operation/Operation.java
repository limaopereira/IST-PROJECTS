package pt.tecnico.distledger.server.domain.operation;


import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class Operation {
    private String account;
    private List<Integer> prevTS;
    private List<Integer> TS;

    /**
     * Constructs an Operation object with the specified account, previous timestamp, and timestamp.
     *
     * @param fromAccount The account involved in the operation
     * @param prevTS      The previous timestamp of the operation
     * @param TS          The current timestamp of the operation
     */
    public Operation(String fromAccount, List<Integer> prevTS, List<Integer> TS) {
        this.account = fromAccount;
        this.prevTS = prevTS;
        this.TS = TS;
    }

    /**
     * Retrieves the account associated with this operation.
     *
     * @return The account involved in the operation
     */
    public String getAccount() {
        return account;
    }

    /**
     * Sets the account associated with this operation.
     *
     * @param account The account involved in the operation
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Sets the previous timestamp of this operation.
     *
     * @param prevTS The previous timestamp of the operation
     */
    public void setPrevTS(List<Integer> prevTS){
        this.prevTS = prevTS;
    }

    /**
     * Retrieves the previous timestamp of this operation.
     *
     * @return The previous timestamp of the operation
     */
    public List<Integer> getPrevTS(){
        return this.prevTS;
    }

    public void setTS(List<Integer> TS) {
        this.TS = TS;
    }

    /**
     * Retrieves the current timestamp of this operation.
     *
     * @return The current timestamp of the operation
     */
    public List<Integer> getTS() {
        return this.TS;
    }

    /**
     * Converts this Operation object to a gRPC message.
     * Subclasses should override this method to provide the specific logic for each operation type.
     *
     * @return A DistLedgerCommonDefinitions.Operation.Builder object representing this Operation object
     */
    public DistLedgerCommonDefinitions.Operation.Builder toGrpc(){
        DistLedgerCommonDefinitions.Operation.Builder operationBuilder = DistLedgerCommonDefinitions.Operation.newBuilder();
        operationBuilder
                .setType(DistLedgerCommonDefinitions.OperationType.OP_UNSPECIFIED)
                .setUserId(this.getAccount())
                .addAllPrevTS(this.getPrevTS())
                .addAllTS(this.getTS())
                .build();
        return operationBuilder;
    }

    /**
     * Updates the accounts based on this Operation object.
     * Subclasses should override this method to provide the specific logic for each operation type.
     *
     * @param accounts A concurrent map of account names and their balances
     * @throws Exception If any error occurs during the update process
     */
    public void update(ConcurrentMap<String, Integer> accounts) throws Exception {
    }

}
