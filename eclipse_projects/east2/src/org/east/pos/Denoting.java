package org.east.pos;

import org.opencyc.cycobject.CycFort;

public abstract class Denoting extends PartOfSpeech{
  public final CycFort getDenotat(){
    return denotat;
  }
  private CycFort denotat;
  protected Denoting(CycFort denotat){
    this.denotat=denotat;
  }
  public String toString(){
    return denotat.cyclify();
  }
}
