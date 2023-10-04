package ggc;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutputStream.PutField;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import ggc.exceptions.InvalidPartnerKeyException;
import ggc.exceptions.InvalidProductKeyException;
import ggc.exceptions.InvalidComponentKeyException;
import ggc.exceptions.InvalidTransactionKeyException;
import ggc.exceptions.InvalidProductAmountException;
import ggc.exceptions.BadEntryException;
import ggc.exceptions.MissingFileAssociationException;
import ggc.exceptions.NotPositiveDateException;
import ggc.exceptions.UnavailableFileException;
import ggc.exceptions.ImportFileException;

/** Façade for access. */
public class WarehouseManager {

  /** Name of file storing current store. */
  private String _filename = "";

  /** The warehouse itself. */
  private Warehouse _warehouse = new Warehouse();


  /**
   * @@throws IOException
   * @@throws FileNotFoundException
   * @@throws MissingFileAssociationException
   */
  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    if(!hasFilename())
      throw new MissingFileAssociationException();
    ObjectOutputStream out=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(_filename)));
    out.writeObject(_warehouse);
    out.close();
  }

  /**
   * @@param filename
   * @@throws MissingFileAssociationException
   * @@throws IOException
   * @@throws FileNotFoundException
   */
  public void saveAs(String filename) throws MissingFileAssociationException, FileNotFoundException, IOException {
    if(filename.equals(""))
      throw new MissingFileAssociationException();
    _filename = filename;
    save();
  }

  /**
   * @@param filename
   * @@throws UnavailableFileException
   */
  public void load(String filename) throws UnavailableFileException, FileNotFoundException, IOException, ClassNotFoundException{
    ObjectInputStream in=new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)));
    _warehouse=(Warehouse)in.readObject();
    _filename=filename;
    in.close();
  }

  /**
   * @param textfile
   * @throws ImportFileException
   */
  public void importFile(String textfile) throws ImportFileException {
    try {
	    _warehouse.importFile(textfile);
    }
    catch (IOException | BadEntryException /* FIXME maybe other exceptions */ e) {
	    throw new ImportFileException(textfile);
    }
    catch(InvalidPartnerKeyException e){
      e.printStackTrace();
    }
  }
 

  //Partner methods -------------------------------------------------------------------------------


  public void registerPartner(String id, String name, String address) throws InvalidPartnerKeyException{
    _warehouse.registerPartner(id, name, address);
  }
  

  public String displayPartner(String id) throws InvalidPartnerKeyException{
    return _warehouse.displayPartner(id);
  }


  public Collection<Partner> displayPartners(){
    return _warehouse.displayPartners();
  }


  public Collection<Transaction> displayPartnerPurchases(String id) throws InvalidPartnerKeyException{
    return _warehouse.displayPartnerPurchases(id);
  }


  public Collection<Transaction> displayPartnerSales(String id) throws InvalidPartnerKeyException{
    return _warehouse.displayPartnerSales(id);
  }


  public Collection<Transaction> displayPartnerPaidTransactions(String id) throws InvalidPartnerKeyException{
    return _warehouse.displayPartnerPaidTransactions(id);
  }


  public boolean containsPartnerKey(String id){
    return _warehouse.containsPartnerKey(id);
  }


  //Product methods -------------------------------------------------------------------------------


  public Collection<Product> displayProducts(){
    return _warehouse.displayProducts();
  }


  public boolean containsProductKey(String id){
    return _warehouse.containsProductKey(id);
  }
 

  //Batch methods ---------------------------------------------------------------------------------


  public void registerSimpleBatch(String idProduct, String idPartner, Double price, int stock){
    _warehouse.registerSimpleBatch(idProduct, idPartner, price, stock);
  }


  public void registerDerivedBatch(String idProduct, String idPartner, Double price, int stock, Double aggravation, String recipeText) throws InvalidComponentKeyException{
    _warehouse.registerDerivedBatch(idProduct, idPartner, price, stock, aggravation, recipeText);
  }

  public Collection<Batch> displayBatches(){
    return _warehouse.displayBatches();
  }
  

  public Collection<Batch> displayBatchesByPartner(String id) throws InvalidPartnerKeyException{
    return _warehouse.displayBatchesByPartner(id);
  }


  public Collection<Batch> displayBatchesByProduct(String id) throws InvalidProductKeyException{
    return _warehouse.displayBatchesByProduct(id);
  }

  public Collection<Batch> displayBatchesByPrice(Double priceLimit){
    return _warehouse.displayBatchesByPrice(priceLimit);
  }
  

  //Transactions methods --------------------------------------------------------------------------


  public void registerKnownProductPurchase(String idPartner, String idProduct, Double price, int amount) throws InvalidPartnerKeyException, InvalidProductKeyException{
    _warehouse.registerKnownProductPurchase(idPartner, idProduct, price, amount);
  }

  public void registerUnknownProductPurchase(String idPartner, String idProduct, Double price, int amount){
    _warehouse.registerUnknownProductPurchase(idPartner, idProduct, price, amount);
  }

  public void registerSale(String idPartner, String idProduct, int amount, int deadline) throws InvalidPartnerKeyException, InvalidProductKeyException, InvalidProductAmountException{
    _warehouse.registerSale(idPartner, idProduct, amount, deadline);
  }

  
  public void registerDisaggregation(String idPartner, String idProduct, int amount) throws InvalidProductKeyException,InvalidPartnerKeyException,InvalidProductAmountException{
    _warehouse.registerDisaggregation(idPartner, idProduct, amount);
  }
  

  public String displayTransaction(Integer id) throws InvalidTransactionKeyException{
    return _warehouse.displayTransaction(id);
  }


  public void paySale(Integer id) throws InvalidTransactionKeyException{
    _warehouse.paySale(id);
  }


  //Notification methods --------------------------------------------------------------------------


  public void toggleNotifications(String idPartner, String idProduct) throws InvalidPartnerKeyException,InvalidProductKeyException{
    _warehouse.toggleNotifications(idPartner, idProduct);
  }


  //Date methods ----------------------------------------------------------------------------------


  public void advanceDate(int days) throws NotPositiveDateException{
    _warehouse.advanceDate(days);
  }

  public int getDate(){
    return _warehouse.getDate();
  }

  
  //Miscellaneous methods -------------------------------------------------------------------------


  public boolean hasFilename(){
    return !_filename.equals("");
  }

  public Double getBalance(){
    return _warehouse.getBalance();
  }

  public Double getAccountingBalance(){
    return _warehouse.getAccountingBalance();
  }
}
