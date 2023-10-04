package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.exceptions.AccountNotFoundException;
import pt.tecnico.distledger.server.domain.exceptions.InvalidTransferAmountException;
import pt.tecnico.distledger.server.domain.exceptions.InvalidTransferArguments;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class TransferOp extends Operation {
    private String destAccount;
    private int amount;

    /**
     * Constructor for the TransferOp class.
     *
     * @param fromAccount The source account for the transfer
     * @param destAccount The destination account for the transfer
     * @param amount The amount to be transferred
     * @param prevTS The previous timestamp
     * @param TS The current timestamp
     */
    public TransferOp(String fromAccount, String destAccount, int amount, List<Integer> prevTS, List<Integer> TS) {
        super(fromAccount, prevTS, TS);
        this.destAccount = destAccount;
        this.amount = amount;
    }

    /**
     * Retrieves the destination account for the transfer.
     *
     * @return The destination account for the transfer
     */
    public String getDestAccount() {
        return destAccount;
    }

    /**
     * Sets the destination account for the transfer.
     *
     * @param destAccount The destination account for the transfer
     */
    public void setDestAccount(String destAccount) {
        this.destAccount = destAccount;
    }

    /**
     * Retrieves the amount to be transferred.
     *
     * @return The amount to be transferred
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount to be transferred.
     *
     * @param amount The amount to be transferred
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Converts the TransferOp instance to a gRPC-compatible operation.
     *
     * @return A DistLedgerCommonDefinitions.Operation.Builder instance representing the transfer operation
     */
    @Override
    public DistLedgerCommonDefinitions.Operation.Builder toGrpc(){
        DistLedgerCommonDefinitions.Operation.Builder operationBuilder = DistLedgerCommonDefinitions.Operation.newBuilder();
        operationBuilder
                .setType(DistLedgerCommonDefinitions.OperationType.OP_TRANSFER_TO)
                .setUserId(this.getAccount())
                .setAmount(this.getAmount())
                .setDestUserId(this.getDestAccount())
                .addAllPrevTS(this.getPrevTS())
                .addAllTS(this.getTS())
                .build();
        return operationBuilder;
    }

    /**
     * Updates the accounts based on the transfer operation.
     *
     * @param accounts The concurrent map of accounts to be updated
     * @throws Exception If any validation or account update error occurs
     */
    @Override
    public void update(ConcurrentMap<String, Integer> accounts) throws Exception {
        if (amount <= 0) {
            throw new InvalidTransferAmountException("The amount to be transferred needs to be positive.");
        }
        if (getAccount().compareTo(destAccount) == 0) {
            throw new InvalidTransferArguments("The source account has to be different from the destination account.");
        }

        if (!accounts.containsKey(getAccount())) {
            throw new AccountNotFoundException("The user '" + getAccount() + "' does not exist.");
        }
        if (!accounts.containsKey(destAccount)) {
            throw new AccountNotFoundException("The user '" + destAccount + "' does not exist.");
        }

        if (accounts.get(getAccount()) < amount) {
            throw new InvalidTransferAmountException("The amount to be transferred is greater than the account balance of user '" + getAccount() + "'.");
        }
        accounts.put(getAccount(), accounts.get(getAccount()) - amount);
        accounts.put(destAccount, accounts.get(destAccount) + amount);
    }
}
