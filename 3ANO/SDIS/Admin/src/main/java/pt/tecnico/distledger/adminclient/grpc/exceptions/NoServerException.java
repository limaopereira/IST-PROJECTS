package pt.tecnico.distledger.adminclient.grpc.exceptions;

public class NoServerException extends RuntimeException{
    public NoServerException(String message){
        super(message);
    }
}
