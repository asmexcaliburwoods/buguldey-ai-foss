package org.east.concepts.utility;

import org.east.gui.workbench.treesFrame.DisplayableNodeImpl;
import org.east.gui.workbench.treesFrame.TreeFactory;
import org.east.concepts.UIDGenerator;

import javax.swing.tree.DefaultMutableTreeNode;

public class Titled extends DisplayableNodeImpl{
  private static final long serialVersionUID = -7570761858045555856L;
  public static String createUntitledTitle(){
    return "Untitled-"+UIDGenerator.getInstance().createUID();
  }
  protected String title;
  protected Titled(String title){
    this.title=title;
  }
  public String getTitle(){
    if(title==null)throw new RuntimeException();
    return title;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    return tf.createTreeNode(getTitle());
  }
}
