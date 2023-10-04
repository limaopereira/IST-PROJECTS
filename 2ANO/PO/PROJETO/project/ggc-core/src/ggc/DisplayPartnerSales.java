package ggc;

public class DisplayPartnerSales implements TransactionDisplay{
    private String _idPartner;

    public DisplayPartnerSales(String idPartner){
        _idPartner=idPartner;
    }

    public boolean visitSaleTransaction(Sale sale){
        return sale.getIdPartner().equalsIgnoreCase(_idPartner);
    }

    public boolean visitPurchaseTransaction(Purchase purchase){
        return false;
    }

    public boolean visitDisaggregationTransaction(Disaggregation disaggregation){
        return disaggregation.getIdPartner().equalsIgnoreCase(_idPartner);
    }
}
