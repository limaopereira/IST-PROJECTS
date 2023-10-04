package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnavailableProductException;
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.app.exceptions.UnknownPartnerKeyException;
//FIXME import classes
import  ggc.exceptions.InvalidProductKeyException;
import ggc.exceptions.InvalidPartnerKeyException;
import ggc.exceptions.InvalidProductAmountException;

/**
 * Register order.
 */
public class DoRegisterBreakdownTransaction extends Command<WarehouseManager> {

  public DoRegisterBreakdownTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_BREAKDOWN_TRANSACTION, receiver);
    addStringField("idPartner",Prompt.partnerKey());
    addStringField("idProduct",Prompt.productKey());
    addIntegerField("amount",Prompt.amount());
    //FIXME maybe add command fields
  }

  @Override
  public final void execute() throws CommandException {
    String idPartner=stringField("idPartner");
    String idProduct=stringField("idProduct");
    Integer amount=integerField("amount");
    try{
      _receiver.registerDisaggregation(idPartner,idProduct,amount);
    }
    catch(InvalidProductKeyException e){
      throw new UnknownProductKeyException(idProduct);
    }
    catch(InvalidPartnerKeyException e){
      throw new UnknownPartnerKeyException(idPartner);
    }
    catch(InvalidProductAmountException e){
      throw new UnavailableProductException(e.getIdProduct(), e.getAmountNeeded(), e.getAmountAvailable());
    }
    //FIXME implement command
  }

}
