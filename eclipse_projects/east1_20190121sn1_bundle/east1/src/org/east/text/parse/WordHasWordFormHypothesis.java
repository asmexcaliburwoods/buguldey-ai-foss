package org.east.text.parse;

import org.east.e1.ParsedWord;
import org.east.pos.PartOfSpeech;
import org.east.thinking.HypothesisImpl;
import org.east.thinking.SetOfHypotheses;

import java.util.ArrayList;
import java.util.Iterator;

public class WordHasWordFormHypothesis extends HypothesisImpl{
  private ParsedWord word;
  private PartOfSpeech wordForm;
  private java.util.List inactiveWordForms;
  public WordHasWordFormHypothesis(SetOfHypotheses set,ParsedWord word,PartOfSpeech wordForm){
    super(set);
    this.word=word;
    this.wordForm=wordForm;
  }
  public ParsedWord getWord(){
    return word;
  }
  public PartOfSpeech getWordForm(){
    return wordForm;
  }
  protected void assertImpl() throws Exception{
    Iterator it=new ArrayList(word.getWord().getWordForms()).iterator();
    while(it.hasNext()){
      PartOfSpeech pos=(PartOfSpeech)it.next();
      if(pos==wordForm)continue;
      if(inactiveWordForms==null)inactiveWordForms=new ArrayList(word.getWord().getWordForms().size()-1);
      inactiveWordForms.add(pos);
      word.getWord().removeWordForm(pos,hypothesisIsActive());
    }
    word.getLinks().add(this);
  }
  private String hypothesisIsActive(){
    return "hypothesis is active: "+this;
  }
  protected void retractImpl(){
    if(inactiveWordForms!=null){
      Iterator it=inactiveWordForms.iterator();
      while(it.hasNext()){
        PartOfSpeech pos=(PartOfSpeech)it.next();
        word.getWord().restoreWordForm(pos,hypothesisIsActive());
      }
      word.getLinks().remove(this);
    }
  }
  public String hypothesisToString(){
    return "word-has-word-form-hypothesis: word: "+word.getWord()+"; word form: "+wordForm;
  }
}
