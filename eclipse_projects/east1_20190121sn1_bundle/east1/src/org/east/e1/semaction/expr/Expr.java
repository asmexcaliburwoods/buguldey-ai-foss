package org.east.e1.semaction.expr;

import org.east.e1.semaction.Scope;

import java.io.Serializable;

public abstract class Expr implements Serializable{
  public abstract Object evaluate(Scope ctx)throws Exception;
  public final Object evaluateUnwrapLValue(Scope ctx)throws Exception{
    Object o=evaluate(ctx);
    if(o instanceof LValue)o=((LValue)o).getValue();
    return o;
  }
}
