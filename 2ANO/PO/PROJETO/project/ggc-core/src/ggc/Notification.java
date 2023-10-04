package ggc;

import java.io.Serializable;

public abstract class Notification implements Serializable{
    private String _idProduct;
    private Double _price;

    public Notification(String idProduct, Double price){
        _idProduct=idProduct;
        _price=price;   
    }
    
    public String toString(){
        return _idProduct + "|" + (int)Math.round(_price);
    }
}
