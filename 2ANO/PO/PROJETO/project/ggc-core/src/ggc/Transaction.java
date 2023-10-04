package ggc;

import java.io.Serializable;

public abstract class Transaction implements Serializable{
    private int _id;
    private String _idPartner;
    private String _idProduct;
    private int _amount;
    private Double _baseValue;
    private int _paymentDate;

    public Transaction(int id,  String idPartner, String idProduct, int amount, Double baseValue){
        _id=id;
        _idPartner=idPartner;
        _idProduct=idProduct;
        _amount=amount;
        _baseValue=baseValue;
    }    

    public Transaction(int id,  String idPartner, String idProduct, int amount, Double baseValue, int paymentDate){
        this(id, idPartner, idProduct, amount, baseValue);
        _paymentDate=paymentDate;
    }

    public abstract Double accept(TransactionOperation transactionOperation);
    public abstract boolean accept(TransactionDisplay transactionDisplay);

    public String getIdPartner(){
        return _idPartner;
    }

    public String getIdProduct(){
        return _idProduct;
    }

    public Double getBaseValue(){
        return _baseValue;
    }

    public int getPaymentDate(){
        return _paymentDate;
    }

    public void setPaymentDate(int date){
        _paymentDate=date;
    }

    public String toString(){
        return _id + "|" + _idPartner + "|" + _idProduct + "|" + _amount + "|" + (int)Math.round(_baseValue);
    }
}