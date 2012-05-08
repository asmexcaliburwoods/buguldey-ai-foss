package org.east.gui.workbench.treesFrame;

import javax.swing.tree.DefaultMutableTreeNode;

public interface TreeFactory{
  DefaultMutableTreeNode createTreeNode(Object displayName);
  void addChild(DefaultMutableTreeNode parent,DefaultMutableTreeNode child);
  void addNamedChild(DefaultMutableTreeNode parent,String name,DefaultMutableTreeNode child);
  void addNamedChild(DefaultMutableTreeNode parent,String name,Object childDisplayName);
  /** @return created child */
  DefaultMutableTreeNode addChild(DefaultMutableTreeNode parent, Object childDisplayName);
  DefaultMutableTreeNode named(String name,DefaultMutableTreeNode namedChild);
  DefaultMutableTreeNode named(String name,Object namedChild);
  DefaultMutableTreeNode toTree(DisplayableNode node);
}
