package pt.tecnico.distledger.server.domain;

import java.util.List;

public class BalanceResult {
    private int balance;
    private List<Integer> valueTS;

    /**
     * Constructor for the BalanceResult class.
     *
     * @param balance The balance value of an account.
     * @param valueTS The timestamp vector associated with the account balance.
     */
    public BalanceResult(int balance, List<Integer> valueTS){
        this.balance = balance;
        this.valueTS = valueTS;
    }

    /**
     * Gets the balance value of the account.
     *
     * @return The balance value.
     */
    public int getBalance(){
        return this.balance;
    }

    /**
     * Gets the timestamp vector associated with the account balance.
     *
     * @return The timestamp vector.
     */
    public List<Integer> getValueTS() {
        return valueTS;
    }
}
