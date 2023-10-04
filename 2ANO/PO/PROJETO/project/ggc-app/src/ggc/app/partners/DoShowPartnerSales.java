package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

import ggc.exceptions.InvalidPartnerKeyException;
import ggc.app.exceptions.UnknownPartnerKeyException;
//FIXME import classes

/**
 * Show all transactions for a specific partner.
 */
class DoShowPartnerSales extends Command<WarehouseManager> {

  DoShowPartnerSales(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER_SALES, receiver);
    addStringField("id",Prompt.partnerKey());
    //FIXME add command fields
  }

  @Override
  public void execute() throws CommandException {
    String id=stringField("id");
    try{
      _display.popup(_receiver.displayPartnerSales(id));
    }
    catch(InvalidPartnerKeyException e){
      throw new UnknownPartnerKeyException(id);
    }
    //FIXME implement command
  }

}
