package ggc;

public class New extends Notification{
    public New(String idProduct, Double price){
        super(idProduct,price);
    }

    public String toString(){
        return "NEW|" + super.toString();
    }
}
