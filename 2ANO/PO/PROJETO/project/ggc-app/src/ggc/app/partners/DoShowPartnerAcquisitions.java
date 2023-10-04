package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownPartnerKeyException;
//FIXME import classes
import ggc.exceptions.InvalidPartnerKeyException;

/**
 * Show all transactions for a specific partner.
 */
class DoShowPartnerAcquisitions extends Command<WarehouseManager> {

  DoShowPartnerAcquisitions(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER_ACQUISITIONS, receiver);
    addStringField("id",Prompt.partnerKey());
    //FIXME add command fields
  }

  @Override
  public void execute() throws CommandException {
    String id=stringField("id");
    try{
      _display.popup(_receiver.displayPartnerPurchases(id));
    }
    catch(InvalidPartnerKeyException e){
      throw new UnknownPartnerKeyException(id);
    }
    //FIXME implement command 
  }

}
