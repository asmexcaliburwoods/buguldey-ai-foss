package org.east.text.parse;

import org.east.concepts.utility.Node;

import java.util.Iterator;

public class AssignParents{
  public static void assignParents(Node parsedSentence) throws Exception{
    setParents(null,parsedSentence);
  }
  private static void setParents(Node parent,Node lo) throws Exception{
    lo.setParent(parent);
    Iterator it=lo.getLinks().iterator();
    while(it.hasNext()){
      Object n=it.next();
      if(n instanceof Node){
        Node o=(Node)n;
        setParents(lo,o);
      }
    }
  }
}
