package ggc;

public class Bargain extends Notification{
    public Bargain(String idProduct, Double price){
        super(idProduct,price);
    }

    public String toString(){
        return "BARGAIN|" + super.toString();
    }
}