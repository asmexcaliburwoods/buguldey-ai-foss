package org.east.implementSoftware;

import org.east.goals.Goal;
import org.east.reasons.popupGoalReasons.GoalReason;
import org.east.reasons.popupDesireReasons.DesireReason;
import org.east.concepts.FileConcept;
import org.east.concepts.TextUnderstandingArc;
import org.east.concepts.utility.Text;
import org.east.text.TextUnderstandingEngine;
import org.east.gui.workbench.treesFrame.TreesFrame;
import org.east.util.ExceptionUtil;
import org.east.e1.E1Parser;

import java.io.File;

public class ImplementSoftwareGoal implements Goal{
  private String specificationFileName;
  private GoalReason reasonForGoal;
  private boolean reached;
  private boolean unreachable;
  private FileConcept fc;
  private TextUnderstandingArc arc;
  private boolean textRead;
  private boolean textParsed;
  private long textFileLastModified;
  public ImplementSoftwareGoal(String specificationFileName, GoalReason reasonForGoal){
    this.specificationFileName=specificationFileName;
    this.reasonForGoal=reasonForGoal;
  }
  private void readText(){
    try{
      if(fc==null)fc=FileConcept.define(specificationFileName);
      if(arc==null)arc=fc.getTextUnderstandingArc();
      File textFile=new File(fc.getFileName());
      if(textFileLastModified==0)textFileLastModified=textFile.lastModified();
      if(E1Parser.isNLParserUpdated()||textFileLastModified!=arc.getLastUpdated()){
        arc.setText(new Text(fc.getFileName(),fc.getContent(),null));
      }
      textRead=true;
    }catch(Throwable tr){
      unreachable=true;
      ExceptionUtil.handleException(tr);
    }
  }
  public void stepToCompletion(){
    if(!textRead)readText();
    else if(!textParsed)parseText();
    else if(!reached){
      deriveCode();
      reached=true;
    }
    else throw new RuntimeException("already reached");
  }
  public boolean shouldNowRunStepToCompletion(){
    return !isReached();
  }
  private void deriveCode(){
    try{
      CodingEngine.deriveCodeFromModel(arc);
    }catch(Throwable tr){
      unreachable=true;
      ExceptionUtil.handleException(tr);
    }
  }
  private void parseText(){
    try{
      if(fc==null)fc=FileConcept.define(specificationFileName);
      if(arc==null)arc=fc.getTextUnderstandingArc();
      File textFile=new File(fc.getFileName());
      if(textFileLastModified==0)textFileLastModified=textFile.lastModified();
      if(E1Parser.isNLParserUpdated()||textFileLastModified!=arc.getLastUpdated()){
        TextUnderstandingEngine.understandTextAndDeriveModel(arc);
        arc.setLastUpdated(textFileLastModified);
      }else
        TreesFrame.addTree("TextUnderstanding",arc.getText().getTextUnderstanding(),"Internal error");
  //      CodingEngine.deriveCodeFromModel(sip);
  //      CycConcept tcp;
  //      System.out.println(tcp=CycLink.resolveSingleConcept("TCP"));
  //      CycConcept protocol;
  //      System.out.println(protocol=CycLink.resolveSingleConcept("protocol"));
  //      System.out.println(tcp.isa(protocol));
  //      CycList wh=CycLink.getWordHints("echo");
  //      System.out.println(wh.get(0));
  //      if(wh.contains(CycLink.getMultiwordHintConcept()))
  //        System.out.println(CycLink.getMultiwordSpellings("echo")[0]);
  //      CycLink.resolveSingleConcept("echo server");
      //CycLink.resolveSingleConcept("pronoun");
      textParsed=true;
    }catch(Throwable tr){
      unreachable=true;
      ExceptionUtil.handleException(tr);
    }
  }
  public GoalReason getGoalReason(){
    return reasonForGoal;
  }
  public boolean isReached(){
    return reached;
  }
  public DesireReason getDesireReason(){
    return getGoalReason();
  }
  public boolean isEstimatedAsReachable(){
    return !unreachable;
  }
  public boolean isEstimatedAsUnreachable(){
    return unreachable;
  }
  public boolean isFulfilled(){
    return isReached();
  }
}
