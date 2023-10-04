package ggc;

public interface TransactionOperation{
    public Double visitSaleTransaction(Sale sale);
    public Double visitPurchaseTransaction(Purchase purchase);
    public Double visitDisaggregationTransaction(Disaggregation disaggregation);
}
