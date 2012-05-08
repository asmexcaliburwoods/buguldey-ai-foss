package org.east.e1.semaction;

import org.east.e1.semaction.expr.Expr;

public class ExpressionStatement extends Statement{
  private Expr e;
  public ExpressionStatement(Expr e){
    this.e=e;
  }
  public void execute(Scope ctx) throws Exception{
    e.evaluate(ctx);
  }
  public String toString(){
    return e.toString();
  }
}
