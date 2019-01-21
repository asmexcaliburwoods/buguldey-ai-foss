package org.east.modeling;

import java.util.List;
import java.util.LinkedList;

public class SequenceOfSituations{
  public static class TransitionAndSituation{
    private Transition transition;
    public Transition getTransition(){
      return transition;
    }
    public void setTransition(Transition transition){
      this.transition=transition;
    }
    private Situation situation;
    public Situation getSituation(){
      return situation;
    }
    public void setSituation(Situation situation){
      this.situation=situation;
    }
  }
  public void setFirstSituation(Situation firstSituation){
    this.firstSituation=firstSituation;
  }
  private Situation firstSituation;
  public Situation getFirstSituation(){
    return firstSituation;
  }
  private List listOfTransitionsAndSituations=new LinkedList();
  /** @return List of class TransitionAndSituation */
  public List getListOfTransitionsAndSituations(){
    return listOfTransitionsAndSituations;
  }
}
