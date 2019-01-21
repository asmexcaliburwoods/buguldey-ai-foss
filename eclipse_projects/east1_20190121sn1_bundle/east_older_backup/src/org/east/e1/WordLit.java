package org.east.e1;

import org.east.util.ExceptionUtil;
import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Sentence;
import org.east.concepts.utility.Word;
import org.opencyc.api.CycApiException;

import java.io.IOException;

public class WordLit extends LHSSequenceElement{
  public String getLiteral(){
    return literal;
  }
  private String literal;
  public WordLit(String literal){
    this.literal=strip(literal);
  }
  public static String strip(String literal){
    return literal.substring(1,literal.length()-1);
  }
  public void resolveRuleLabelReferenceOrWordClass(E1Language e1, LHSSequenceElement parent) throws CycApiException, IOException{
  }
  public void replaceChild(LHSSequenceElement child, LHSSequenceElement newChild){
    ExceptionUtil.unsupportedOperation();
  }
  public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx) throws Exception, IOException{
    if(index.getIndex()>=sentence.getWords().size())return false;
    Word w=(Word)sentence.getWords().get(index.getIndex());
    index.setIndex(index.getIndex()+1);
    boolean match=w.getSpelling().equalsIgnoreCase(literal);
    if(!match)index.setIndex(index.getIndex()-1);
    else{
      executeSemanticalAction(ctx);
    }
    return match;
  }
  public String toString(){
    return "\""+literal+"\"";
  }
}
