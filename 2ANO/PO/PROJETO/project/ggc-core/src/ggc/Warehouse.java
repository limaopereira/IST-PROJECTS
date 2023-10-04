package ggc;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.management.BufferPoolMXBean;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import ggc.exceptions.InvalidPartnerKeyException;
import ggc.exceptions.InvalidProductKeyException;
import ggc.exceptions.InvalidComponentKeyException;
import ggc.exceptions.InvalidProductAmountException;
import ggc.exceptions.InvalidTransactionKeyException;
import ggc.exceptions.NotPositiveDateException;
import ggc.exceptions.BadEntryException;


/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 202109192006L;

  /** Warehouse current balance */
  private Double _balance=0.0;

  /** Internal date counter  */
  private int _date=0;
  
  /** Current transaction id */
  private Integer _currentTransactionId=0;

  /** Warehouse partners */
  private Map<String, Partner> _partners=new TreeMap<String,Partner>(new CollatorWrapper()); 
  
  /** Warehouse products */
  private Map<String, Product> _products=new TreeMap<String,Product>(new CollatorWrapper());
  
  /**Warehouse transacations */
  private Map<Integer,Transaction> _transactions=new LinkedHashMap<Integer,Transaction>();


  /**
    * Read Partners, Simple Batches and Derived Batches from import file and adds them to warehouse
    * @param txtfile filename to be loaded.
    * @throws IOException
    * @throws BadEntryException
    * @throws InvalidPartnerKeyException
    */
  void importFile(String txtfile) throws IOException, BadEntryException,InvalidPartnerKeyException /* FIXME maybe other exceptions */ {
    //FIXME implement method
    try{
      BufferedReader reader=new BufferedReader(new FileReader(txtfile));
      String line;
      while((line=reader.readLine())!=null){
        String[] fields=line.split("\\|");
        registerFromFields(fields);
      }
      reader.close();
    }
    catch(IOException e){
      e.printStackTrace();
    }
  }


  //Miscellaneous methods -------------------------------------------------------------------------


  /**
   * Matches a pattern and register if recognized
   * @param fields parsed fields from imported file
   * @throws BadEntryException
   */
  void registerFromFields(String[] fields) throws BadEntryException, InvalidPartnerKeyException{
    Pattern partnerPattern=Pattern.compile("^(PARTNER)");
    Pattern simpleBatchPattern=Pattern.compile("^(BATCH_S)");
    Pattern derivedBatchPattern=Pattern.compile("^(BATCH_M)");

    if(partnerPattern.matcher(fields[0]).matches()){
      String id=fields[1];
      String name=fields[2];
      String address=fields[3];
      registerPartner(id,name,address);
    }
    else if(simpleBatchPattern.matcher(fields[0]).matches()){
      String idProduct=fields[1];
      String idPartner=fields[2];
      Double price=Double.parseDouble(fields[3]);
      int stock=Integer.parseInt(fields[4]);
      registerSimpleBatch(idProduct,idPartner,price,stock);
    }
    else if(derivedBatchPattern.matcher(fields[0]).matches()){
      String idProduct=fields[1];
      String idPartner=fields[2];
      Double price=Double.parseDouble(fields[3]);
      int stock=Integer.parseInt(fields[4]);
      Double aggravation=Double.parseDouble(fields[5]);
      String recipeText=fields[6];
      try{
        registerDerivedBatch(idProduct,idPartner,price,stock,aggravation,recipeText);
      }
      catch(InvalidComponentKeyException e){
        e.printStackTrace();
      }
    }
    else{
      throw new BadEntryException("Unknown entry:"+ fields[0]);
    }
  }


  /**
   * Gets the warehouse current balance
   * @return warehouse current balance
   */
  Double getBalance(){
    return _balance;
  }


  /**
   * Gets the warehouse accounting balance for the current date
   * @return warehouse accounting balance
   */
  Double getAccountingBalance(){
    Double accountingBalance=0.0;
    for(Map.Entry<Integer,Transaction> entry : _transactions.entrySet()){
      Transaction transaction=entry.getValue();
      calculatePrice(transaction);
      accountingBalance+=transaction.accept(new GetNotPaidValue());
    }
    return _balance+accountingBalance;
  }


  //Partner methods -------------------------------------------------------------------------------


  /**
   * Registers a new partner in the system
   * @param id partner id
   * @param name partner name 
   * @param address partner address
   * @throws InvalidPartnerKeyException
   */
  void registerPartner(String id, String name, String address) throws InvalidPartnerKeyException{
    if(containsPartnerKey(id))
      throw new InvalidPartnerKeyException();
    Partner partner=new Partner(id,name,address);
    // newPartnerNotifications(partner);
    _partners.put(id,partner);
    newPartnerNotifications(partner);
  }


  /**
   * Present information about id specified partner
   * @param id partner id
   * @return partner's information
   * @throws InvalidPartnerKeyException
   */
  String displayPartner(String id) throws InvalidPartnerKeyException{
    if(!containsPartnerKey(id))
      throw new InvalidPartnerKeyException();
    Partner partner=_partners.get(id);
    String display=partner.toString()+"\n";
    for(Notification notification : partner.getNotifications())
      display+=notification.toString()+"\n";
    partner.wipeNotifications();
    return display.substring(0,display.length()-1);    
  }


  /**
   * Return all the partners as an unmodifiable collection
   * @return a collection with all partners
   */
  Collection<Partner> displayPartners(){
    return Collections.unmodifiableCollection(_partners.values());
  }


  /**
   * Return all partner's purchases as an unmodifiable collection
   * @param id partner id
   * @return a collection with all partner's purchases
   * @throws InvalidPartnerKeyException
   */
  Collection<Transaction> displayPartnerPurchases(String id) throws InvalidPartnerKeyException{
    if(!containsPartnerKey(id))
      throw new InvalidPartnerKeyException();
    Predicate<Transaction> byPartner= transaction -> transaction.accept(new DisplayPartnerPurchases(id));
    List<Transaction> partnerPurchases=_transactions.values().stream().filter(byPartner).collect(Collectors.toList()); 
    return Collections.unmodifiableCollection(partnerPurchases);
  }


  /**
   * Return all partner's sales as an unmodifiable collection
   * @param id partner id
   * @return a collection with all partner's sales
   * @throws InvalidPartnerKeyException
   */
  Collection<Transaction> displayPartnerSales(String id) throws InvalidPartnerKeyException{
    if(!containsPartnerKey(id))
      throw new InvalidPartnerKeyException();
    Predicate<Transaction> byPartner= transaction -> transaction.accept(new DisplayPartnerSales(id));
    List<Transaction> partnerSales=_transactions.values().stream().filter(byPartner).collect(Collectors.toList()); 
    partnerSales.forEach(transaction -> calculatePrice(transaction));
    return Collections.unmodifiableCollection(partnerSales);
  }


  /**
   * Return all partner's paid transactions as an unmodifiable collection
   * @param id partner id
   * @return a collection with all partner's paid transactions
   * @throws InvalidPartnerKeyException
   */
  Collection<Transaction> displayPartnerPaidTransactions(String id) throws InvalidPartnerKeyException{
    if(!containsPartnerKey(id))
      throw new InvalidPartnerKeyException();
    Predicate<Transaction> byPartner= transaction -> transaction.accept(new DisplayPartnerPaidTransactions(id));
    List<Transaction> partnerPaidTransactions=_transactions.values().stream().filter(byPartner).collect(Collectors.toList()); 
    return Collections.unmodifiableCollection(partnerPaidTransactions);
  }


  /**
   * Checks if the partner id is in use in the system
   * @param id partner id
   * @return true if already in use
   */
  boolean containsPartnerKey(String id){
    return _partners.containsKey(id);
  }


  //Batch methods ---------------------------------------------------------------------------------
  

  /**
   * Registers a new simple batch in the system
   * @param idProduct product id
   * @param idPartner partner id
   * @param price batch price
   * @param stock batch stock
   */
  void registerSimpleBatch(String idProduct, String idPartner, Double price, int stock){
    if(!containsProductKey(idProduct)){
      Product product=new SimpleProduct(idProduct,price);
      _products.put(idProduct,product);
      newProductNotifications(product);
    }
    Product product=_products.get(idProduct);
    Batch batch=new Batch(idProduct,idPartner,price,stock);
    product.addBatch(batch);
  }


  /**
   * Registers a new derived batch in the system
   * @param idProduct product id
   * @param idPartner partner id
   * @param price batch price
   * @param stock batch stock 
   * @param aggravation recipe price aggravation
   * @param recipeText product recipe text
   * @throws InvalidComponentKeyException
   */
  void registerDerivedBatch(String idProduct, String idPartner, Double price, int stock, Double aggravation, String recipeText) throws InvalidComponentKeyException{
    if(!containsProductKey(idProduct)){
      Recipe recipe=parseRecipe(aggravation,recipeText);
      Product product=new DerivedProduct(idProduct, price, recipe);
      _products.put(idProduct,product);
      newProductNotifications(product);
    }
    Product product=_products.get(idProduct);
    Batch batch=new Batch(idProduct, idPartner, price, stock);
    product.addBatch(batch);
  }


  /**
   * Get all warehouse batches
   * @return an arraylist with all warehouse batches
   */
  List<Batch> getAllBatches(){
    List<Batch> batches=new ArrayList<Batch>();
    for(Map.Entry<String,Product> entry : _products.entrySet()){
      Product product=entry.getValue();
      for(Batch batch : product.getSortedBatches())
        batches.add(batch);
    }
    return batches;
  }


  /**
   * Return all batches as an unmodifiable collection
   * @return a collection with all batches
   */
  Collection<Batch> displayBatches(){
    List<Batch> batches=getAllBatches();
    return Collections.unmodifiableCollection(batches);
  }
  

  /**
   * Return all partner's batches as an unmodifiable colleciton
   * @param id partner id
   * @return a collection with all partner's batches
   * @throws InvalidPartnerKeyException
   */
  Collection<Batch> displayBatchesByPartner(String id) throws InvalidPartnerKeyException{
    if(!containsPartnerKey(id))
      throw new InvalidPartnerKeyException();
    List<Batch> batches=getAllBatches();
    Predicate<Batch> byPartner=batch -> batch.getIdPartner().equalsIgnoreCase(id);
    List<Batch> partnerBatches=batches.stream().filter(byPartner).collect(Collectors.toList());
    return Collections.unmodifiableCollection(partnerBatches);
  }


  /**
   * Return all product's batches as an unmodifiable collection
   * @param id product id
   * @return a collection with all product's batches
   * @throws InvalidProductKeyException
   */
  Collection<Batch> displayBatchesByProduct(String id) throws InvalidProductKeyException{
    if(!containsProductKey(id))
      throw new InvalidProductKeyException();
    Product product=_products.get(id);
    List<Batch> sortedBatches=Arrays.asList(product.getSortedBatches());
    return Collections.unmodifiableCollection(sortedBatches);
  }


  /**
   * Return all batches under a given price as an unmodifiable collection
   * @param priceLimit price limit
   * @return a collection with all batches under a given price
   */
  Collection<Batch> displayBatchesByPrice(Double priceLimit){
    List<Batch> batches=getAllBatches();
    Predicate<Batch> byPrice= batch -> batch.getPrice()<priceLimit;
    List<Batch> priceBatches=batches.stream().filter(byPrice).collect(Collectors.toList());
    return Collections.unmodifiableCollection(priceBatches);
  }


  //Product methods -------------------------------------------------------------------------------


  /**
   * Check if the product id is in use in the system 
   * @param id product id
   * @return true if already in use
   */
  boolean containsProductKey(String id){
    return _products.containsKey(id);
  }


  /**
   * Parses the recipe text to a recipe
   * @param aggravation recipe price aggravation
   * @param recipeText recipe text
   * @return a recipe
   * @throws InvalidComponentKeyException
   */
  Recipe parseRecipe(Double aggravation, String recipeText) throws InvalidComponentKeyException{
    Recipe recipe=new Recipe(aggravation,recipeText);
    String[] components=recipeText.split("#");
    for(String component : components){
      String[] splitedComponent=component.split(":");
      if(!containsProductKey(splitedComponent[0]))
        throw new InvalidComponentKeyException(splitedComponent[0]);
      Product product=_products.get(splitedComponent[0]);
      Integer quantity=Integer.parseInt(splitedComponent[1]);
      recipe.put(product,quantity);
    }
    return recipe;
  }


  /**
   * Return all products as an unmodifiable collection
   * @return a collection with all products
   */
  Collection<Product> displayProducts(){
    return Collections.unmodifiableCollection(_products.values());
  }


  //Transactions methods --------------------------------------------------------------------------


  /**
   * Registers a new sale in the system
   * @param idPartner partner id
   * @param idProduct product id
   * @param amount sale amount
   * @param deadline sale deadline
   * @throws InvalidPartnerKeyException
   * @throws InvalidProductKeyException
   * @throws InvalidProductAmountException
   */
  void registerSale(String idPartner, String idProduct, int amount, int deadline) throws InvalidPartnerKeyException, InvalidProductKeyException, InvalidProductAmountException{
    if(!containsPartnerKey(idPartner))
      throw new InvalidPartnerKeyException();
    if(!containsProductKey(idProduct))
      throw new InvalidProductKeyException();
    Product product=_products.get(idProduct);
    Partner partner=_partners.get(idPartner);
    Map<Product,Integer> initialLinked=new LinkedHashMap<Product,Integer>();
    Map<Product,Integer> finalLinked=product.accept(new HasAvailableStock(amount,initialLinked));
    for(Map.Entry<Product,Integer> entry : finalLinked.entrySet())
      if(entry.getKey().getTotalStock()<entry.getValue())
        throw new InvalidProductAmountException(entry.getKey().getId(),entry.getValue(),entry.getKey().getTotalStock());
    Double price=product.accept(new Sell(amount));
    Sale sale=new Sale(_currentTransactionId,idPartner,idProduct,amount,price,deadline);
    _transactions.put(_currentTransactionId++,sale);
     partner.setTotalPerformed(price);
  }


  /**
   * Registers a new purchase of a known product in the system
   * @param idPartner partner id
   * @param idProduct product id
   * @param price purchase price
   * @param amount purchase amount
   * @throws InvalidPartnerKeyException
   * @throws InvalidProductKeyException
   */
  void registerKnownProductPurchase(String idPartner, String idProduct, Double price, int amount) throws InvalidPartnerKeyException, InvalidProductKeyException{
    if(!containsPartnerKey(idPartner))
      throw new InvalidPartnerKeyException();
    if(!containsProductKey(idProduct))
      throw new InvalidProductKeyException();
    Product product=_products.get(idProduct);
    Double oldMinPrice=product.getMinPrice();
    Batch batch=new Batch(idProduct,idPartner,price,amount);
    product.addBatch(batch);
    if(product.getBatchesNumber()==1)
      product.notifyNew();
    else if(product.getMinPrice()<oldMinPrice)
      product.notifyBargain();
    Purchase purchase=new Purchase(_currentTransactionId, idPartner, idProduct, amount, price, _date);
    _transactions.put(_currentTransactionId++,purchase);
    _partners.get(idPartner).setTotalSold(price*amount);
    _balance-=price*amount;
  }
    

  /**
   * Registers a new purchase of a unknown product in the system
   * @param idPartner partner id
   * @param idProduct product id
   * @param price purchase price
   * @param amount purchase amount
   */
  void registerUnknownProductPurchase(String idPartner, String idProduct, Double price, int amount){
    Purchase purchase=new Purchase(_currentTransactionId, idPartner, idProduct, amount, price, _date);
    _transactions.put(_currentTransactionId++,purchase);
    _partners.get(idPartner).setTotalSold(price*amount);
    _balance-=price*amount;
  }

  
  /**
   * Registers a new disaggregation in the system
   * @param idPartner partner id
   * @param idProduct product id
   * @param amount disaggregation amount
   * @throws InvalidProductKeyException
   * @throws InvalidPartnerKeyException
   * @throws InvalidProductAmountException
   */
  void registerDisaggregation(String idPartner, String idProduct, int amount) throws InvalidProductKeyException,InvalidPartnerKeyException,InvalidProductAmountException{
    if(!containsProductKey(idProduct))
      throw new InvalidProductKeyException();
    if(!containsPartnerKey(idPartner))
      throw new InvalidPartnerKeyException();
    Product product=_products.get(idProduct);
    Partner partner=_partners.get(idPartner);
    Disaggregation disaggregation=product.accept(new Disaggregate(_currentTransactionId,idPartner,amount,_date));
    if(disaggregation!=null){
      _transactions.put(_currentTransactionId++,disaggregation);
      Double paidValue=disaggregation.getPaidValue();
      _balance+=paidValue;
      partner.setPoints(0,paidValue);
    }
  }
  

  /**
   * Check if the transaction id is in use in the system 
   * @param id transaction id
   * @return true if already in use
   */
  boolean containsTransactionKey(Integer id){
    return _transactions.containsKey(id);
  }


  /**
   * Registers the payment of a sale in the system
   * @param id sale id
   * @throws InvalidTransactionKeyException
   */
  void paySale(Integer id) throws InvalidTransactionKeyException{
    if(!containsTransactionKey(id))
      throw new InvalidTransactionKeyException(); 
    Transaction transaction=_transactions.get(id);
    Partner partner=_partners.get(transaction.getIdPartner());
    Product product=_products.get(transaction.getIdProduct());
    int offset=product.getDeadlineOffset();
    Double payment=transaction.accept(new Pay(partner,offset,_date));
    _balance+=payment;
  }

  
  /**
   * Calculates the price of a transaction at the current date
   * @param transaction
   */
  void calculatePrice(Transaction transaction){
    Partner partner=_partners.get(transaction.getIdPartner());
    Product product=_products.get(transaction.getIdProduct());
    int offset=product.getDeadlineOffset();
    transaction.accept(new CalculatePrice(partner,offset,_date));
  }


  /**
   * Present information about id specified transaction
   * @param id transaction id
   * @return transaction's information
   * @throws InvalidTransactionKeyException
   */
  String displayTransaction(Integer id) throws InvalidTransactionKeyException{
    if(!containsTransactionKey(id))
      throw new InvalidTransactionKeyException();
    Transaction transaction=_transactions.get(id);
    calculatePrice(transaction);
    return _transactions.get(id).toString();
  }


  //Notification methods --------------------------------------------------------------------------


  /**
   * Puts new partner as an observer for all existing products
   * @param partner
   */
  void newPartnerNotifications(Partner partner){
    for(Map.Entry<String,Product> entry : _products.entrySet())
      entry.getValue().registerObserver(partner);
  }


  /**
   * Puts all partners as observers for the new product
   * @param product
   */
  void newProductNotifications(Product product){
    for(Map.Entry<String,Partner> entry : _partners.entrySet())
      product.registerObserver(entry.getValue());
  }


  /**
   * Registers or removes and observer on the product, depending if it's present or not
   * @param idPartner partner id
   * @param idProduct product id
   * @throws InvalidPartnerKeyException
   * @throws InvalidProductKeyException
   */
  void toggleNotifications(String idPartner, String idProduct) throws InvalidPartnerKeyException,InvalidProductKeyException{
    if(!containsPartnerKey(idPartner))
      throw new InvalidPartnerKeyException();
    else if(!containsProductKey(idProduct))
      throw new InvalidProductKeyException();
    Product product=_products.get(idProduct);
    Partner partner=_partners.get(idPartner);
    product.toggleNotifications(partner);
  }


  //Date methods ----------------------------------------------------------------------------------


  /**
   * Advances internal date counter
   * @param days number of days to advance
   * @throws NotPositiveDateException
   */
  void advanceDate(int days) throws NotPositiveDateException{
    if(days<=0)
      throw new NotPositiveDateException();
    _date+=days;
  }


  /**
   * Gets the date value
   * @return  the current date
   */
  int getDate(){
    return _date;
  }   
}
