package org.east.e1.semaction.expr;

import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Node;
import org.east.concepts.NameableInstantiableConcept;
import org.east.concepts.Name;

public class LinkExpr extends Expr{
  private Expr e1;
  private Expr e2;
  public LinkExpr(Expr e1, Expr e2){
    this.e1=e1;
    this.e2=e2;
  }
  public Object evaluate(Scope ctx) throws Exception{
    Object r1=e1.evaluateUnwrapLValue(ctx);
    Object r2=e2.evaluateUnwrapLValue(ctx);
    Node lo=(Node)r1;
    if(r2 instanceof String){
      NameableInstantiableConcept nic=(NameableInstantiableConcept)
              Name.resolveSingleConcept("NameableInstantiableConcept");
      r2=nic.newInstance((String)r2);
    }
    lo.getLinks().add(r2);
    return r2;
  }
  public String toString(){
    return e1+"->"+e2;
  }
}
