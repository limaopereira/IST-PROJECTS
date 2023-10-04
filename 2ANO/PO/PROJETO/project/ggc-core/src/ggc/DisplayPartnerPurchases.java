package ggc;

public class DisplayPartnerPurchases implements TransactionDisplay{
    private String _idPartner;

    public DisplayPartnerPurchases(String idPartner){
        _idPartner=idPartner;
    }
    
    public boolean visitSaleTransaction(Sale sale){
        return false;
    }

    public boolean visitPurchaseTransaction(Purchase purchase){
        return purchase.getIdPartner().equalsIgnoreCase(_idPartner);
    } 

    public boolean visitDisaggregationTransaction(Disaggregation disaggregation){
        return false;
    }
}
