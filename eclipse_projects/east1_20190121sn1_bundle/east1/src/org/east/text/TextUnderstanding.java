package org.east.text;

import org.east.concepts.utility.Node;
import org.east.gui.workbench.treesFrame.DisplayableNodeImpl;
import org.east.gui.workbench.treesFrame.TreeFactory;

import javax.swing.tree.DefaultMutableTreeNode;

public class TextUnderstanding extends DisplayableNodeImpl{
  private Node assertionsForVerbs;
  public Node getAssertionsForVerbs(){
    return assertionsForVerbs;
  }
  public void setAssertionsForVerbs(Node assertionsForVerbs){
    this.assertionsForVerbs=assertionsForVerbs;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    DefaultMutableTreeNode tu=tf.named("text-understanding", tf.toTree(assertionsForVerbs));
    tf.addChild(tu,tf.toTree(textUnderstandingNode));
    return tu;
  }
  private Node textUnderstandingNode;
  public Node getTextUnderstandingNode(){
    return textUnderstandingNode;
  }
  public void setTextUnderstandingNode(Node textUnderstandingNode){
    this.textUnderstandingNode=textUnderstandingNode;
  }
}
