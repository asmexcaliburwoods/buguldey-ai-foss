package org.east.thinking;

import org.east.gui.workbench.treesFrame.TreeFactory;
import org.east.gui.workbench.treesFrame.DisplayableNodeImpl;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.List;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Iterator;

public abstract class HypothesisImpl extends DisplayableNodeImpl implements Hypothesis{
  private SetOfHypotheses set;
  private HypothesisSandbox sandbox;
  private Hypothesis previousHypothesis;
  public HypothesisImpl(SetOfHypotheses set,HypothesisSandbox sandbox){
    this.set=set;
    this.sandbox=sandbox;
    sandbox.setHypothesis(this);
    set.addHypothesis(this);
  }
  public HypothesisImpl(SetOfHypotheses set){
    this(set,new HypothesisSandboxImpl());
  }
  private boolean valid,invalid;
  private List consequences=new LinkedList();
  public List getConsequences(){
    return Collections.unmodifiableList(consequences);
  }
  public void addConsequence(Assertion a){
    consequences.add(a);
  }
  public SetOfHypotheses getSetOfHypotheses(){
    return set;
  }
  public final void doAssert() throws Exception{
    valid=true;
    assertImpl();
  }
  protected abstract void assertImpl() throws Exception;
  protected abstract void retractImpl();
  public final void doRetract(String reason) throws Exception{
    System.out.println("Retracting hypothesis "+this+";\r\nreason: "+reason);
    invalid=true;
    retractImpl();
    Iterator it=consequences.iterator();
    while(it.hasNext()){
      Assertion a=(Assertion)it.next();
      a.doRetract("Retracted hypothesis "+this);
    }
    set.pickNextAlternative();
  }
  public void setPreviousHypothesis(Hypothesis h){
    if(this.previousHypothesis!=null)throw new RuntimeException("previousHypothesis: "+previousHypothesis+"; h: "+h);
    this.previousHypothesis=h;
  }
  public boolean isValid(){
    return valid;
  }
  public boolean isInvalid(){
    return invalid;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    DefaultMutableTreeNode h=tf.createTreeNode("hypothesis");
    tf.addChild(h,hypothesisToString());
    return h;
  }
  public String toString(){
    return hypothesisToString();
  }
  public HypothesisSandbox getSandbox(){
    return sandbox;
  }
  public Hypothesis getPreviousHypothesis(){
    return previousHypothesis;
  }
}
