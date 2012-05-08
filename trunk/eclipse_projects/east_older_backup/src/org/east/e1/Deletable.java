package org.east.e1;

import org.opencyc.api.CycApiException;
import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Sentence;

import java.io.IOException;

public class Deletable extends LHSSequenceElement{
  public LHSSequenceElement getSeq(){
    return seq;
  }
  private LHSSequenceElement seq;
  public Deletable(LHSSequenceElement e){
    this.seq=e;
  }
  public void resolveRuleLabelReferenceOrWordClass(E1Language e1, LHSSequenceElement parent) throws CycApiException, IOException{
    seq.resolveRuleLabelReferenceOrWordClass(e1,this);
  }
  public void resolveInstanceNameOrOperation(){
    //seq.resolveInstanceNameOrOperation();//todo
  }
  public void replaceChild(LHSSequenceElement child, LHSSequenceElement newChild){
    if(seq!=child)throwNoSuchChild();
    seq=newChild;
  }
  public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx) throws Exception, IOException{
    int i=0;
    int indexStart=index.getIndex();
    boolean match=false;
    while(true){
      if(i>=2)break;
      AlternativesNode node_i=node.getCreateAlternative(i,"deletable, count="+i+(i==1?"; seq="+seq:""));
      if(node_i.isFullyExplored()){i++;continue;}
      LHSSequenceElement elem=i==0?seq:null;
      index.setIndex(indexStart);
      Scope childCtx=new Scope(this,ctx);
      sentence=sentence.getActiveForm();
      if(elem!=null&&elem.match(node_i,index,sentence,childCtx)){
        match=true;
        break;
      }
      if(elem==null){
        match=true;
        break;
      }
      i++;
    }
    if(match)executeSemanticalAction(ctx);
    else node.setFullyExplored(true);
    return match;
  }
  public String toString(){
    return "["+seq+"]";
  }
}
