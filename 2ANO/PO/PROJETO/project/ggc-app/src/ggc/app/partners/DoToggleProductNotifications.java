package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.exceptions.InvalidPartnerKeyException;
//FIXME import classes
import ggc.exceptions.InvalidProductKeyException;

/**
 * Toggle product-related notifications.
 */
class DoToggleProductNotifications extends Command<WarehouseManager> {

  DoToggleProductNotifications(WarehouseManager receiver) {
    super(Label.TOGGLE_PRODUCT_NOTIFICATIONS, receiver);
    addStringField("idPartner",Prompt.partnerKey());
    addStringField("idProduct",Prompt.productKey());
    //FIXME add command fields
  }

  @Override
  public void execute() throws CommandException {
    String idPartner=stringField("idPartner");
    String idProduct=stringField("idProduct");
    try{
      _receiver.toggleNotifications(idPartner,idProduct);

    }
    catch(InvalidPartnerKeyException e){
      throw new UnknownPartnerKeyException(idPartner);
    }
    catch(InvalidProductKeyException e){
      throw new UnknownProductKeyException(idProduct);
    }
    //FIXME implement command
  }
}
