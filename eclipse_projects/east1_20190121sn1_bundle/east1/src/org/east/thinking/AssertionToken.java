package org.east.thinking;

import org.east.gui.workbench.treesFrame.DisplayableNodeImpl;
import org.east.gui.workbench.treesFrame.TreeFactory;

import javax.swing.tree.DefaultMutableTreeNode;

public class AssertionToken extends DisplayableNodeImpl{
  private String tokenName;
  public AssertionToken(String tokenName){
    this.tokenName=tokenName;
  }
  public String getTokenName(){
    return tokenName;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    return tf.createTreeNode(tokenName);
  }
  public String toString(){
    return tokenName;
  }
}
