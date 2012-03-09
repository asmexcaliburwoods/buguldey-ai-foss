package org.east.concepts.utility;

import org.east.concepts.Concept;
import org.east.concepts.Name;
import org.east.cyc.CycLink;
import org.east.pos.PartOfSpeech;
import org.east.e1.ParsedWord;
import org.east.text.parse.SentenceParsingProcessContext;
import org.opencyc.cycobject.CycList;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class Word implements Serializable{
  private static final long serialVersionUID = -4744438504882353919L;
  private ParsedWord parent;
  public ParsedWord getParent(){
    return parent;
  }
  public void setParent(ParsedWord parent){
    this.parent=parent;
  }
  private boolean delimiter;
  private Sentence sentence;
  private int positionInSentence;
  public boolean isDelimiter(){
    return delimiter;
  }
  private String word;
  private boolean cycConceptFetched;
  private Set<PartOfSpeech> wordForms;
  private boolean acConceptFetched;
  private Concept acConcept;
  public synchronized Concept getAutocoderConcept(){
    if(!isDelimiter()&&!acConceptFetched){
      acConcept=Name.resolveSingleConcept(word);
      acConceptFetched=true;
    }
    return acConcept;
  }
  public synchronized Set<PartOfSpeech> getWordForms() throws Exception{
    if(!isDelimiter()&&!cycConceptFetched){
      boolean noWordLookup=CycLink.queryPredicate("(#$ea-noWordLookup \""+word.toLowerCase()+"\")");
      if(!noWordLookup){
        SentenceParsingProcessContext sentenceParsingProcessContext=sentence.getSentenceParsingProcessContext();
        enumCycConcepts(word,sentenceParsingProcessContext);
        String w=word.toLowerCase();
        if(!w.equals(word))enumCycConcepts(w,sentenceParsingProcessContext);
        String w2=word.toUpperCase();
        if(!w2.equals(word))enumCycConcepts(w2,sentenceParsingProcessContext);
        checkAmbiguous();
        checkUnknown();
      }
      cycConceptFetched=true;
    }
    if(wordForms==null)wordForms=new HashSet<PartOfSpeech>();
    return Collections.unmodifiableSet(wordForms);
  }
  private void checkAmbiguous(){
    if(wordForms.size()>1)
      System.out.println("More than one meaning for a word "+this+":\r\n  "+wordForms);
  }
  private void checkUnknown(){
    if(wordForms.isEmpty())
      System.out.println("Unknown word "+this+":\r\n  "+wordForms);
  }
  private void enumCycConcepts(String word,SentenceParsingProcessContext sentenceParsingProcessContext) throws Exception, IOException{
    CycList wh=CycLink.getWordHints(word);
//    System.out.println(wh.get(0));
    if(wh!=null&&wh.contains(CycLink.getMultiwordHintConcept())){
      String[] multiwordSpellings=CycLink.getMultiwordSpellings(word);
      if(multiwordSpellings!=null&&multiwordSpellings.length>0){
        for(int i=0;i<multiwordSpellings.length;i++){
          String spelling=multiwordSpellings[i];
          if(spellingMatches(spelling,sentenceParsingProcessContext))return;
        }
//        System.out.println(multiwordSpellings[0]);
      }
    }
//    CycConcept[] cycConcepts=CycLink.resolveMultipleConcepts(word);
//    if(cycConcepts!=null)
//      for(int i=0;i<cycConcepts.length;i++){
//        CycConcept cycConcept=cycConcepts[i];
//        this.wordForms.add(cycConcept);
//      }
    Set wf=CycLink.getWordForms(word);
    if(wordForms==null)wordForms=wf;
    else wordForms.addAll(wf);
  }
  private boolean spellingMatches(String term,SentenceParsingProcessContext sentenceParsingProcessContext) throws Exception, IOException{
    StringTokenizer st=new StringTokenizer(term," \t\r\n");
    int pos=positionInSentence;
    List<Word> words=new ArrayList<Word>(sentence.getWords());
    while(st.hasMoreTokens()){
      String termWord=st.nextToken();
      Iterator<Word> it=words.iterator();
      Word word1=null;
      while(it.hasNext()){
        word1=it.next();
        if(word1.positionInSentence>=pos)break;
      }
      if(word1==null||word1.positionInSentence!=pos)return false;
      if(!termWord.equalsIgnoreCase(word1.word))return false;
      pos++;
    }
    //matches, apply it.
    word=term;
    acConceptFetched=false;
    st=new StringTokenizer(term," \t\r\n");
    pos=positionInSentence;
    while(st.hasMoreTokens()){
      st.nextToken();
      Iterator<Word> it=words.iterator();
      Word word1=null;
      while(it.hasNext()){
        word1=it.next();
        if(word1.positionInSentence>=pos)break;
      }
      if(pos>positionInSentence)
        words.remove(word1);
      pos++;
    }
    if(wordForms==null)wordForms=new HashSet<PartOfSpeech>();
    wordForms.addAll(CycLink.getWordForms(term));
    checkAmbiguous();
    Sentence newSentence=Sentence.createFromWordList(
            words,
            sentence.getParentTextSectionContext(),
            sentence.getParentText(),
            sentenceParsingProcessContext);
    sentenceParsingProcessContext.setActiveSentenceForm(newSentence);
    return true;
  }
  public String getSpelling(){
    return word;
  }
  public Sentence getSentence(){
    return sentence;
  }
  public int getPositionInSentence(){
    return positionInSentence;
  }
  public Word(Word word,Set<PartOfSpeech> wordForms){
    this(word.getSpelling(),word.getSentence(),word.getPositionInSentence());
    this.cycConceptFetched=true;
    this.wordForms=new HashSet<PartOfSpeech>();
    this.wordForms.addAll(wordForms);
  }
  public Word(String spelling,Set<PartOfSpeech> wordForms){
    this(spelling,null,-1);
    this.cycConceptFetched=true;
    this.wordForms=new HashSet<PartOfSpeech>();
    this.wordForms.addAll(wordForms);
  }
  public Word(String word,Sentence sentence,int positionInSentence){
    this.word=word;
    this.sentence=sentence;
    this.positionInSentence=positionInSentence;
    if(word.equals("."))this.delimiter=true;else
    if(word.equals("!"))this.delimiter=true;else
    if(word.equals("?"))this.delimiter=true;else
    if(word.equals(";"))this.delimiter=true;else
    if(word.equals(","))this.delimiter=true;else
    if(word.equals("-"))this.delimiter=true;else
    if(word.equals("["))this.delimiter=true;else
    if(word.equals("]"))this.delimiter=true;else
    if(word.equals("("))this.delimiter=true;else
    if(word.equals(")"))this.delimiter=true;else
    if(word.equals(":"))this.delimiter=true;
  }
  public String toString(){
    return word+" (position in sentence: "+positionInSentence+")";
  }
  public boolean equals(Object o){
    if(o==null||!(o instanceof Word))return false;
    Word w=(Word)o;
    return w.word.equals(word)&&
            w.positionInSentence==positionInSentence&&
            sentence.equals(w.sentence);
  }
  public void removeWordForm(PartOfSpeech pos,String reasonForRemoval){
    System.out.println("Removed word form "+pos+" from the word "+this+"; reason:\r\n  "+reasonForRemoval);
    wordForms.remove(pos);
  }
  public void restoreWordForm(PartOfSpeech pos,String reasonForRestore){
    System.out.println("Restored word form "+pos+" from the word "+this+"; reason:\r\n  "+reasonForRestore);
    wordForms.add(pos);
  }
}
