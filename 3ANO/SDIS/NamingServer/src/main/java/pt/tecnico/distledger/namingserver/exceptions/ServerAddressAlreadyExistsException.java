package pt.tecnico.distledger.namingserver.exceptions;

public class ServerAddressAlreadyExistsException extends Exception{
    public ServerAddressAlreadyExistsException(String message){
        super("Not possible to register the server: " + message);
    }
}
