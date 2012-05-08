package org.east.e1;

public class InstanceNameOrOperation{
  private boolean isNew;
  public boolean isNew(){
    return isNew;
  }
  public String getIdent(){
    return ident;
  }
  private String ident;
  public InstanceNameOrOperation(boolean isNew,String ident){
    this.isNew=isNew;
    this.ident=ident;
  }
}
