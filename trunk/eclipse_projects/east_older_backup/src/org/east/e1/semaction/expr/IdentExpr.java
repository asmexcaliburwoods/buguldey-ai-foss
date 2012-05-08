package org.east.e1.semaction.expr;

import org.east.e1.semaction.Scope;

public class IdentExpr extends Expr{
  private String ident;
  public IdentExpr(String ident){
    this.ident=ident;
  }
  public Object evaluate(Scope ctx) throws Exception{
    return ctx.getLValue(ident);
  }
  public String toString(){
    return ident;
  }
}
