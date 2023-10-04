package ggc;

public class Sale extends Transaction{
    private Double _valueToPay;
    private int _deadline;
    private boolean _paid;

    public Sale(int id, String idPartner, String idProduct, int amount, Double price, int deadline){
        super(id,idPartner,idProduct,amount,price);
        _valueToPay=price;
        _deadline=deadline;
        _paid=false;
    }

    public Double accept(TransactionOperation transactionOperation){
        return transactionOperation.visitSaleTransaction(this);
    }

    public boolean accept(TransactionDisplay transactionDisplay){
        return transactionDisplay.visitSaleTransaction(this);
    }

    public int getDeadline(){
        return _deadline;
    }

    public Double getValueToPay(){
        return _valueToPay;
    }

    public void setValueToPay(Double valueToPay){
        _valueToPay=valueToPay;
    }

    public void pay(int date, Double paid){
        _paid=true;
        setPaymentDate(date);
        _valueToPay=paid;
    }

    public boolean isPaid(){
        return _paid;
    }

    public String toString(){
        String paymentDate="";
        if(_paid==true)
            paymentDate="|"+Integer.toString(getPaymentDate());
        return "VENDA|" + super.toString() + "|" + (int)Math.round(_valueToPay) + "|" + _deadline + paymentDate;
    }
}
