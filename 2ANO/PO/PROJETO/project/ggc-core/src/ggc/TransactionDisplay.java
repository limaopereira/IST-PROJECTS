package ggc;

public interface TransactionDisplay{
    public boolean visitSaleTransaction(Sale sale);
    public boolean visitPurchaseTransaction(Purchase purchase);
    public boolean visitDisaggregationTransaction(Disaggregation disaggregation);
}