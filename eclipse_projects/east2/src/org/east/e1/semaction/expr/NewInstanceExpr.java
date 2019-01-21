package org.east.e1.semaction.expr;

import org.east.e1.semaction.Scope;
import org.east.e1.WordLit;
import org.east.concepts.Name;
import org.east.concepts.InstantiableConcept;
import org.east.concepts.NameableInstantiableConcept;

public class NewInstanceExpr extends Expr{
  private String className;
  private InstantiableConcept instantiableConcept;
  private NameableInstantiableConcept instantiableConceptN;
  private boolean stringLit;
  public NewInstanceExpr(String className,boolean stringLit){
    this.stringLit=stringLit;
    if(stringLit)className=WordLit.strip(className);
    this.className=className;
    if(stringLit)
      this.instantiableConceptN=(NameableInstantiableConcept)Name.resolveSingleConcept(
              "NameableInstantiableConcept");
    else{
      this.instantiableConcept=(InstantiableConcept)Name.resolveSingleConcept(className);
      if(instantiableConcept==null)throw new RuntimeException("no such class: "+className);
    }
  }
  public Object evaluate(Scope ctx) throws Exception{
    if(stringLit)
      return instantiableConceptN.newInstance(className);
    else
      return instantiableConcept.newInstance(null);
  }
  public String toString(){
    if(stringLit)return "new \""+className+"\"";
    else return "new "+className;
  }
}
