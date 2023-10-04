package pt.tecnico.distledger.namingserver.exceptions;

public class ServerAddressNotFoundException extends Exception{
    public ServerAddressNotFoundException(String message){
        super("Not possible to delete the server: " + message);
    }
}
