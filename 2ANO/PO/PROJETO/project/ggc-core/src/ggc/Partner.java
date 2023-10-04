package ggc;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Partner implements Serializable, NotificationObserver{
    private String _id;
    private String _name;
    private String _address;
    private Double _points;
    private Double _totalPerformed;
    private Double _totalPaid;
    private Double _totalSold;
    private RankState _rank=new NormalRank(this);
    private List<Notification> _notifications=new ArrayList<Notification>();

    public Partner(String id, String name, String address){
        _id=id;
        _name=name;
        _address=address;
        _points=0.0;
        _totalPerformed=0.0;
        _totalPaid=0.0;
        _totalSold=0.0;
    }

    public String getId(){
        return _id;
    }

    public RankState getRank(){
        return _rank;
    }

    public void setTotalPerformed(Double totalPerformed){
        _totalPerformed+=totalPerformed;
    }

    public void setTotalSold(Double totalSold){
        _totalSold+=totalSold;
    }

    public Double calculatePrice(int diffDate, int offset, Double price){
        return _rank.calculatePrice(diffDate,offset,price);
    }

    public Double pay(int diffDate, int offset, Double price){
        Double pricePaid=_rank.calculatePrice(diffDate,offset,price);
        setPoints(diffDate,pricePaid);
        _totalPaid+=pricePaid;
        return pricePaid;
    }

    public Double getPoints(){
        return _points;
    }

    public void setPoints(int diffDate,Double pricePaid){
        if(diffDate>=0){
            _points+=pricePaid*10;
            _rank.nextRank();
        }
        else
            _points-=_rank.previousRank(diffDate);
    }

    public void setRank(RankState state){
        _rank=state;
    }
    
    public void updateNew(String idProduct, Double price, NotificationDelivery delivery){
        Notification notification=new New(idProduct,price);
        delivery.deliver(this,notification);
    }

    public void updateBargain(String idProduct, Double price, NotificationDelivery delivery){
        Notification notification=new Bargain(idProduct,price);
        delivery.deliver(this,notification);
    }

    public List<Notification> getNotifications(){
        return _notifications;
    }

    public void wipeNotifications(){
        _notifications=new ArrayList<Notification>();
    }

    public String toString(){
        return _id + "|" + _name + "|" + _address + "|" + _rank + "|" + (int)Math.round(_points) + "|" +
         (int)Math.round(_totalSold) + "|" + (int)Math.round((_totalPerformed)) + "|" + (int)Math.round(_totalPaid);
    }
}