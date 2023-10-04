package ggc;

import java.io.Serializable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import ggc.exceptions.InvalidProductAmountException;

public abstract class Product implements Serializable, NotificationSubject, Comparable<Product>{
    private String _id;
    private Double _maxPrice;
    private int _deadlineOffset;
    private Queue<Batch> _batches=new PriorityQueue<Batch>();
    private List<NotificationObserver> _observers=new ArrayList<NotificationObserver>();
    private NotificationDelivery _delivery=new AppDelivery();

    public Product(String id, Double maxPrice,int deadlineOffset){
        _id=id;
        _maxPrice=maxPrice;
        _deadlineOffset=deadlineOffset;
    }

    public abstract Double accept(Sell sell);
    public abstract Disaggregation accept(Disaggregate disaggregate) throws InvalidProductAmountException;
    public abstract Map<Product,Integer> accept(HasAvailableStock hasAvailableStock) throws InvalidProductAmountException;
    
    public String getId(){
        return _id;
    }

    public Double getMinPrice(){
        Double minPrice=_maxPrice;
        for(Batch batch : _batches)
            if(batch.getPrice()<minPrice)
                minPrice=batch.getPrice();
        return minPrice;
    }

    public Double getMaxPrice(){
        return _maxPrice;
    }

    public int getTotalStock(){
        int totalStock=0;
        for(Batch batch : _batches)
            totalStock+=batch.getStock();
        return totalStock;
    }

    public int getDeadlineOffset(){
        return _deadlineOffset;
    }

    public Queue<Batch> getBatches(){
        return _batches;
    }

    public Batch[] getSortedBatches(){
        Batch[] sortedBatches=_batches.toArray(new Batch[_batches.size()]);
        Arrays.sort(sortedBatches,new BatchComparator());
        return sortedBatches;
    }

    public int getBatchesNumber(){
        return _batches.size();
    }


    public void addBatch(Batch batch){
        if(_maxPrice<batch.getPrice())
            _maxPrice=batch.getPrice();
        _batches.add(batch);
    }

    public void removeBatch(Batch batch){
        _batches.remove(batch);
    }

    public void updateMaxPrice(Double price){
        if(price>_maxPrice)
            _maxPrice=price;
    }

    public void registerObserver(NotificationObserver observer){
        _observers.add(observer);
    }

    public void removeObserver(NotificationObserver observer){
        int i=_observers.indexOf(observer);
        if(i>=0)
            _observers.remove(i);
    }

    public void toggleNotifications(NotificationObserver observer){
        if(_observers.contains(observer))
            removeObserver(observer);
        else
            registerObserver(observer);
    }

    public void notifyNew(){
        for(NotificationObserver observer : _observers)
            observer.updateNew(_id,getMinPrice(),_delivery);
    }

    public void notifyBargain(){
        for(NotificationObserver observer: _observers)
            observer.updateBargain(_id,getMinPrice(),_delivery);
    }

    public int compareTo(Product other){
        CollatorWrapper _collatorwrapper=new CollatorWrapper();
        return _collatorwrapper.compare(_id,other.getId());
    }

    public String toString(){
        return _id + "|" + (int)Math.round(_maxPrice) + "|" + getTotalStock();
    }

}
