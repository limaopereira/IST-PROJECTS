package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import ggc.app.exceptions.DuplicatePartnerKeyException;
import ggc.exceptions.InvalidPartnerKeyException;
import ggc.WarehouseManager;
//FIXME import classes

/**
 * Register new partner.
 */
class DoRegisterPartner extends Command<WarehouseManager> {

  DoRegisterPartner(WarehouseManager receiver) {
    super(Label.REGISTER_PARTNER, receiver);
    addStringField("id",Prompt.partnerKey());
    addStringField("name",Prompt.partnerName());
    addStringField("address",Prompt.partnerAddress());
    //FIXME add command fields
  }

  @Override
  public void execute() throws CommandException {
    String id=stringField("id");
    String name=stringField("name");
    String address=stringField("address");
    try{
      _receiver.registerPartner(id,name,address);
    }
    catch(InvalidPartnerKeyException e){
      throw new DuplicatePartnerKeyException(id);
    }
    //FIXME implement command
  }

}
