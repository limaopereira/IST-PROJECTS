package pt.tecnico.distledger.namingserver.exceptions;

public class ServiceNameNotFoundException extends Exception{
    public ServiceNameNotFoundException(String message){
        super("Not possible to delete the server: " + message);
    }
}
