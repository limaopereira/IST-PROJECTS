package ggc.app.products;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.InvalidProductKeyException;

import ggc.app.exceptions.UnknownProductKeyException;
//FIXME import classes


/**
 * Show all products.
 */
class DoShowBatchesByProduct extends Command<WarehouseManager> {

  DoShowBatchesByProduct(WarehouseManager receiver) {
    super(Label.SHOW_BATCHES_BY_PRODUCT, receiver);
    addStringField("id",Prompt.productKey());
    //FIXME maybe add command fields
  }

  @Override
  public final void execute() throws CommandException {
    String id=stringField("id");
    try{
      _display.popup(_receiver.displayBatchesByProduct(id));
    }
    catch(InvalidProductKeyException e){
      throw new UnknownProductKeyException(id);
    }
    //FIXME implement command
  }

}
