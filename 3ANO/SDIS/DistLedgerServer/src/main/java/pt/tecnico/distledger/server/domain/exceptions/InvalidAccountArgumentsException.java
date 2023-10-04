package pt.tecnico.distledger.server.domain.exceptions;

public class InvalidAccountArgumentsException extends Exception{
    public InvalidAccountArgumentsException(String message){
        super(message);
    }
}
