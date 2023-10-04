package ggc;

public class Disaggregation extends Transaction{
    private Double _paidValue=0.0;
    private String _textDisaggregation;

    public Disaggregation(int id,  String idPartner, String idProduct, int amount, Double baseValue, int paymentDate, String textDisaggregation){
        super(id,idPartner,idProduct,amount,baseValue,paymentDate);
        if(baseValue>0)
            _paidValue=baseValue;
        _textDisaggregation=textDisaggregation;
    }

    public Double getPaidValue(){
        return _paidValue;
    }

    public boolean isPaid(){
        return _paidValue>0;
    }

    public Double accept(TransactionOperation transactionOperation){
        return transactionOperation.visitDisaggregationTransaction(this);
    }

    public boolean accept(TransactionDisplay transactionDisplay){
        return transactionDisplay.visitDisaggregationTransaction(this);
    }

    public String toString(){
        return "DESAGREGAÇÃO|" + super.toString() + "|" + (int)Math.round(_paidValue) + "|" + getPaymentDate() + "|" + _textDisaggregation; 
    }
}
