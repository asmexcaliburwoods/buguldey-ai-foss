package org.east.e1;

import org.opencyc.api.CycApiException;
import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Sentence;

import java.io.IOException;

public class LHSSeqElementOr extends LHSSequenceElement{
  public LHSSequenceElement getElem2(){
    return elem2;
  }
  private LHSSequenceElement elem2;
  public LHSSequenceElement getElem1(){
    return elem1;
  }
  private LHSSequenceElement elem1;
  public void setElem1(LHSSequenceElement s1){
    elem1=s1;
  }
  public void setElem2(LHSSequenceElement s2){
    elem2=s2;
  }
  public void resolveRuleLabelReferenceOrWordClass(E1Language e1, LHSSequenceElement parent) throws CycApiException, IOException{
    elem1.resolveRuleLabelReferenceOrWordClass(e1,this);
    elem2.resolveRuleLabelReferenceOrWordClass(e1,this);
  }
  public void resolveInstanceNameOrOperation(){//todo
    //elem1.resolveInstanceNameOrOperation();
    //elem2.resolveInstanceNameOrOperation();
  }
  public void replaceChild(LHSSequenceElement child, LHSSequenceElement newChild){
    if(elem1==child){
      elem1=newChild;
      return;
    }
    if(elem2==child){
      elem2=newChild;
      return;
    }
    throwNoSuchChild();
  }
  public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx) throws Exception, IOException{
    int i=0;
    int indexStart=index.getIndex();
    boolean match=false;
    while(true){
      if(i>=2)break;
      AlternativesNode node_i=node.getCreateAlternative(i,i==0?elem1:elem2);
      if(node_i.isFullyExplored()){i++;continue;}
      LHSSequenceElement elem=i==0?elem1:elem2;
      index.setIndex(indexStart);
      sentence=sentence.getActiveForm();
      match=elem.match(node_i, index, sentence,ctx);
      if(match)break;
      i++;
    }
    if(match)executeSemanticalAction(ctx);
    else node.setFullyExplored(true);
    return match;
  }
  public String toString(){
    return elem1+" | "+elem2;
  }
}
