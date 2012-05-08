package org.east.concepts.utility;

import org.east.text.parse.TextContext;
import org.east.text.parse.TextSectionContext;
import org.east.text.TextUnderstanding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

public class Text extends TextSection{
  private List sections;
  private TextContext textContext;
  public Text(String title,String content,Text parentText){
    super(title,content,parentText);
    this.textContext=new TextContext(this);
    TextSectionContext textSectionContext=getTextSectionContext();
    List sections=new ArrayList();
    StringTokenizer st=new StringTokenizer(content,".!?",true);
    String sentence="";
    boolean eosFound=false;
    while(st.hasMoreTokens()){
      String s=st.nextToken();
      if(s.equals(".")){eosFound=true;sentence+=".";continue;}
      if(s.equals("!")){eosFound=true;sentence+="!";continue;}
      if(s.equals("?")){eosFound=true;sentence+="?";continue;}
      if(eosFound){
        Text.addSentenceIfNotNull(
                sections,
                parseSentence(textSectionContext,this,sentence));
        sentence="";
        eosFound=false;
      }
      sentence+=s;
    }
    Text.addSentenceIfNotNull(
            sections,
            parseSentence(textSectionContext,this,sentence));
    this.sections=Collections.unmodifiableList(new ArrayList(sections));
  }
  /** @return List of TextSection*/
  public List getSections(){
//    TextSection
    return sections;
  }
  public TextContext getTextContext(){
    return textContext;
  }
//  public synchronized void replaceBy(Object structuralElement,Node by){
//    int pos=structuralElements.indexOf(structuralElement);
//    if(pos==-1)
//      throw new IllegalArgumentException(
//              "structural element ("+structuralElement+") is not in this text: ("+this+")");
//  }
  public synchronized String toString(){
//    StringBuffer sb=new StringBuffer();
//    Iterator it=sections.iterator();
//    boolean first=true;
//    while(it.hasNext()){
//      Object se=it.next();
//      if(!first)sb.append("\n");
//      sb.append(se.toString());
//      first=false;
//    }
//    return sb.toString();
    return getTitle();
  }
//  public void addStructuralElement(Object o){
//    if(o instanceof Sentence){
//      Sentence s=(Sentence)o;
//      if(s.getWords().size()>0)structuralElements.add(s);
//    }else structuralElements.add(o);
//  }
  private static void addSentenceIfNotNull(List sectionsInText, Sentence s){
    if(s!=null)sectionsInText.add(s);
  }
  private static Sentence parseSentence(TextSectionContext parentSectionContext,Text parentText,String sentence){
    if(sentence.length()==0)return null;
    Sentence sentenceObject=new Sentence(sentence,parentSectionContext,parentText);
    if(sentenceObject.getWords().isEmpty())return null;
    return sentenceObject;
  }
  private TextUnderstanding textUnderstanding;
  public TextUnderstanding getTextUnderstanding(){
    return textUnderstanding;
  }
  public void setTextUnderstanding(TextUnderstanding textUnderstanding){
    if(this.textUnderstanding!=null)throw new RuntimeException();
    this.textUnderstanding=textUnderstanding;
  }
}
