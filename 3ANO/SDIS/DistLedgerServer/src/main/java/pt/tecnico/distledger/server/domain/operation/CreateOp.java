package pt.tecnico.distledger.server.domain.operation;

import pt.tecnico.distledger.server.domain.exceptions.AccountAlreadyExistsException;
import pt.tecnico.distledger.server.domain.exceptions.InvalidAccountArgumentsException;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class CreateOp extends Operation {

    /**
     * Constructs a CreateOp object with the specified account, previous timestamp, and timestamp.
     *
     * @param account The name of the account to be created
     * @param prevTS  The previous timestamp of the operation
     * @param TS      The current timestamp of the operation
     */
    public CreateOp(String account, List<Integer> prevTS, List<Integer> TS) {
        super(account, prevTS, TS);
    }

    /**
     * Converts this CreateOp object to a gRPC message.
     *
     * @return A DistLedgerCommonDefinitions.Operation.Builder object representing this CreateOp object
     */
    @Override
    public DistLedgerCommonDefinitions.Operation.Builder toGrpc(){
        DistLedgerCommonDefinitions.Operation.Builder operationBuilder = DistLedgerCommonDefinitions.Operation.newBuilder();
        operationBuilder
                .setType(DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT)
                .setUserId(this.getAccount())
                .addAllPrevTS(this.getPrevTS())
                .addAllTS(this.getTS())
                .build();
        return operationBuilder;
    }

    /**
     * Updates the accounts based on this CreateOp object. This method checks for invalid account names
     * and existing accounts, and if successful, creates a new account with a balance of 0.
     *
     * @param accounts A concurrent map of account names and their balances
     * @throws Exception If the account name is invalid or the account already exists
     */
    @Override
    public void update(ConcurrentMap<String, Integer> accounts) throws Exception{
        if (getAccount() == null || getAccount().isEmpty() || getAccount().isBlank()) {
            throw new InvalidAccountArgumentsException("Invalid user name.");
        }
        if (accounts.containsKey(getAccount())) {
            throw new AccountAlreadyExistsException("The user '" + getAccount() + "' already exists.");
        }
        accounts.put(getAccount(), 0);
    }
}
