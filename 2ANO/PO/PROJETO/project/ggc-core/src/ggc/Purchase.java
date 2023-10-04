package ggc;

public class Purchase extends Transaction{
    public Purchase(int id,  String idPartner, String idProduct, int amount, Double price, int paymentDate){
        super(id,idPartner,idProduct,amount,price*amount,paymentDate);
    }

    public Double accept(TransactionOperation transactionOperation){
        return transactionOperation.visitPurchaseTransaction(this);
    }

    public boolean accept(TransactionDisplay transactionDisplay){
        return transactionDisplay.visitPurchaseTransaction(this);
    }

    public String toString(){
        return "COMPRA|" + super.toString() + "|" + getPaymentDate();
    }
}
