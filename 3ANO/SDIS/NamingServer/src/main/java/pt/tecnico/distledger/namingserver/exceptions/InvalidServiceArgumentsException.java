package pt.tecnico.distledger.namingserver.exceptions;

public class InvalidServiceArgumentsException extends Exception{
    public InvalidServiceArgumentsException(String message){
        super("Not possible to register the server: " + message);
    }
}
