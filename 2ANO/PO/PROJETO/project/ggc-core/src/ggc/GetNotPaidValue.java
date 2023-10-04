package ggc;

public class GetNotPaidValue implements TransactionOperation{

    public Double visitSaleTransaction(Sale sale){
        if(!sale.isPaid())
            return sale.getValueToPay();
        return 0.0;
    }

    public Double visitPurchaseTransaction(Purchase purchase){
        return 0.0;
    }

    public Double visitDisaggregationTransaction(Disaggregation disaggregation){
        return 0.0;
    }
}
