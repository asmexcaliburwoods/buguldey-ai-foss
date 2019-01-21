package org.east.concepts.utility;

import org.east.text.parse.SentenceContext;
import org.east.text.parse.TextSectionContext;
import org.east.text.parse.SentenceParsingProcessContext;

import java.util.*;

public class Sentence extends TextSection{
  public Sentence getActiveForm(){
    return sentenceParsingProcessContext.getActiveSentenceForm();
  }
  private SentenceParsingProcessContext sentenceParsingProcessContext;
  public SentenceParsingProcessContext getSentenceParsingProcessContext(){
    return sentenceParsingProcessContext;
  }
  public void setSentenceParsingProcessContext(SentenceParsingProcessContext sentenceParsingProcessContext){
    if(this.sentenceParsingProcessContext!=null)throw new RuntimeException();
    this.sentenceParsingProcessContext=sentenceParsingProcessContext;
  }
  private SentenceContext sentenceContext;
  private TextSectionContext parentTextSectionContext;
  public static Sentence createFromWordList(List words,TextSectionContext parentTextSectionContext,Text parentText,SentenceParsingProcessContext sentenceParsingProcessContext){
    String spelling=wordsToString(words);
    Sentence sentence=new Sentence(spelling, words, parentTextSectionContext, parentText);
    sentence.setSentenceParsingProcessContext(sentenceParsingProcessContext);
    return sentence;
  }
  public static Sentence createFromSpelling(String sentence,TextSectionContext parentTextSectionContext,Text parentText){
    return new Sentence(sentence,parentTextSectionContext,parentText);
  }
  private Sentence(String sentence,List words,TextSectionContext parentTextSectionContext,Text parentText){
    super(null,sentence,parentText);
    this.sentenceContext=new SentenceContext(parentTextSectionContext,this);
    this.parentTextSectionContext=parentTextSectionContext;
    this.words=Collections.unmodifiableList(new ArrayList(words));
    this.title=createTitle();
  }
  public Sentence(String sentence,TextSectionContext parentTextSectionContext,Text parentText){
    super(null,sentence,parentText);
    this.sentenceContext=new SentenceContext(parentTextSectionContext,this);
    this.parentTextSectionContext=parentTextSectionContext;

    StringTokenizer st=new StringTokenizer(sentence,",;()[].!? \t\r\n",true);
    List words=new ArrayList();
    int pos=0;
    while(st.hasMoreTokens()){
      String tok=st.nextToken();
      if(tok.equals(" ")||tok.equals("\t")||tok.equals("\r")||tok.equals("\n"))
        continue;
      words.add(new Word(tok,this,pos));
      pos++;
    }
    this.words=Collections.unmodifiableList(new ArrayList(words));
    this.title=createTitle();
  }
  private String createTitle(){
    String s=toTitle();
    if(s.length()>256)s=s.substring(0,256)+"...";
    return s;
  }
  /** @return List of Word */
  public List getWords(){
    return words;
  }
  private final List words;
  public synchronized String toString(){
//    StringBuffer sb=new StringBuffer();
//    Iterator it=words.iterator();
//    boolean first=true;
//    boolean wasLeftPar=false;
//    while(it.hasNext()){
//      Word word=(Word)it.next();
//      if(!first&&!word.isDelimiter()&&!wasLeftPar)sb.append(" ");
//      if(!first&&(word.getSpelling().equals("[")||word.getSpelling().equals("(")))sb.append(" ");
//      sb.append(word.getSpelling());
//      wasLeftPar=word.getSpelling().equals("[")||word.getSpelling().equals("(");
//      first=false;
//    }
//    return sb.toString();
    return getTitle();
  }
  private static String wordsToString(List words){
    StringBuffer sb=new StringBuffer();
    Iterator it=words.iterator();
    boolean first=true;
    boolean wasLeftPar=false;
    while(it.hasNext()){
      Word word=(Word)it.next();
      if(!first&&!word.isDelimiter()&&!wasLeftPar)sb.append(" ");
      if(!first&&(word.getSpelling().equals("[")||word.getSpelling().equals("(")))sb.append(" ");
      sb.append(word.getSpelling());
      wasLeftPar=word.getSpelling().equals("[")||word.getSpelling().equals("(");
      first=false;
    }
    return sb.toString();
  }
  private String toTitle(){
    StringBuffer sb=new StringBuffer();
    Iterator it=words.iterator();
    boolean first=true;
    boolean wasLeftPar=false;
    while(it.hasNext()){
      Word word=(Word)it.next();
      if(!first&&!word.isDelimiter()&&!wasLeftPar)sb.append(" ");
      if(!first&&(word.getSpelling().equals("[")||word.getSpelling().equals("(")))sb.append(" ");
      sb.append(word.getSpelling());
      wasLeftPar=word.getSpelling().equals("[")||word.getSpelling().equals("(");
      first=false;
      if(sb.length()>128&&it.hasNext()){
        sb.append("...");
        break;
      }
    }
    return sb.toString();
  }
  public SentenceContext getSentenceContext(){
    return sentenceContext;
  }
  public TextSectionContext getParentTextSectionContext(){
    return parentTextSectionContext;
  }
}
