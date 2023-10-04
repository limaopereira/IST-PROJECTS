package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;


import javax.lang.model.util.ElementScanner14;

import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownPartnerKeyException;
//FIXME import classes
import ggc.app.exceptions.UnknownProductKeyException;
import ggc.exceptions.InvalidPartnerKeyException;

/**
 * Show partner.
 */
class DoShowPartner extends Command<WarehouseManager> {
  //private String partnerInfo;

  DoShowPartner(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER, receiver);
    addStringField("id",Prompt.partnerKey());
    //FIXME add command fields
  }

  @Override
  public void execute() throws CommandException {
    String id=stringField("id");
    try{
      _display.popup(_receiver.displayPartner(id));
    }
    catch(InvalidPartnerKeyException e){
      throw new UnknownPartnerKeyException(id);
    }
    //FIXME implement command
  }

}
