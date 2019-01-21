package org.east.gui.workbench.treesFrame;

import org.east.Constants;
import org.east.concepts.utility.Node;
import org.east.gui.util.ScreenBoundsUtil;
import org.east.text.parse.IdentifyEntities;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class TreesFrame extends JFrame implements TreeFactory{
  public DefaultMutableTreeNode createTreeNode(Object displayName){
    return new DefaultMutableTreeNode(displayName==null?"null":displayName.toString());
  }
  private static class Link{
    Node from,to;
  }
  private static TreesFrame instance=new TreesFrame();
  private DefaultMutableTreeNode rootNode=new DefaultMutableTreeNode();
  private DefaultTreeModel treeModel=new DefaultTreeModel(rootNode);
  private JTree tree=new JTree(treeModel);
  private List shown=new ArrayList();
  private TreesFrame(){
    super("Trees - "+Constants.TITLE_WITH_VERSION);
    setBounds(ScreenBoundsUtil.getScreenBounds());
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(new JScrollPane(tree),BorderLayout.CENTER);
    tree.setRootVisible(false);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }
  public static void addTree(String treeName,DisplayableNode tree,String reasonForNullTree){
    instance.addTree0(treeName,tree,reasonForNullTree);
    if(!instance.isVisible())instance.show();
  }
  private void addTree0(String treeName, DisplayableNode tree,String reasonForNullTree){
    shown.clear();
    DefaultMutableTreeNode snode=addChild(rootNode,treeName);
    if(tree==null)
      addChild(snode,reasonForNullTree);
    else
      recurse(snode,tree);
    expandAll(snode,true);
  }
  private void expandAll(TreeNode node,boolean topLevel){
    if(!topLevel)tree.expandPath(new TreePath(treeModel.getPathToRoot(node)));
    Enumeration children=node.children();
    while(children.hasMoreElements()){
      TreeNode child=(TreeNode)children.nextElement();
      expandAll(child,false);
    }
  }
  private void recurse(DefaultMutableTreeNode parent, Node from,List links){
    Iterator it=links.iterator();
    while(it.hasNext()){
      Object next=it.next();
      if(next instanceof Node){
        Node to=(Node)next;
        if(isShown(from,to))continue;
        Link link=new Link();
        link.from=from;
        link.to=to;
        shown.add(link);
        recurse(parent,to);
      }else if(next instanceof IdentifyEntities.Object){
        IdentifyEntities.Object to=(IdentifyEntities.Object)next;
        recurse(parent, to);
      } else{
        DisplayableNode to=(DisplayableNode)next;
        recurse(parent, to);
      }
    }
  }
  private void recurse(DefaultMutableTreeNode parent, Node lo){
    if(lo instanceof DisplayableNode){
      DisplayableNode dn=(DisplayableNode)lo;
      addChild(parent,dn.toTree(this));
    }
    else
      recurse(addChild(parent,lo),lo,lo.getLinks());
  }
  private void recurse(DefaultMutableTreeNode parent, IdentifyEntities.Object lo){
    recurse(addChild(parent,lo),lo.getTree());
  }
  private void recurse(DefaultMutableTreeNode parent, DisplayableNode lo){
    addChild(parent,lo.toTree(this));
  }
  public DefaultMutableTreeNode addChild(DefaultMutableTreeNode parent,Object lo){
    DefaultMutableTreeNode child=createTreeNode(lo.toString());
    addChild(parent, child);
    return child;
  }
  public DefaultMutableTreeNode named(String name, DefaultMutableTreeNode namedChild){
    DefaultMutableTreeNode nameNode=createTreeNode(name);
    addChild(nameNode,namedChild);
    return nameNode;
  }
  public DefaultMutableTreeNode named(String name, Object namedChild){
    return named(name,createTreeNode(namedChild));
  }
  public DefaultMutableTreeNode toTree(DisplayableNode node){
    return node==null?createTreeNode(null):node.toTree(this);
  }
  public void addChild(DefaultMutableTreeNode parent, DefaultMutableTreeNode child){
    parent.add(child);
    treeModel.nodesWereInserted(parent,new int[]{parent.getChildCount()-1});
  }
  public void addNamedChild(DefaultMutableTreeNode parent, String name, DefaultMutableTreeNode child){
    addChild(parent,named(name,child));
  }
  public void addNamedChild(DefaultMutableTreeNode parent, String name, Object childDisplayName){
    addChild(parent,named(name,childDisplayName));
  }
  private boolean isShown(Node from, Node to){
    Iterator it=shown.iterator();
    while(it.hasNext()){
      Link link=(Link)it.next();
      if((link.from==from)&&(link.to==to))return true;
    }
    return false;
  }
}
