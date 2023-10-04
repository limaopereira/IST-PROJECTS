package ggc;

import ggc.exceptions.InvalidProductAmountException;
import java.util.Map;

public class SimpleProduct extends Product{
    public SimpleProduct(String id, Double maxPrice){
        super(id,maxPrice,5);
    }

    public Double accept(Sell sell){
        return sell.visitSimpleProduct(this);
    }

    public Disaggregation accept(Disaggregate disaggregate) throws InvalidProductAmountException{
        return disaggregate.visitSimpleProduct(this);
    }

    public Map<Product,Integer> accept(HasAvailableStock hasAvailableStock) throws InvalidProductAmountException{
        return hasAvailableStock.visitSimpleProduct(this);
    }
}
