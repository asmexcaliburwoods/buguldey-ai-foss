package org.east.e1;

import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Sentence;
import org.east.concepts.utility.Word;
import org.east.pos.Denoting;
import org.east.pos.PartOfSpeech;
import org.opencyc.cycobject.CycFort;
import org.opencyc.cycobject.CycNart;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class NartUnifier extends WordClass{
  public NartUnifier(List wordForms, String wordForConcept, String instanceName){
    super(wordForms, wordForConcept, instanceName);
    if(!getWordForConcept().startsWith("(")||!getWordForConcept().endsWith(")"))
      throw new RuntimeException("NART must be surrounded by round brackets");
  }
  public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx) throws Exception, IOException{
    Word w=(Word)sentence.getWords().get(index.getIndex());
    index.setIndex(index.getIndex()+1);
    Iterator it=w.getWordForms().iterator();
    boolean match=false;
    while(it.hasNext()){
      PartOfSpeech pos=(PartOfSpeech)it.next();
      if(matchesWordForm(pos)){
        match=true;
        break;
      }
    }
    if(match){
      WordUtil.wordParsed(w,this,ctx);
      executeSemanticalAction(ctx);
    }
    return match;
  }
  public boolean matchesWordForm(PartOfSpeech pos){
    if(!(pos instanceof Denoting))return false;
    Denoting dwf=(Denoting)pos;
    CycFort cycFort=dwf.getDenotat();
    if(!(cycFort instanceof CycNart))return false;
    String s=cycFort.cyclify();
    String realNart=s.substring(1,s.length()-1);
    String wfc=getWordForConcept();
    String maskNart=getWordForConcept().substring(1,wfc.length()-1);
    SmartTokenizer st1=new SmartTokenizer(realNart);
    SmartTokenizer st2=new SmartTokenizer(maskNart);
    while(st1.hasMoreTokens()){
      if(!st2.hasMoreTokens())return false;
      String t1=st1.nextToken();
      String t2=st2.nextToken();
      if(t2.startsWith("?"))continue;
      if(t2.equals(t1))continue;
      return false;
    }
    if(st2.hasMoreTokens())return false;
    return true;
  }
}
