package org.east.e1.semaction;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class SemanticalAction implements Serializable{
  private List statements=new ArrayList();
  public void addStatement(Statement s){
    statements.add(s);
  }
  public void execute(Scope ctx) throws Exception{
    Iterator it=statements.iterator();
    while(it.hasNext()){
      Statement statement=(Statement)it.next();
      statement.execute(ctx);
    }
  }
  public String toString(){
    StringBuffer sb=new StringBuffer("(. ");
    Iterator it=statements.iterator();
    while(it.hasNext()){
      Statement statement=(Statement)it.next();
      sb.append(statement).append("; ");
    }
    sb.append(".)");
    return sb.toString();
  }
}
