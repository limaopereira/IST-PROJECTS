package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import pt.tecnico.uilib.forms.Form;

import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.exceptions.InvalidPartnerKeyException;
import ggc.exceptions.InvalidProductKeyException;
import ggc.exceptions.InvalidComponentKeyException;
//FIXME import classes


/**
 * Register order.
 */
public class DoRegisterAcquisitionTransaction extends Command<WarehouseManager> {

  public DoRegisterAcquisitionTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_ACQUISITION_TRANSACTION, receiver);
    addStringField("idPartner",Prompt.partnerKey());
    addStringField("idProduct",Prompt.productKey());
    addRealField("price",Prompt.price());
    addIntegerField("amount",Prompt.amount());
    //FIXME maybe add command fields
  }

  @Override
  public final void execute() throws CommandException {
    String idPartner=stringField("idPartner");
    String idProduct=stringField("idProduct");
    Double price=realField("price");
    Integer amount=integerField("amount");
    try{
      _receiver.registerKnownProductPurchase(idPartner,idProduct,price,amount);
    }
    catch(InvalidPartnerKeyException e){
      throw new UnknownPartnerKeyException(idPartner);
    }
    catch(InvalidProductKeyException e){
      boolean isDerivedProduct=Form.confirm(Prompt.addRecipe());
      if(isDerivedProduct){
        Integer numberOfComponents=Form.requestInteger(Prompt.numberOfComponents());
        Double aggravation=Form.requestReal(Prompt.alpha());
        String recipeText="";
        for(int i=0;i<numberOfComponents;i++){
          String idComponent=Form.requestString(Prompt.productKey());
          Integer amountComponent=Form.requestInteger(Prompt.amount());
          if(i==0)
            recipeText+=idComponent+":"+amountComponent;
          else
            recipeText+="#"+idComponent+":"+amountComponent;
        }
        try{
          _receiver.registerDerivedBatch(idProduct,idPartner,price,amount,aggravation,recipeText);
        }
        catch(InvalidComponentKeyException e2){
          throw new UnknownProductKeyException(e2.getComponentId());
        }
      }
      else{
      _receiver.registerSimpleBatch(idProduct,idPartner,price,amount);
      }
      _receiver.registerUnknownProductPurchase(idPartner,idProduct,price,amount);
    }
    //FIXME implement command
  }
}
