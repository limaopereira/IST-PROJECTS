package ggc;

import java.io.Serializable;

public abstract class NotificationDelivery implements Serializable{
    public abstract void deliver(Partner partner, Notification notification);
}
