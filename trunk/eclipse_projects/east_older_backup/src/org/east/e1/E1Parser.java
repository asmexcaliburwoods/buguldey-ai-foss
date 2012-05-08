package org.east.e1;

import org.opencyc.api.CycApiException;
import org.east.concepts.Concept;
import org.east.concepts.Name;
import org.east.concepts.EastProjectDialogueTextualContext;
import org.east.concepts.utility.MeaningAllocator;

import java.io.IOException;
import java.io.File;
import java.util.Iterator;

public class E1Parser extends Concept{
  private long e1_lastModified;
  private static boolean nlParserUpdated;
  public E1Language getE1Language(){
    return e1;
  }
  private E1Language e1;
  public synchronized boolean update(){
    boolean updated=false;
    String fileName="nlparser.e1";
    File file=new File(fileName);
    if(!file.exists())
      throw new RuntimeException("file "+file.getAbsolutePath()+" does not exist");
    long lastMod=file.lastModified();
//    System.out.println("old ts "+e1_lastModified+"; new ts "+lastMod);
    if(e1_lastModified!=lastMod){
      System.out.println("Parsing natural language definition");
      updated=true;
      Scanner scanner = new Scanner(fileName);
      Parser parser = new Parser(scanner);
      E1Language e1=parser.Parse();


      if (parser.errors.count != 0)
        if (parser.errors.count == 1)
          throw new RuntimeException("1 error detected in "+fileName);
        else
          throw new RuntimeException(parser.errors.count + " errors detected in "+fileName);

      try{
        compile(e1);
      } catch(Exception e){
        throw new RuntimeException(e);
      }

      this.e1=e1;
      this.e1_lastModified=lastMod;
      System.out.print("Saving... ");
      save();
      System.out.println("Saved.");
    }
    return updated;
  }
  private static void compile(E1Language e1) throws CycApiException, IOException{
    resolveRuleLabelReferenceOrWordClass(e1);
    resolveInstanceNameOrOperation(e1);
    resolveTopLevelRules(e1);
  }
  private static void resolveTopLevelRules(E1Language e1){
    Iterator it=e1.getRules().iterator();
    while(it.hasNext()){
      E1Rule rule=(E1Rule)it.next();
      if(rule.getRuleId().equals(e1.getTopLevelRuleId())){
        e1.getTopLevelRules().add(rule);
      }
    }
  }
  private static void resolveInstanceNameOrOperation(E1Language e1){
    Iterator it=e1.getRules().iterator();
    while(it.hasNext()){
      E1Rule rule=(E1Rule)it.next();
//      rule.getLhs().resolveInstanceNameOrOperation();//todo
    }
  }
  private static void resolveRuleLabelReferenceOrWordClass(E1Language e1) throws CycApiException, IOException{
    Iterator it=e1.getRules().iterator();
    while(it.hasNext()){
      E1Rule rule=(E1Rule)it.next();
      LHSSequenceElement lhs=rule.getLhs();
      lhs.resolveRuleLabelReferenceOrWordClass(e1,null);
    }
  }
  public static void define(){
    Name.define("E1Parser", EastProjectDialogueTextualContext.getInstance(),
            E1Parser.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new E1Parser();
              }
            });
  }
  public static void setNLParserUpdated(boolean b){
    nlParserUpdated=b;
  }
  public static boolean isNLParserUpdated(){
    return nlParserUpdated;
  }
}
