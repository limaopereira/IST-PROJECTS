package ggc.app.products;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
//FIXME import classes
import ggc.app.exceptions.UnknownPartnerKeyException;
import ggc.exceptions.InvalidPartnerKeyException;

/**
 * Show batches supplied by partner.
 */
class DoShowBatchesByPartner extends Command<WarehouseManager> {

  DoShowBatchesByPartner(WarehouseManager receiver) {
    super(Label.SHOW_BATCHES_SUPPLIED_BY_PARTNER, receiver);
    addStringField("id",Prompt.partnerKey());
    //FIXME maybe add command fields
  }

  @Override
  public final void execute() throws CommandException {
    String id=stringField("id");
    try{
      _display.popup(_receiver.displayBatchesByPartner(id));
    }
    catch(InvalidPartnerKeyException e){
      throw new UnknownPartnerKeyException(id);
    }
    //FIXME implement command
  }

}
