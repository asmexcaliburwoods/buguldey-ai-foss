package org.east.concepts.utility;

import org.east.cyc.CycLink;
import org.opencyc.api.CycApiException;

import java.io.IOException;

public class EastProjectCycConstant extends CycConcept{
  private org.opencyc.cycobject.CycFort cycConstant;
  public EastProjectCycConstant(org.opencyc.cycobject.CycFort cycConstant){
    super(cycConstant.cyclify());
    this.cycConstant=cycConstant;
  }
  public org.opencyc.cycobject.CycFort getCycFort(){
    return cycConstant;
  }
  public boolean isa(CycConcept collection) throws CycApiException, IOException{
    if(collection==null||!(collection instanceof EastProjectCycConstant))return false;
    return CycLink.isa(cycConstant,((EastProjectCycConstant)collection).cycConstant);
  }
  public int hashCode(){
    return cycConstant.hashCode();
  }
  public boolean equals(Object o){
    return cycConstant.equals(((EastProjectCycConstant)o).cycConstant);
  }
}
