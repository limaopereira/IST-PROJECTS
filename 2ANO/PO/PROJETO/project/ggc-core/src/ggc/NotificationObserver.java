package ggc;

import java.io.Serializable;

interface NotificationObserver extends Serializable{
    public void updateBargain(String idProduct, Double price, NotificationDelivery delivery);
    public void updateNew(String idProduct, Double price, NotificationDelivery delivery);
}
