package org.east.e1;

import org.east.concepts.utility.NodeImpl;
import org.east.concepts.utility.Word;
import org.east.pos.PartOfSpeech;
import org.east.util.ExceptionUtil;

import java.util.Iterator;

public class ParsedWord extends NodeImpl{
  private Word word;
  private WordClass wordClass;
  public ParsedWord(Word w, WordClass wordClass){
    this.word=w;
    this.wordClass=wordClass;
  }
  public void setWord(Word word){
    this.word=word;
  }
  public void setWordClass(WordClass wordClass){
    this.wordClass=wordClass;
  }
  public Word getWord(){
    return word;
  }
  public WordClass getWordClass(){
    return wordClass;
  }
  public String toString(){
    StringBuffer sb=new StringBuffer(getWord().getSpelling()+" [");
    try{
      Iterator it=getWord().getWordForms().iterator();
      boolean first=true;
      while(it.hasNext()){
        PartOfSpeech wf=(PartOfSpeech)it.next();
        if(!first)sb.append(",");
        first=false;
        sb.append(wf.toString());
      }
    }catch(Exception e){
      ExceptionUtil.handleException(e);
    }
    sb.append("]");
    if(wordClass!=null)sb.append(", class: ").append(getWordClass().getWordForConcept());
    return sb.toString();
  }
  /** Must match Object.equals(Object), otherwise we will remove
   * incorrect objects on Node.getLinks().remove(Object) */
  public final boolean equals(Object o){
    return super.equals(o);
//    if(o==null||!(o instanceof ParsedWord))
//      return false;
//    ParsedWord pw=(ParsedWord)o;
//    return pw.word.getSpelling().equalsIgnoreCase(word.getSpelling());
  }
}
