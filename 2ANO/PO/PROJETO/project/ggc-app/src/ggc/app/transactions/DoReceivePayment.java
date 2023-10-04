package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.WarehouseManager;
import ggc.app.exceptions.UnknownTransactionKeyException;
//FIXME import classes
import ggc.exceptions.InvalidTransactionKeyException;

/**
 * Receive payment for sale transaction.
 */
public class DoReceivePayment extends Command<WarehouseManager> {

  public DoReceivePayment(WarehouseManager receiver) {
    super(Label.RECEIVE_PAYMENT, receiver);
    addIntegerField("id",Prompt.transactionKey());
    //FIXME add command fields
  }

  @Override
  public final void execute() throws CommandException {
    Integer id=integerField("id");
    try{
      _receiver.paySale(id);
    }
    catch(InvalidTransactionKeyException e){
      throw new UnknownTransactionKeyException(id);
    }
    //FIXME implement command
  }
  

}
