package org.east.e1.semaction.expr;

import org.east.e1.semaction.Scope;

public class AssignToNameExpr extends Expr{
  private String varName;
  private Expr e;
  public AssignToNameExpr(String varName, Expr e){
    this.varName=varName;
    this.e=e;
  }
  public Object evaluate(Scope ctx) throws Exception{
    Object r=e.evaluateUnwrapLValue(ctx);
    return ctx.createLValue(varName,r);
  }
  public String toString(){
    return "("+varName+"="+e+")";
  }
}
