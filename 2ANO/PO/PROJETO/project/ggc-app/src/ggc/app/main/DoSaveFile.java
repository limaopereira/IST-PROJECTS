package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import pt.tecnico.uilib.forms.Form;

import java.io.FileNotFoundException;
import java.io.IOException;

import ggc.WarehouseManager;
import ggc.app.exceptions.FileOpenFailedException;
import ggc.exceptions.MissingFileAssociationException;
//FIXME import classes

/**
 * Save current state to file under current name (if unnamed, query for name).
 */
class DoSaveFile extends Command<WarehouseManager> {

  /** @param receiver */
  DoSaveFile(WarehouseManager receiver) {
    super(Label.SAVE, receiver);
    //FIXME maybe add command fields
  }

  @Override
  public final void execute() throws CommandException{
    try{
      _receiver.save();
    //FIXME implement command
    }
    catch(IOException e){
      e.printStackTrace();
    }
    catch(MissingFileAssociationException e){
      String file=Form.requestString(Prompt.newSaveAs());
      try{
      _receiver.saveAs(file);
      }
      catch(IOException e2){
        e2.printStackTrace();
      }
      catch(MissingFileAssociationException e2){
        throw new FileOpenFailedException(file);
      }
    }
  }

}
