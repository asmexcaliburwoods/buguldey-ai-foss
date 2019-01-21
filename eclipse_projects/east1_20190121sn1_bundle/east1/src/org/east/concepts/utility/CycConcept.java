package org.east.concepts.utility;

import org.opencyc.api.CycApiException;
import org.opencyc.cycobject.CycFort;

import java.io.IOException;

public abstract class CycConcept extends NodeImpl{
  protected CycConcept(String name){
    super(name);
  }
  public abstract  boolean isa(CycConcept collection) throws CycApiException, IOException;
  public abstract CycFort getCycFort();
}
