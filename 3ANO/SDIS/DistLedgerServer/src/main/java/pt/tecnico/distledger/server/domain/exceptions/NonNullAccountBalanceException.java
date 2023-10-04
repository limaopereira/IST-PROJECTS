package pt.tecnico.distledger.server.domain.exceptions;

public class NonNullAccountBalanceException extends Exception{
    public NonNullAccountBalanceException(String message){
        super(message);
    }
}
