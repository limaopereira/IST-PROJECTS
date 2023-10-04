package ggc.app.lookups;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
//FIXME import classes

/**
 * Lookup products cheaper than a given price.
 */
public class DoLookupProductBatchesUnderGivenPrice extends Command<WarehouseManager> {

  public DoLookupProductBatchesUnderGivenPrice(WarehouseManager receiver) {
    super(Label.PRODUCTS_UNDER_PRICE, receiver);
    addRealField("priceLimit",Prompt.priceLimit());
    //FIXME add command fields
  }

  @Override
  public void execute() throws CommandException {
    Double priceLimit=realField("priceLimit");
    _display.popup(_receiver.displayBatchesByPrice(priceLimit));
    //FIXME implement command
  }

}
