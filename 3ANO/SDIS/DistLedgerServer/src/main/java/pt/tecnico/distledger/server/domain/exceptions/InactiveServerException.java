package pt.tecnico.distledger.server.domain.exceptions;

public class InactiveServerException extends Exception{
    public InactiveServerException(String message){
        super(message);
    }
}
