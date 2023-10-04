package ggc;

public class SelectionRank extends RankState{
    public SelectionRank(Partner partner){
        super(partner);
    }

    public void nextRank(){
        if(_partner.getPoints()>25000)
            _partner.setRank(new EliteRank(_partner));
    }

    public Double previousRank(int diffDate){
        if(-diffDate>2){
            _partner.setRank(new NormalRank(_partner));
            return _partner.getPoints()*0.9;
        }
        return 0.0;
    }

    public Double calculatePrice(int diffDate,int offset, Double price){
        if(diffDate>=offset)
            return price*0.9;
        else if(diffDate>=2)
            return price*0.95;
        else if(-diffDate<=1)
            return price;
        else if(-diffDate<=offset)
            return price*(1+(0.02*(-diffDate)));
        else
            return price*(1+(0.05*(-diffDate)));
    }

    public String toString(){
        return "SELECTION";
    }
}
