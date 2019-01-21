package org.east.concepts.utility;

import org.east.gui.workbench.treesFrame.DisplayableNodeImpl;
import org.east.gui.workbench.treesFrame.TreeFactory;
import org.east.gui.workbench.treesFrame.DisplayableNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class NodeImpl extends DisplayableNodeImpl implements Serializable,Node{
  private String name;
  public NodeImpl(){
    this("...");
  }
  public NodeImpl(String name){
    this.name=name;
  }
  public String getName(){
    return name;
  }
  private Node parent;
  public Node getParent(){
    return parent;
  }
  public void setParent(Node parent){
    this.parent=parent;
  }
  private List links=new ArrayList(1);
  public List getLinks(){
    return links;
  }
  public void setLinks(List links){
    if(!this.links.isEmpty())throw new RuntimeException("overridden some links");
    this.links=links;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    DefaultMutableTreeNode list=tf.createTreeNode(toString());
    Iterator it=getLinks().iterator();
    while(it.hasNext()){
      Object link=it.next();
      if(link instanceof DisplayableNode){
        DisplayableNode dn=(DisplayableNode)link;
        tf.addChild(list,dn.toTree(tf));
        continue;
      }
      tf.addChild(list,link);
    }
    return list;
  }
  public void toTreeFlat(DefaultMutableTreeNode parent, TreeFactory tf){
    Iterator it=getLinks().iterator();
    while(it.hasNext()){
      Object link=it.next();
      if(link instanceof DisplayableNode){
        DisplayableNode dn=(DisplayableNode)link;
        tf.addChild(parent,dn.toTree(tf));
        continue;
      }
      tf.addChild(parent,link);
    }
  }
  public String toLispString(){
    StringBuffer sb=new StringBuffer("(");
    Iterator it=getLinks().iterator();
    while(it.hasNext()){
      Object link=it.next();
      if(sb.length()>1)sb.append(" ");
      if(link instanceof Node){
        Node node=(Node)link;
        sb.append(node.toLispString());
      }
      sb.append(link.toString());
    }
    sb.append(")");
    return sb.toString();
  }
  public String toString(){
    return name;
  }
}
