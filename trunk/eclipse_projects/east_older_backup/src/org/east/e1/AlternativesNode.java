package org.east.e1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public final class AlternativesNode{
  private static Stack stack=new Stack();
  private boolean fullyExplored;
  public void setNotAnAlt(boolean notAnAlt){
    this.notAnAlt=notAnAlt;
  }
  private boolean notAnAlt;
  private AlternativesNode parent;
  private AlternativesNode root;
  public boolean isFullyExplored(){
    return fullyExplored;
  }
  public boolean isNotAnAlt(){
    return notAnAlt;
  }
  public void setFullyExplored(boolean fullyExplored){
//    if(!isNotAnAlt()&&fullyExplored)System.out.println("Alt "+this+" is now fully explored");
    this.fullyExplored=fullyExplored;
  }
  private Object definingObject;
  public Object getDefiningObject(){
    return definingObject;
  }
  public AlternativesNode(AlternativesNode parent,Object definingObject){
    this.definingObject=definingObject;
    this.parent=parent;
    if(parent==null)root=this;
    else root=getRoot();
    stack.push(this);
  }
  public AlternativesNode getRoot(){
    if(root!=null)return root;
    return parent.getRoot();
  }
  private List alternatives=new ArrayList();
  public AlternativesNode getCreateAlternative(int n,Object definingObject){
    AlternativesNode node;
    if(alternatives.size()<=n){
      if(alternatives.size()!=n)
        throw new RuntimeException("alternatives.size must be ==n");
      node=new AlternativesNode(this,definingObject);
      alternatives.add(node);
    }else node=(AlternativesNode)alternatives.get(n);
    return node;
  }
  public String toString(){
    return "fullyExplored="+fullyExplored+";o="+definingObject;
  }
  private AlternativesNode peekAlt(){return (AlternativesNode)stack.peek();}
  private void removeFromStack(){
    int pos=stack.indexOf(this);
    if(pos==-1)return;
    while(stack.size()>pos){
      AlternativesNode node=peekAlt();
      node.removeFromParent();
      stack.pop();
    }
  }
  private void removeFromParent(){
    if(parent==null)throw new RuntimeException("root.removeFromParent() was called");
    parent.removeAllAfter(this);
  }
  private void removeAllAfter(AlternativesNode node){
    int pos=alternatives.indexOf(node);
    if(pos==-1)return;
    while(alternatives.size()>pos){
      alternatives.remove(alternatives.size()-1);
    }
  }
  public AlternativesNode getAltToMarkInactive(){
    while(true){
      if(stack.isEmpty())return root;
      AlternativesNode node=peekAlt();
      if(node.isFullyExplored()||node.isNotAnAlt()){node.removeFromStack();continue;}
      return node;
    }
  }
}
