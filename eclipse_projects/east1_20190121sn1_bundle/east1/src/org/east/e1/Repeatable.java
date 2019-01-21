package org.east.e1;

import org.opencyc.api.CycApiException;
import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Sentence;

import java.io.IOException;

public class Repeatable extends LHSSequenceElement{
  public LHSSequenceElement getSeq(){
    return seq;
  }
  private LHSSequenceElement seq;
  public Repeatable(LHSSequenceElement e){
    this.seq=e;
  }
  public void resolveRuleLabelReferenceOrWordClass(E1Language e1, LHSSequenceElement parent) throws CycApiException, IOException{
    seq.resolveRuleLabelReferenceOrWordClass(e1,this);
  }
  public void resolveInstanceNameOrOperation(){
    //todo seq.resolveInstanceNameOrOperation();
  }
  public void replaceChild(LHSSequenceElement child, LHSSequenceElement newChild){
    if(seq!=child)throwNoSuchChild();
    seq=newChild;
  }
  public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx) throws Exception, IOException{
//    int i=0;
//    int indexStart=index.getIndex();
//    while(true){
//      if(node.isFullyExplored())return false;
//      AlternativesNode node_i=node.getCreateAlternative(i,"repeatable, count="+i+": "+this);
//      if(node_i.isFullyExplored()){i++;continue;}
//      index.setIndex(indexStart);
//      for(int j=0;j<i;j++){
//        if(!seq.match(node_i,index,treesFrame)){
//          node_i.setFullyExplored(true);
//          node.setFullyExplored(true);
//          return false;
//        }
//      }
//      return true;
//    }
//    int i=0;
//    int indexStart=index.getIndex();
    while(true){
      if(node.isFullyExplored())return false;
//      AlternativesNode node_i=node.getCreateAlternative(i,"repeatable, count="+i+": "+this);
      int start=index.getIndex();
      Scope childCtx=new Scope(this,ctx);
      sentence=sentence.getActiveForm();
      boolean match=seq.match(node, index, sentence,childCtx);
      if(!match){
        index.setIndex(start);
        break;
//        node_i.setFullyExplored(true);
//        node.setFullyExplored(true);
//        return false;
      }
//      if(node_i.isFullyExplored()){i++;continue;}
//      index.setIndex(indexStart);
//      for(int j=0;j<i;j++){
//      }
    }
    executeSemanticalAction(ctx);
    return true;
  }
  public String toString(){
    return "{"+seq+"}";
  }
}
