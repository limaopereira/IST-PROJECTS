package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import java.io.FileNotFoundException;
import java.io.IOException;

import ggc.WarehouseManager;
import ggc.exceptions.UnavailableFileException;
import ggc.app.exceptions.FileOpenFailedException;

//FIXME import classes


/**
 * Open existing saved state.
 */
class DoOpenFile extends Command<WarehouseManager> {

  /** @param receiver */
  DoOpenFile(WarehouseManager receiver) {
    super(Label.OPEN, receiver);
    addStringField("file",Prompt.openFile());
    //FIXME maybe add command fields
  }

  @Override
  public final void execute() throws CommandException {
    String file=stringField("file");
    try{
      _receiver.load(file);
    }
    catch(UnavailableFileException e){
      throw new FileOpenFailedException(e.getFilename());
    }
    catch(FileNotFoundException e){
      throw new FileOpenFailedException(file);
    }
    catch(IOException e){
      e.printStackTrace();
    }
    catch(ClassNotFoundException e){
      e.printStackTrace();
    }
    /*
    try {
      //FIXME implement command
    } catch (UnavailableFileException ufe) {
      throw new FileOpenFailedException(ufe.getFilename());
    }
    */
  }
}
