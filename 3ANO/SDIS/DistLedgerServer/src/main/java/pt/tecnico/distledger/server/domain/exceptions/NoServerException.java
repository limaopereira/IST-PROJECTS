package pt.tecnico.distledger.server.domain.exceptions;

public class NoServerException extends RuntimeException{
    public NoServerException(String message){
        super(message);
    }
}