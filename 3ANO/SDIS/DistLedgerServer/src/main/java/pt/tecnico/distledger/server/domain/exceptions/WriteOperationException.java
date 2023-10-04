package pt.tecnico.distledger.server.domain.exceptions;

public class WriteOperationException extends Exception{
    public WriteOperationException(String message){
        super(message);
    }
}
