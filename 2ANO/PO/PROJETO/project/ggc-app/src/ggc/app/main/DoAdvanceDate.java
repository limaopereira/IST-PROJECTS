package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import ggc.app.exceptions.InvalidDateException;
import ggc.exceptions.NotPositiveDateException;
import ggc.WarehouseManager;
//FIXME import classes

/**
 * Advance current date.
 */
class DoAdvanceDate extends Command<WarehouseManager> {

  DoAdvanceDate(WarehouseManager receiver) {
    super(Label.ADVANCE_DATE, receiver);
    addIntegerField("days",Prompt.daysToAdvance());
    //FIXME add command fields
  }

  @Override
  public final void execute() throws CommandException {
    int days=integerField("days");
    try{
      _receiver.advanceDate(days);
    }
    catch(NotPositiveDateException e){
      throw new InvalidDateException(days);
    }
    //FIXME implement command
  }

}
