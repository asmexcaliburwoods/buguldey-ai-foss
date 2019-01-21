package org.east.concepts.utility;

import org.east.concepts.UIDGenerator;
import org.east.gui.workbench.treesFrame.DisplayableNodeImpl;
import org.east.gui.workbench.treesFrame.TreeFactory;
import org.east.cyc.CycLink;

import javax.swing.tree.DefaultMutableTreeNode;

public class Context extends DisplayableNodeImpl{
  private UIDGenerator.UID uid;
  private String name;
  private String displayableNodeVisualTag;
  protected Context(String constantNameTag,String displayableNodeVisualTag){
    this.uid=UIDGenerator.getInstance().createUID();
    this.name=CycLink.getEastProjectConstantNamePrefix()+constantNameTag+"-"+uid;
    this.displayableNodeVisualTag=displayableNodeVisualTag;
  }
  public UIDGenerator.UID getUID(){
    return uid;
  }
  public String getName(){
    return name;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    DefaultMutableTreeNode name=tf.createTreeNode(this.name);
    return tf.named(displayableNodeVisualTag,tf.named("name",name));
  }
  protected String getDisplayableNodeVisualTag(){
    return displayableNodeVisualTag;
  }
}
