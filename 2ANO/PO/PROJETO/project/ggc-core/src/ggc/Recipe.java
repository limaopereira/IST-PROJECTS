package ggc;

import java.io.Serializable;
import java.util.Map;
import java.util.LinkedHashMap;

public class Recipe implements Serializable{
    private Double _aggravation;
    private String _recipeText;
    private Map<Product,Integer> _recipe;

    public Recipe(Double aggravation){
        _aggravation=aggravation;
        _recipeText="";
        _recipe=new LinkedHashMap<Product,Integer>();
    }
    
    public Recipe(Double aggravation, String recipeText){
        this(aggravation);
        _recipeText=recipeText;
    }

    public Double getAggravation(){
        return _aggravation;
    }

    public void put(Product product, Integer quantity){
        _recipe.put(product,quantity);
    }

    public Integer get(Product product){
        return _recipe.get(product);
    }

    public Map<Product,Integer> getRecipe(){
        return _recipe;
    }

    public String toString(){
        return _aggravation + "|" + _recipeText;
    }
} 
