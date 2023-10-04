package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.exceptions.InvalidTransactionKeyException;
import ggc.app.exceptions.UnknownTransactionKeyException;
//FIXME import classes

/**
 * Show specific transaction.
 */
public class DoShowTransaction extends Command<WarehouseManager> {

  public DoShowTransaction(WarehouseManager receiver) {
    super(Label.SHOW_TRANSACTION, receiver);
    addIntegerField("id",Prompt.transactionKey());
    //FIXME maybe add command fields
  }

  @Override
  public final void execute() throws CommandException {
    Integer id=integerField("id");
    try{
      _display.popup(_receiver.displayTransaction(id));
    }
    catch(InvalidTransactionKeyException e){
      throw new UnknownTransactionKeyException(id);
    }
    //FIXME implement command
  }

}
