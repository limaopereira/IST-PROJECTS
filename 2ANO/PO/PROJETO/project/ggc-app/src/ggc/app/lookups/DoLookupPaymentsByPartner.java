package ggc.app.lookups;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;

import ggc.exceptions.InvalidPartnerKeyException;
import ggc.app.exceptions.UnknownPartnerKeyException;
//FIXME import classes

/**
 * Lookup payments by given partner.
 */
public class DoLookupPaymentsByPartner extends Command<WarehouseManager> {

  public DoLookupPaymentsByPartner(WarehouseManager receiver) {
    super(Label.PAID_BY_PARTNER, receiver);
    addStringField("id",Prompt.partnerKey());
    //FIXME add command fields
  }

  @Override
  public void execute() throws CommandException {
    String id=stringField("id");
    try{
      _display.popup(_receiver.displayPartnerPaidTransactions(id));
    }
    catch(InvalidPartnerKeyException e){
      throw new UnknownPartnerKeyException(id);
    }
    //FIXME implement command
  }

}
