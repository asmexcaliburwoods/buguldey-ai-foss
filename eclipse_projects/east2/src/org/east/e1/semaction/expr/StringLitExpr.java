package org.east.e1.semaction.expr;

import org.east.e1.WordLit;
import org.east.e1.semaction.Scope;

public class StringLitExpr extends Expr{
  private String value;
  public StringLitExpr(String value){
    this.value=WordLit.strip(value);
  }
  public Object evaluate(Scope ctx) throws Exception{
    return value;
  }
  public String toString(){
    return "\""+value+"\"";
  }
}
