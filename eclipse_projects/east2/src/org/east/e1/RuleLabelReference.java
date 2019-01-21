package org.east.e1;

import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Sentence;
import org.east.util.ExceptionUtil;

import java.io.IOException;
import java.util.List;

public class RuleLabelReference extends LHSSequenceElement{
  private List rules;
  private String displayName;
  public E1Args getArgs(){
    return args;
  }
  private E1Args args;
  public RuleLabelReference(String displayName,E1Args args,List rules){
    this.rules=rules;
    this.displayName=displayName;
    this.args=args;
    int size=args.getArgs().size();
    if(size!=1)
      throw new RuntimeException("args size must be 1, but it is "+size+": ("+args+"), in "+this);
  }
  public void resolveRuleLabelReferenceOrWordClass(E1Language e1, LHSSequenceElement parent){
    ExceptionUtil.unsupportedOperation();
  }
  public void resolveInstanceNameOrOperation(){
    ExceptionUtil.unsupportedOperation();
  }
  public List getRules(){
    return rules;
  }
  public void replaceChild(LHSSequenceElement child, LHSSequenceElement newChild){
    ExceptionUtil.unsupportedOperation();
  }
  public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx) throws Exception, IOException{
    int i=0;
    int indexStart=index.getIndex();
    boolean match=false;
    while(true){
      if(i>=getRules().size())break;
      E1Rule rule=(E1Rule)getRules().get(i);
      AlternativesNode node_i=node.getCreateAlternative(i,rule);
      if(rule!=node_i.getDefiningObject())
        throw new RuntimeException("Internal error: rule must be equal to defining object, but it is not! "+
                "\r\nRule="+rule+"; defining object="+node_i.getDefiningObject());
      if(node_i.isFullyExplored()){i++;continue;}
      index.setIndex(indexStart);
      if(rule==null)
        throw new NullPointerException("rule is null");
      match=rule.match(node_i, index, sentence,ctx);
      if(match){
        ctx.createLValue(args.getArg1(),ctx.getReturnValue());
        break;
      }
      i++;
    }
    if(match)executeSemanticalAction(ctx);
    else node.setFullyExplored(true);
    return match;
  }
  public String toString(){
    return displayName+"("+args+")";
  }
}
