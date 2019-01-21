package org.east.e1;

import org.opencyc.api.CycApiException;
import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Sentence;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;
import java.io.IOException;

public class LHSSeq extends LHSSequenceElement{
  public List getElements(){
    return Collections.unmodifiableList(elements);
  }
  private List elements=new LinkedList();
  void addElem(LHSSequenceElement e){
    elements.add(e);
  }
  public void resolveRuleLabelReferenceOrWordClass(E1Language e1, LHSSequenceElement parent) throws CycApiException, IOException{
    for(int i=0;i<elements.size();i++){
      LHSSequenceElement e=(LHSSequenceElement)elements.get(i);
      e.resolveRuleLabelReferenceOrWordClass(e1,this);
    }
  }
  public void resolveInstanceNameOrOperation(){
    Iterator it=elements.iterator();
    while(it.hasNext()){
      LHSSequenceElement e=(LHSSequenceElement)it.next();
      //e.resolveInstanceNameOrOperation();//todo
    }
  }
  public void replaceChild(LHSSequenceElement child, LHSSequenceElement newChild){
    Iterator it=elements.iterator();
    int i=0;
    while(it.hasNext()){
      LHSSequenceElement e=(LHSSequenceElement)it.next();
      if(e==child){
        elements.remove(i);
        elements.add(i, newChild);
        return;
      }
      i++;
    }
    throwNoSuchChild();
  }
  public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx) throws Exception, IOException{
    Iterator it=elements.iterator();
    int i=0;
    int start=index.getIndex();
    boolean match=true;
    while(it.hasNext()){
      LHSSequenceElement element=(LHSSequenceElement)it.next();
      AlternativesNode node_i=node.getCreateAlternative(i,"sequence element; position="+(i++)+"; element="+element);
      node_i.setNotAnAlt(true);
      sentence=sentence.getActiveForm();
      match=element.match(node_i, index, sentence,ctx);
      if(!match){
        index.setIndex(start);
        break;
      }
    }
    if(match)executeSemanticalAction(ctx);
    return match;
  }
  public String toString(){
    StringBuffer sb=new StringBuffer();
    Iterator it=elements.iterator();
    while(it.hasNext()){
      Object o=it.next();
      if(sb.length()>0)sb.append(' ');
      sb.append(o.toString());
    }
    return sb.toString();
  }
}
