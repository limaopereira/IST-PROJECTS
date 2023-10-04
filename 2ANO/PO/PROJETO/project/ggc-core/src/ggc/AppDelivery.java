package ggc;

import java.io.Serializable;

public class AppDelivery extends NotificationDelivery{
    public void deliver(Partner partner,Notification notification){
        partner.getNotifications().add(notification);
    }
}
