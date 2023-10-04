package ggc.exceptions;

public class InvalidComponentKeyException extends Exception{
    private String _componentId;

    public InvalidComponentKeyException(String componentId){
        _componentId=componentId;
    }

    public String getComponentId(){
        return _componentId;
    }
}
