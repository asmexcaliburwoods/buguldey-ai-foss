package org.east.pos;

import org.opencyc.cycobject.CycFort;

public class Adverb extends Denoting{
  private String toString;
  public Adverb(CycFort denotat){
    super(denotat);
  }
  public synchronized String toString(){
    if(toString==null)toString=getDenotat().cyclify();
    return toString;
  }
}
