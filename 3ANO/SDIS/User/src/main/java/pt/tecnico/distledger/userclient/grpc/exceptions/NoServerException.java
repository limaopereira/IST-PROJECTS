package pt.tecnico.distledger.userclient.grpc.exceptions;

public class NoServerException extends RuntimeException{
    public NoServerException(String message){
        super(message);
    }
}
