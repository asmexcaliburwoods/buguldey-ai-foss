package org.east.text.parse;

import org.east.concepts.utility.Node;
import org.east.concepts.utility.Sentence;
import org.east.thinking.Hypothesis;

import java.io.Serializable;

public class SentenceParsingProcessContext implements Serializable{
  private Node notes;
  public SentenceParsingProcessContext(Node notes,Sentence sentence){
    this.notes=notes;
    this.activeSentenceForm=sentence;
  }
  public Node getNotes(){
    return notes;
  }
  private Sentence activeSentenceForm;
  private Hypothesis activeHypothesis;
  public Hypothesis getActiveHypothesis(){
    return activeHypothesis;
  }
  public synchronized void addHypothesis(Hypothesis hypothesis){
    hypothesis.setPreviousHypothesis(activeHypothesis);
    activeHypothesis=hypothesis;
  }
  public Sentence getActiveSentenceForm(){
    return activeSentenceForm;
  }
  public void setActiveSentenceForm(Sentence activeSentenceForm){
    this.activeSentenceForm=activeSentenceForm;
  }
}
