package ggc;

import java.io.Serializable;

public abstract class RankState implements Serializable{
    protected Partner _partner;

    public RankState(Partner partner){
        _partner=partner;
    }

    public abstract void nextRank();
    public abstract Double previousRank(int diffDate);
    public abstract Double calculatePrice(int diffDate,int offstet,Double price); 

}