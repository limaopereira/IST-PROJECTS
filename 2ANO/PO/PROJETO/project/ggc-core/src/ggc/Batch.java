package ggc;

import java.io.Serializable;

public class Batch implements Serializable, Comparable<Batch>{
    private String _idProduct;
    private String _idPartner;
    private Double _price;
    private int _stock;
    
    public Batch(String idProduct, String idPartner, Double price, int stock){
        _idProduct=idProduct;
        _idPartner=idPartner;
        _stock=stock;
        _price=price;
    }

    public String getIdProduct(){
        return _idProduct;
    }

    public String getIdPartner(){
        return _idPartner;
    }

    public Double getPrice(){
        return _price;
    }

    public int getStock(){
        return _stock;
    }

    public void setStock(int stock){
        _stock=stock;
    }

    public int compareTo(Batch other){
        return Double.compare(_price,other.getPrice());
    }

    public String toString(){
        return _idProduct + "|" + _idPartner + "|" + (int)Math.round(_price) + "|" + _stock;
    }
}
