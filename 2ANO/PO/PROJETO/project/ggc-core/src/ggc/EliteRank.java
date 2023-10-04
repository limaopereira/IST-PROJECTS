package ggc;


public class EliteRank extends RankState{
    public EliteRank(Partner partner){
        super(partner);
    }

    public void nextRank(){};

    public Double previousRank(int diffDate){
        if(-diffDate>15){
            _partner.setRank(new SelectionRank(_partner));
            return _partner.getPoints()*0.75;
        }
        return 0.0;
    }

    public Double calculatePrice(int diffDate,int offset, Double price){
        if(diffDate>=0)
            return price*0.9;
        else if(-diffDate<=offset)
            return price*0.95;
        else
            return price;
    }
   
    public String toString(){
        return "ELITE";
    }
    
}
