package org.east.concepts;

import org.east.concepts.utility.MeaningAllocator;

import java.math.BigInteger;
import java.io.Serializable;

public class UIDGenerator extends Concept{
  public static class UID implements Serializable{
    private final BigInteger uid;
    private UID(BigInteger uid){
      this.uid=uid;
    }
    public String toString(){
      return uid.toString(16);
    }
  }
  public static UIDGenerator getInstance(){
    return (UIDGenerator)Concept.resolve(Name.define("UIDGenerator",
            JavaLanguageTextualContext.getInstance(), UIDGenerator.class,new MeaningAllocator(){
      public Concept allocate(){
        return new UIDGenerator();
      }
    }).getConcept().getConceptId());
  }
  private BigInteger lastUid=BigInteger.ZERO;
  public synchronized UID createUID(){
    lastUid=lastUid.add(BigInteger.ONE);
    save();
    return new UID(lastUid);
  }
}
