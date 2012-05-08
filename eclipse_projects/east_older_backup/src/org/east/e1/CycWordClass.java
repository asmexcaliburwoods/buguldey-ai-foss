package org.east.e1;

import org.east.concepts.utility.CycConcept;
import org.east.concepts.utility.Sentence;
import org.east.concepts.utility.Word;
import org.east.cyc.CycLink;
import org.east.e1.semaction.Scope;
import org.east.pos.Denoting;
import org.east.pos.PartOfSpeech;
import org.opencyc.cycobject.CycFort;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class CycWordClass extends WordClass{
  private CycConcept[] cycConcepts;
  public CycConcept[] getCycConcepts(){
    return cycConcepts;
  }
  public String toString(){
    StringBuffer sb=new StringBuffer();
    for(int i=0;i<cycConcepts.length;i++){
      CycConcept cycConcept=cycConcepts[i];
      if(sb.length()>0)sb.append(",");
      sb.append(cycConcept);
    }
    return super.toString()+"/"+sb+"/";
  }
  public boolean matchesWordForm(PartOfSpeech pos) throws Exception{
    boolean match=false;
    for(int i=0;i<cycConcepts.length;i++){
      CycConcept cycConcept=cycConcepts[i];
      if(pos instanceof Denoting){
        Denoting d=(Denoting)pos;
        CycFort fort=d.getDenotat();
        if(fort.equals(cycConcept.getCycFort())||CycLink.isa(fort,cycConcept.getCycFort()))
          match=true;
      }
    }
    return match;
  }
  public CycWordClass(List wordForms,String wordForConcept,CycConcept[] cycConcepts, String instanceName){
    super(wordForms,wordForConcept,instanceName);
    this.cycConcepts=cycConcepts;
  }
  public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence,
                       Scope ctx) throws Exception, IOException{
    Word w=(Word)sentence.getWords().get(index.getIndex());
    index.setIndex(index.getIndex()+1);
    boolean match=false;
      for(int i=0;i<cycConcepts.length;i++){
        CycConcept cycConcept=cycConcepts[i];
        Iterator it=w.getWordForms().iterator();
        while(it.hasNext()){
          PartOfSpeech pos=(PartOfSpeech)it.next();
          if(!(pos instanceof Denoting))continue;
          Denoting d=(Denoting)pos;
          CycFort fort=d.getDenotat();
          if(fort.equals(cycConcept.getCycFort())||CycLink.isa(fort,cycConcept.getCycFort())){
            match=true;
            break;
          }
        }
        if(match)break;
      }
//    if(match){
//      if(getWordForms()!=null){
//        match=false;
//        Iterator it=getWordForms().iterator();
//        while(it.hasNext()){
//          Object wordForm=it.next();
//          if(wordForm instanceof CycConcept){
//            CycConcept wordFormCycConcept=(CycConcept)wordForm;
//            Iterator it2=new HashSet(w.getCycConcepts()).iterator();
//            while(it2.hasNext()){
//              CycConcept fort=(CycConcept)it2.next();
//              if(fort.equals(wordFormCycConcept)||
//                      fort.isa(wordFormCycConcept)){
//                match=true;
//                break;
//              }
//            }
//            if(match)break;
//          }
//        }
//        match=false;
//      }
//    }
    if(!match)index.setIndex(index.getIndex()-1);
    else{
      WordUtil.wordParsed(w,this,ctx);
      executeSemanticalAction(ctx);
    }
    return match;
  }
}
