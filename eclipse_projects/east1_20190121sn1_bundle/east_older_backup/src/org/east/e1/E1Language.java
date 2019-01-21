package org.east.e1;

import org.east.e1.semaction.Scope;
import org.east.concepts.utility.Sentence;
import org.east.concepts.utility.Node;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class E1Language implements Serializable{
  public List getTopLevelRules(){
    return topLevelRules;
  }
  private List topLevelRules=new LinkedList();
  private String topLevelRuleId;
  public List getRules(){
    return Collections.unmodifiableList(rules);
  }
  private List rules=new LinkedList();
  public void setTopLevelRuleId(String ruleId){
    topLevelRuleId=ruleId;
  }
  public String getTopLevelRuleId(){
    return topLevelRuleId;
  }
  public void addRule(E1Rule rule){
    rules.add(rule);
  }
  public Object match(IndexInSentence index, Sentence sentence, Node sentenceParsingProcess) throws Exception, IOException{
    AlternativesNode node=new AlternativesNode(null,this);
    System.out.println("PARSING "+sentence);
    Object matchFound=null;
    int i=0;
    while(true){
      if(i>=getTopLevelRules().size())break;
      AlternativesNode node_i=node.getCreateAlternative(i,getTopLevelRules().get(i));
      if(node_i.isFullyExplored()){i++;continue;}
      E1Rule rule=(E1Rule)node_i.getDefiningObject();
      index.setIndex(0);
      Scope ctx=new Scope(this);
      sentence=sentence.getActiveForm();
      boolean partialMatch=rule.match(node_i, index, sentence,ctx);
      if(partialMatch){
        sentence=sentence.getActiveForm();
        if(index.getIndex()==sentence.getWords().size()){
          System.out.println("Match found.");
          matchFound=ctx.getReturnValue();
          break;
        }
      }
//      markLastAltAsExplored(node_i);
      break;
    }
    if(matchFound==null)System.out.println("Cannot parse sentence.");
    return matchFound;
  }
  private void markLastAltAsExplored(AlternativesNode node){
    node=node.getAltToMarkInactive();
    System.out.println("Alt was: "+node+"; now fexp==true.");
    node.setFullyExplored(true);
    Thread.yield();
  }
}
