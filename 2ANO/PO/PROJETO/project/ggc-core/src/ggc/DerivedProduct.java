package ggc;

import java.util.Map;
import ggc.exceptions.InvalidProductAmountException;

public class DerivedProduct extends Product{
    private Recipe _recipe;
    private String _textDisaggregation;
    
    public DerivedProduct(String id, Double maxPrice, Recipe recipe){
        super(id,maxPrice,3);
        _recipe=recipe;
    }

    public Double accept(Sell sell){
        return sell.visitDerivedProduct(this);
    }

    public Disaggregation accept(Disaggregate disaggregate) throws InvalidProductAmountException{
        return disaggregate.visitDerivedProduct(this);
    }

    public Map<Product,Integer> accept(HasAvailableStock hasAvailableStock) throws InvalidProductAmountException{
        return hasAvailableStock.visitDerivedProduct(this);
    }

    public Recipe getRecipe(){
        return _recipe;
    }


    public String getTextDisaggregation(){
        return _textDisaggregation;
    }

    public String toString(){
        return super.toString() + "|" + _recipe.toString();
    }
}
