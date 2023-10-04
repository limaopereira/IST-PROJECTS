package ggc;

import ggc.exceptions.InvalidProductAmountException;
import java.util.Map;
import java.util.HashMap;

public class Sell{
    private int _amount;
    
    public Sell(int amount){
        _amount=amount;
    }
    
    private Double sellProduct(Product product, int amount){
        int amountNeeded=_amount;
        Double price=0.0;
        while(amountNeeded>0 && product.getBatchesNumber()>0){
            Batch batch = product.getBatches().peek();
            int batchStock=batch.getStock();
            Double batchPrice=batch.getPrice();
            if(batchStock<=amountNeeded){
                price+=batchStock*batchPrice;
                amountNeeded-=batchStock;
                product.removeBatch(batch);
            }
            else{
                price+=amountNeeded*batchPrice;
                batch.setStock(batchStock-amountNeeded);
                amountNeeded=0;
            }
        }
        return price;
    }

    public Double visitSimpleProduct(SimpleProduct product){
        return sellProduct(product,_amount);
    }
    
    public Double visitDerivedProduct(DerivedProduct product){
        Double price=0.0;
        if(product.getTotalStock()<_amount){
            Recipe recipe=product.getRecipe();
            Double aggravation=recipe.getAggravation();
            int productStock=product.getTotalStock();
            int amountNeeded=_amount-productStock;
            for(Map.Entry<Product,Integer> entry : recipe.getRecipe().entrySet()){
                Product component=entry.getKey();
                Integer componentAmount=entry.getValue();
                Integer componentAmountNeeded=amountNeeded*componentAmount;
                price+=(1+aggravation)*component.accept(new Sell(componentAmountNeeded));
            }
            product.updateMaxPrice(price/amountNeeded);
            price+=sellProduct(product,productStock);
        }
        else{
            price+=sellProduct(product,_amount);
        }
        return price;
    } 
}