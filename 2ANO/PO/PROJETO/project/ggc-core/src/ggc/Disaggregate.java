package ggc;

import java.util.Map;
import ggc.exceptions.InvalidProductAmountException;

public class Disaggregate{
    private int _currentTransactionId;
    private String _idPartner;
    private int _amount;
    private int _date;

    public Disaggregate(int currentTransactionId, String idPartner, int amount, int date){
        _currentTransactionId=currentTransactionId;
        _idPartner=idPartner;
        _amount=amount;
        _date=date;
    }

    public Disaggregation visitSimpleProduct(SimpleProduct product){
        return null;
    }

    public Disaggregation visitDerivedProduct(DerivedProduct product) throws InvalidProductAmountException{
        
        if(product.getTotalStock()<_amount)
            throw new InvalidProductAmountException(product.getId(),_amount,product.getTotalStock());
        Recipe recipe=product.getRecipe();
        Double pricePurchase=0.0;
        String textDisaggregation="";
        for(Map.Entry<Product,Integer> entry : recipe.getRecipe().entrySet()){
            Product component=entry.getKey();
            Integer componentAmount=entry.getValue();
            Integer componentAmountCreated=_amount*componentAmount;
            String componentId=component.getId();
            Double componentPrice=component.getMinPrice();
            Double componentValue=componentPrice*componentAmountCreated; 
            pricePurchase+=componentValue;
            Batch batch=new Batch(componentId,_idPartner,componentPrice,componentAmountCreated);
            textDisaggregation+=componentId+":"+componentAmountCreated+":"+(int)Math.round(componentValue)+"#";
            component.addBatch(batch);
        }
        textDisaggregation=textDisaggregation.substring(0,textDisaggregation.length()-1);
        Double priceSale=product.accept(new Sell(_amount));
        Double baseValue=priceSale-pricePurchase;
        Disaggregation disaggregation=new Disaggregation(_currentTransactionId, _idPartner, product.getId(), _amount, baseValue, _date, textDisaggregation);
        return disaggregation;
    }
    
}
