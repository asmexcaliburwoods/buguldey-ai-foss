package org.east.e1;

import org.east.concepts.utility.Sentence;
import org.east.concepts.utility.Word;

public class IndexInSentence{
  public Sentence getSentence(){
    return sentence;
  }
  private Sentence sentence;
  public void setIndex(int index){
    this.index=index;
  }
  public int getIndex(){
    return index;
  }
  private int index;
  public IndexInSentence(Sentence s){
    this.sentence=s;
  }
  public String toString(){
    return "index="+index+", word="+(sentence.getWords().size()<=index?"N/A":((Word)sentence.getWords().get(index)).getSpelling());
  }
}
