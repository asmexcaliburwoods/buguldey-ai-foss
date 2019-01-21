package org.east.gui.workbench.treesFrame;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;

public interface DisplayableNode extends Serializable{
  void toTree(DefaultMutableTreeNode parent,TreeFactory tf);
  DefaultMutableTreeNode toTree(TreeFactory tf);
}
