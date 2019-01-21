package org.east.gui.workbench.treesFrame;

import javax.swing.tree.DefaultMutableTreeNode;

public abstract class DisplayableNodeImpl implements DisplayableNode{
  public void toTree(DefaultMutableTreeNode parent, TreeFactory tf){
    tf.addChild(parent,toTree(tf));
  }
}
