package ggc;

public class CalculatePrice implements TransactionOperation{
    private Partner _partner;
    private int _offset;
    private int _date;

    public CalculatePrice(Partner partner, int offset, int date){
        _partner=partner;
        _offset=offset;
        _date=date;
    }

    public Double visitSaleTransaction(Sale sale){
        if(!sale.isPaid()){
            int deadline=sale.getDeadline();
            Double price=sale.getBaseValue();
            int diffDate=deadline-_date;
            Double valueToPay=_partner.calculatePrice(diffDate,_offset,price);
            sale.setValueToPay(valueToPay);
            return valueToPay;
        }
        return 0.0;
    }

    public Double visitPurchaseTransaction(Purchase purchase){
        return 0.0;
    }

    public Double visitDisaggregationTransaction(Disaggregation disaggregation){
        return 0.0;
    }
    
}
