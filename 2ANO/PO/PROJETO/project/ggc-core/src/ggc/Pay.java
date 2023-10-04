package ggc;

public class Pay implements TransactionOperation{
    private Partner _partner;
    private int _offset;
    private int _date;

    public Pay(Partner partner,int offset, int date){
        _partner=partner;
        _offset=offset;
        _date=date;
    }

    public Double visitSaleTransaction(Sale sale){
        if(!sale.isPaid()){
            int deadline=sale.getDeadline();
            Double price=sale.getBaseValue();
            Double payment;
            int diffDate=deadline-_date;
            payment=_partner.pay(diffDate,_offset,price);
            sale.pay(_date,payment);
            return payment;
        }
        return 0.0;
    }
    
    public Double visitPurchaseTransaction(Purchase purchase){
        return 0.0;
    };
    
    public Double visitDisaggregationTransaction(Disaggregation disaggregation){
        return 0.0;
    };
}
