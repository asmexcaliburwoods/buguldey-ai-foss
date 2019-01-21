package org.east.e1;

import org.east.util.ExceptionUtil;
import org.east.concepts.utility.Word;
import org.east.pos.PartOfSpeech;
import org.opencyc.api.CycApiException;

import java.io.IOException;
import java.util.List;
import java.util.Iterator;
import java.util.HashSet;

public abstract class WordClass extends LHSSequenceElement{
  public final List getWordForms(){
    return wordForms;
  }
  private List wordForms;
  public final String getWordForConcept(){
    return wordForConcept;
  }
  private String wordForConcept;
  public String toString(){
    return wordForConcept+"("+instanceName+")";
  }
  public final String getInstanceName(){
    return instanceName;
  }
  private String instanceName;
  public WordClass(List wordForms,String wordForConcept,String instanceName){
    this.instanceName=instanceName;
    this.wordForConcept=wordForConcept;
    this.wordForms=wordForms;
  }
  public final void resolveRuleLabelReferenceOrWordClass(){
    ExceptionUtil.unsupportedOperation();
  }
  public final void resolveInstanceNameOrOperation(){
    ExceptionUtil.unsupportedOperation();
  }
  public final void resolveRuleLabelReferenceOrWordClass(E1Language e1, LHSSequenceElement parent) throws CycApiException, IOException{
    ExceptionUtil.unsupportedOperation();
  }
  public final void replaceChild(LHSSequenceElement child, LHSSequenceElement newChild){
    ExceptionUtil.unsupportedOperation();
  }
//  public abstract boolean match(AlternativesNode node, IndexInSentence index, Sentence treesFrame, Scope ctx) throws CycApiException, IOException;
  public final void filterWordForms(Word word) throws Exception{
    //new HashSet() is to prevent ConcurrentModificationException
    Iterator it=new HashSet(word.getWordForms()).iterator();
    while(it.hasNext()){
      PartOfSpeech pos=(PartOfSpeech)it.next();
      boolean match=false;
      if(matchesWordForm(pos))match=true;
      if(!match)word.removeWordForm(pos,"Stage right after parse: does not match word class "+this);
    }
  }
  public abstract boolean matchesWordForm(PartOfSpeech word) throws Exception;
}
