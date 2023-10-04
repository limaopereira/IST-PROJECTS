package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnavailableProductException;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.exceptions.InvalidPartnerKeyException;
//FIXME import classes
import ggc.exceptions.InvalidProductAmountException;
import ggc.exceptions.InvalidProductKeyException;

/**
 * 
 */
public class DoRegisterSaleTransaction extends Command<WarehouseManager> {

  public DoRegisterSaleTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    addStringField("idPartner",Prompt.partnerKey());
    addIntegerField("deadline",Prompt.paymentDeadline());
    addStringField("idProduct",Prompt.productKey());
    addIntegerField("amount",Prompt.amount());
    //FIXME maybe add command fields 
  }

  @Override
  public final void execute() throws CommandException {
    String idPartner=stringField("idPartner");
    Integer deadline=integerField("deadline");
    String idProduct=stringField("idProduct");
    Integer amount=integerField("amount");

    try{
      _receiver.registerSale(idPartner,idProduct,amount,deadline);
    }
    catch(InvalidPartnerKeyException e){
      throw new UnknownPartnerKeyException(idPartner);
    }
    catch(InvalidProductKeyException e){
      throw new UnknownProductKeyException(idProduct);
    }
    catch(InvalidProductAmountException e){
      throw new UnavailableProductException(e.getIdProduct(), e.getAmountNeeded(), e.getAmountAvailable());
    }
    //FIXME implement command
  }
}
