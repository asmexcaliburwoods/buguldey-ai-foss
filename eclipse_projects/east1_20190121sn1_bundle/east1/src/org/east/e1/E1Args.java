package org.east.e1;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.Serializable;

public class E1Args implements Serializable{
  public List getArgs(){
    return args;
  }
  private List args=new LinkedList();
  public LHSSeq getLHS(){
    return lhs;
  }
  private LHSSeq lhs;
  public String toString(){
    StringBuffer sb=new StringBuffer();
    Iterator it=args.iterator();
    while(it.hasNext()){
      String arg=(String)it.next();
      if(sb.length()>0)sb.append(",");
      sb.append(arg);
    }
    return sb.toString();
  }
  public String getArg1(){
    return (String)getArgs().get(0);
  }
}
