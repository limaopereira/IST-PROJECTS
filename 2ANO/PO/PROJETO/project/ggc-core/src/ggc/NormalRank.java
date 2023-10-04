package ggc;


public class NormalRank extends RankState{

    public NormalRank(Partner partner){
        super(partner);
    }

    public void nextRank(){
        if(_partner.getPoints()>25000)
            _partner.setRank(new EliteRank(_partner));
        else if(_partner.getPoints()>2000)
            _partner.setRank(new SelectionRank(_partner));
    }

    public Double previousRank(int diffDate){
        return _partner.getPoints();
    }
   
    public Double calculatePrice(int diffDate,int offset, Double price){
        if(diffDate>=offset)
            return price*0.9;
        else if(diffDate>=0)
            return price;
        else if(-diffDate<=offset)
            return price*(1+(0.05*(-diffDate)));
        else
            return price*(1+(0.1*(-diffDate)));
       
    }

    public String toString(){
        return "NORMAL";
    }
}