package ggc;

public class DisplayPartnerPaidTransactions implements TransactionDisplay{
    private String _idPartner;

    public DisplayPartnerPaidTransactions(String idPartner){
        _idPartner=idPartner;
    }

    public boolean visitSaleTransaction(Sale sale){
        return sale.isPaid() && sale.getIdPartner().equalsIgnoreCase(_idPartner);
    }

    public boolean visitPurchaseTransaction(Purchase purchase){
        return false;
    }

    public boolean visitDisaggregationTransaction(Disaggregation disaggregation){
        return disaggregation.isPaid() && disaggregation.getIdPartner().equalsIgnoreCase(_idPartner);
    }
}
