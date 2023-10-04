package ggc;

import java.io.Serializable;

interface NotificationSubject extends Serializable{
    public void registerObserver(NotificationObserver o);
    public void removeObserver(NotificationObserver o);
    public void notifyNew();
    public void notifyBargain();
}
