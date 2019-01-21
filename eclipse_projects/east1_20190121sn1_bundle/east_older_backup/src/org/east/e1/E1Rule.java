package org.east.e1;

import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Sentence;

import java.io.IOException;
import java.io.Serializable;

public class E1Rule implements Serializable{
  public LHSSequenceElement getLhs(){
    return lhs;
  }
  private LHSSequenceElement lhs;
  public E1Args getFormalArgs(){
    return formalArgs;
  }
  private E1Args formalArgs;
  public String getRuleId(){
    return ruleId;
  }
  private String ruleId;
  public E1Rule(String ruleId){
    this.ruleId=ruleId;
  }
  public void setFormalArgs(E1Args args){
    int size=args.getArgs().size();
    if(size!=1)
      throw new RuntimeException("args size must be 1, but it is "+size+": ("+args+"), in "+this);
    this.formalArgs=args;
  }
  public void setLHS(LHSSequenceElement seq){
    this.lhs=seq;
  }
  public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx) throws Exception, IOException{
    try{
      Scope ctxRule=new Scope(this);
      sentence=sentence.getActiveForm();
      boolean match=lhs.match(node, index, sentence, ctxRule);
      if(match)ctx.setReturnValue(ctxRule.getValue(formalArgs.getArg1()));
      return match;
    }catch(StackOverflowError e){
      System.out.println(this);
      throw e;
    }
  }
  public String toString(){
    return ruleId+"("+formalArgs+"): "+lhs+".";
  }
}
