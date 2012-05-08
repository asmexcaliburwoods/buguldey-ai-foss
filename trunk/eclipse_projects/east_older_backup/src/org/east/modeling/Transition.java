package org.east.modeling;

public class Transition{
  private ModelingObject objectFrom;
  public ModelingObject getObjectFrom(){
    return objectFrom;
  }
  public void setObjectFrom(ModelingObject objectFrom){
    this.objectFrom=objectFrom;
  }
  public ModelingObject getObjectTo(){
    return objectTo;
  }
  public void setObjectTo(ModelingObject objectTo){
    this.objectTo=objectTo;
  }
  private ModelingObject objectTo;
}
