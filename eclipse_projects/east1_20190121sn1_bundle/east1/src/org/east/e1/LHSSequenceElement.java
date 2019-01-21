package org.east.e1;

import org.opencyc.api.CycApiException;
import org.east.e1.semaction.SemanticalAction;
import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Sentence;

import java.io.IOException;
import java.io.Serializable;

public abstract class LHSSequenceElement implements Serializable{
  private SemanticalAction semanticalAction;
  public SemanticalAction getSemanticalAction(){
    return semanticalAction;
  }
  public void setSemanticalAction(SemanticalAction semanticalAction){
    this.semanticalAction=semanticalAction;
  }
  public abstract void resolveRuleLabelReferenceOrWordClass(E1Language e1,LHSSequenceElement parent) throws CycApiException, IOException;
//  public abstract void resolveInstanceNameOrOperation();//todo
  public abstract void replaceChild(LHSSequenceElement child, LHSSequenceElement newChild);
  protected void throwNoSuchChild(){
    throw new RuntimeException("no such child");
  }
  public abstract boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx) throws Exception, IOException;
  protected void executeSemanticalAction(Scope ctx)throws Exception{
    if(semanticalAction!=null)semanticalAction.execute(ctx);
  }
}
