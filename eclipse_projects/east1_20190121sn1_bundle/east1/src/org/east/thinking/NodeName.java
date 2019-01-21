package org.east.thinking;

import org.east.gui.workbench.treesFrame.DisplayableNode;
import org.east.gui.workbench.treesFrame.TreeFactory;
import org.east.gui.workbench.treesFrame.DisplayableNodeImpl;

import javax.swing.tree.DefaultMutableTreeNode;

public class NodeName extends DisplayableNodeImpl{
  private String name;
  private Object namedNode;
  public NodeName(String name,Object namedNode){
    this.name=name;
    this.namedNode=namedNode;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    DefaultMutableTreeNode name=tf.createTreeNode(this.name);
    if(namedNode instanceof DisplayableNode)
      tf.addChild(name,((DisplayableNode)namedNode).toTree(tf));
    else
      tf.addChild(name,namedNode);
    return name;
  }
}
