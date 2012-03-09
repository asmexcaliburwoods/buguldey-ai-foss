package org.east.concepts.utility;

import org.east.gui.workbench.treesFrame.TreeFactory;
import org.east.gui.workbench.treesFrame.DisplayableNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;
import java.util.List;

public interface Node extends Serializable, DisplayableNode{
  List<Object> getLinks();
  void setLinks(List<Object> links);
  Node getParent();
  void setParent(Node parent);
  void toTreeFlat(DefaultMutableTreeNode parent, TreeFactory tf);
  String toLispString();
}
