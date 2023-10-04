package pt.tecnico.distledger.server.domain.exceptions;

public class InvalidTransferArguments extends Exception{
    public InvalidTransferArguments(String message){
        super(message);
    }
}
