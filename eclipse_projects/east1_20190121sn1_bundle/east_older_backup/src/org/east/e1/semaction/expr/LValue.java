package org.east.e1.semaction.expr;

public final class LValue{
  private Object value;
  public Object getValue(){
    return value;
  }
  public void setValue(Object value){
    this.value=value;
  }
  public String toString(){
    return String.valueOf(value);
  }
}
