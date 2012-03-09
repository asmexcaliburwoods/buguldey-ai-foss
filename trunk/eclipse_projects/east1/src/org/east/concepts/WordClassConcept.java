package org.east.concepts;

import org.east.concepts.utility.Sentence;
import org.east.concepts.utility.Word;
import org.east.e1.AlternativesNode;
import org.east.e1.IndexInSentence;
import org.east.e1.WordClass;
import org.east.e1.WordUtil;
import org.east.e1.semaction.Scope;
import org.east.pos.PartOfSpeech;

import java.io.IOException;
import java.util.Iterator;

public abstract class WordClassConcept extends InstantiableConcept{
  private static final long serialVersionUID = -339559652871513880L;
  public abstract boolean matchesWordForm(PartOfSpeech word) throws Exception, IOException;
  protected boolean matchesWord(Word w) throws Exception{
    Iterator<PartOfSpeech> it=w.getWordForms().iterator();
    boolean match=false;
    while(it.hasNext()){
      PartOfSpeech pos=it.next();
      if(matchesWordForm(pos)){
        match=true;
        break;
      }
    }
    return match;
  }
  public Instance newInstance(String instanceName,String wordForConcept){
    return new Instance(instanceName,wordForConcept);
  }
  public Object newInstance(Object[] args){
    if(args==null||args.length!=2||!(args[0] instanceof String)||!(args[1] instanceof String))
      throw new IllegalArgumentException("first argument must be String instanceName, second arg must be String wordForConcept");
    return newInstance((String)args[0],(String)args[1]);
  }
  public final class Instance extends WordClass{
    private static final long serialVersionUID = -7976085142926509017L;
	Instance(String instanceName,String wordForConcept){
      super(null,wordForConcept,instanceName);
    }
    public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx) throws Exception, IOException{
      Word w=(Word)sentence.getWords().get(index.getIndex());
      index.setIndex(index.getIndex()+1);
      boolean match=matchesWord(w);
      if(match){
        WordUtil.wordParsed(w,this,ctx);
        executeSemanticalAction(ctx);
      }else index.setIndex(index.getIndex()-1);
      return match;
    }
    public boolean matchesWordForm(PartOfSpeech word) throws Exception{
      return WordClassConcept.this.matchesWordForm(word);
    }
  }
}
