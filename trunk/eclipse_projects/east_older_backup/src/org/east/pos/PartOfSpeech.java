package org.east.pos;

import java.io.Serializable;

public abstract class PartOfSpeech implements Serializable{
  public int hashCode(){
    return toString().hashCode();
  }
  public boolean equals(Object o){
    if(o==null)return false;
    return toString().equals(o.toString());
  }
}
