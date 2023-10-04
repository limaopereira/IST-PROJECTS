package ggc;

import ggc.exceptions.InvalidProductAmountException;
import java.util.Map;
import java.util.HashMap;

public class HasAvailableStock{
    private int _amount;
    private Map<Product,Integer> _list;

    public HasAvailableStock(int amount,Map<Product,Integer> list){
        _amount=amount;
        _list=list;
    }

    public Map<Product,Integer> visitSimpleProduct(SimpleProduct product) throws InvalidProductAmountException{
        if(product.getTotalStock()<_amount){
            throw new InvalidProductAmountException(product.getId(), _amount, product.getTotalStock());
        }
        if(_list.get(product)!=null)
            _list.put(product,_list.get(product)+_amount);
        else
            _list.put(product,_amount);
        return _list;
    }

    public Map<Product,Integer> visitDerivedProduct(DerivedProduct product) throws InvalidProductAmountException{
        if(product.getTotalStock()<_amount){
            Recipe recipe=product.getRecipe();
            int productStock=product.getTotalStock();
            int amountNeeded=_amount-productStock;
            for(Map.Entry<Product,Integer> entry : recipe.getRecipe().entrySet()){
                Product component=entry.getKey();
                Integer componentAmount=entry.getValue();
                Integer componentAmountNeeded=amountNeeded*componentAmount;
                component.accept(new HasAvailableStock(componentAmountNeeded,_list));                   
            }
        }
        else{
            if(_list.get(product)!=null)
                _list.put(product,_list.get(product)+_amount);
            else
                _list.put(product,_amount);
        }
        return _list;
    }
}
