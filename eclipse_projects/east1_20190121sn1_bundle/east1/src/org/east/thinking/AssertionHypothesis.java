package org.east.thinking;

import org.east.concepts.utility.Node;
import org.east.gui.workbench.treesFrame.TreeFactory;

import javax.swing.tree.DefaultMutableTreeNode;

public class AssertionHypothesis extends HypothesisImpl{
  private Node list;
  private static final String ASSERTION_HYPOTHESIS="assertion-hypothesis";
  public AssertionHypothesis(SetOfHypotheses set,Node list){
    super(set);
    this.list=list;
  }
  protected void assertImpl() throws Exception{
  }
  protected void retractImpl(){
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    DefaultMutableTreeNode assertion=tf.createTreeNode(ASSERTION_HYPOTHESIS);
    list.toTreeFlat(assertion,tf);
    return assertion;
  }
  public String hypothesisToString(){
    return ASSERTION_HYPOTHESIS+": "+list.toLispString();
  }
}
