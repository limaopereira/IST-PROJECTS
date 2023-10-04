package pt.tecnico.distledger.server.domain.exceptions;

public class InvalidTransferAmountException extends Exception{
    public InvalidTransferAmountException(String message){
        super(message);
    }
}
