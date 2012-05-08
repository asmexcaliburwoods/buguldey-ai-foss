package org.east.e1;

import org.east.e1.semaction.Scope;
import org.east.concepts.utility.CycConcept;
import org.east.concepts.utility.Sentence;
import org.east.concepts.Name;
import org.east.concepts.Concept;
import org.east.concepts.WordClassConcept;
import org.east.cyc.CycLink;
import org.east.util.ExceptionUtil;
import org.opencyc.api.CycApiException;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class RuleLabelReferenceOrWordClass extends LHSSequenceElement{
  private String literal;
  private int lineNumber;
  private int colNumber;
  private String fileName;
  public boolean isNart(){
    return nart;
  }
  private boolean nart;
  public List getWordForms(){
    return wordForms;
  }
  private List wordForms;
  public String getLiteral(){
    return literal;
  }
  public E1Args getArgs(){
    return args;
  }
  private E1Args args;
  public RuleLabelReferenceOrWordClass(
          List wordForms,int lineNumber,int colNumber,String fileName,
          String literal,boolean nart,E1Args args){
    this.literal=literal;
    this.nart=nart;
    this.args=args;
    this.lineNumber=lineNumber;
    this.colNumber=colNumber;
    this.fileName=fileName;
    this.wordForms=wordForms;
  }
  public void resolveRuleLabelReferenceOrWordClass(E1Language e1,LHSSequenceElement parent) throws CycApiException, IOException{
    Iterator it=e1.getRules().iterator();
    List rulesFound=new LinkedList();
    while(it.hasNext()){
      E1Rule rule=(E1Rule)it.next();
      if(rule.getRuleId().equals(literal)&&rule.getFormalArgs().getArgs().size()==getArgs().getArgs().size()){
        rulesFound.add(rule);
      }
    }
    int alts=rulesFound.size()==0?0:1;
    if(getArgs().getArgs().size()==1){
      Concept concept=Name.resolveSingleConcept(literal);
      if(concept!=null)alts++;
      CycConcept cc=CycLink.resolveSingleConcept(literal);
      if(cc!=null)alts++;
      NartUnifier nartu=nart?new NartUnifier(wordForms,literal,(String)args.getArgs().get(0)):null;
      if(nartu!=null)alts++;
      if(alts>1)
        throw new RuntimeException(fileName+": ambiguous RuleLabelReferenceOrWordClass "+literal+getArgs()+", line "+lineNumber+", col "+colNumber);
      if(cc!=null){
        parent.replaceChild(this,new CycWordClass(wordForms,literal,new CycConcept[]{cc},(String)args.getArgs().get(0)));
        return;
      }
      if(nartu!=null){
        parent.replaceChild(this,nartu);
        nartu.setSemanticalAction(getSemanticalAction());
        return;
      }
      if(concept!=null){
        if(!(concept instanceof WordClassConcept))
          throw new RuntimeException(fileName+": "+literal+" must be instanceof WordClassConcept, line "+lineNumber+", col "+colNumber);
        WordClassConcept c=(WordClassConcept)concept;
        if(wordForms!=null&&!wordForms.isEmpty())
          throw new RuntimeException(fileName+": wordForms list must be empty for wordClass "+literal+", line "+lineNumber+", col "+colNumber);
        WordClassConcept.Instance newChild=c.newInstance((String)args.getArgs().get(0), literal);
        parent.replaceChild(this,newChild);
        newChild.setSemanticalAction(getSemanticalAction());
        return;
      }
    }
    if(rulesFound.size()==0)
      throw new RuntimeException(fileName+": non-existing RuleLabelReferenceOrWordClass "+literal+"("+getArgs()+"), line "+lineNumber+", col "+colNumber);
    if(wordForms!=null&&wordForms.size()>0)
      throw new RuntimeException(fileName+": wordForms are not allowed for RuleLabelReference, line "+lineNumber+", col "+colNumber);
    RuleLabelReference newChild=new RuleLabelReference(literal, args, rulesFound);
    parent.replaceChild(this,newChild);
    newChild.setSemanticalAction(getSemanticalAction());
  }
  public void replaceChild(LHSSequenceElement child, LHSSequenceElement newChild){
    ExceptionUtil.unsupportedOperation();
  }
  public boolean match(AlternativesNode node, IndexInSentence index, Sentence sentence, Scope ctx){
    ExceptionUtil.unsupportedOperation();
    return false;
  }
}
