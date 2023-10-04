package ggc.exceptions;

public class InvalidProductAmountException extends Exception{
    private String _idProduct;
    private int _amountNeeded;
    private int _amountAvailable;

    public InvalidProductAmountException(String idProduct, int amountNeeded, int amountAvailable){
        _idProduct=idProduct;
        _amountNeeded=amountNeeded;
        _amountAvailable=amountAvailable;
    }

    public String getIdProduct(){
        return _idProduct;
    }

    public int getAmountNeeded(){
        return _amountNeeded;
    }

    public int getAmountAvailable(){
        return _amountAvailable;
    }

}