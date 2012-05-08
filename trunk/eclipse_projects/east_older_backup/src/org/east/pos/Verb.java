package org.east.pos;

import org.opencyc.cycobject.CycFort;

public class Verb extends Denoting{
  public static final int INFINITIVE = 1;
  public static final int DOES = 2;
  public static final int DID = 3;
  public static final int UNSPECIFIED = 4;
  private String toString;
  public int getKind(){
    return kind;
  }
  public static String kindToString(int kind){
    switch(kind){
      case INFINITIVE: return "infinitive";
      case DOES: return "does";
      case DID: return "did";
      case UNSPECIFIED: return "unspecified";
      default:throw new RuntimeException();
    }
  }
  private int kind;
  public Verb(CycFort denotat,int kind){
    super(denotat);
    this.kind=kind;
  }
  public synchronized String toString(){
    if(toString==null)toString=super.toString()+"("+kindToString(kind)+")";
    return toString;
  }
}
