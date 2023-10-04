package ggc;

import java.io.Serializable;
import java.util.Comparator;

public class BatchComparator implements Comparator<Batch>, Serializable{
    CollatorWrapper _collatorwrapper=new CollatorWrapper();
    public int compare(Batch b1, Batch b2){
        if(b1.getIdProduct().equalsIgnoreCase(b2.getIdProduct())){
            if(b1.getIdPartner().equalsIgnoreCase(b2.getIdPartner())){
                if(Double.compare(b1.getPrice(),b2.getPrice())==0){
                    return b1.getStock()-b2.getStock();
                }
                return (int)(b1.getPrice()-b2.getPrice());
            }
            return _collatorwrapper.compare(b1.getIdPartner(),b2.getIdPartner());
        }
        return _collatorwrapper.compare(b1.getIdProduct(),b2.getIdProduct());
    }    

}
