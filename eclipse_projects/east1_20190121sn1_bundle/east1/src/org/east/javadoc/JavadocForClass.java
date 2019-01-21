package org.east.javadoc;

import org.east.util.ExceptionUtil;
import org.east.concepts.Concept;

import java.util.List;
import java.util.LinkedList;

public class JavadocForClass extends Concept{
  private String fullyQualifiedClassName;
  private JavadocConfig config;
  private boolean parsed;
  private List methods=new LinkedList();

  public JavadocForClass(JavadocConfig config,String fullyQualifiedClassName){
    this.fullyQualifiedClassName=fullyQualifiedClassName;
    this.config=config;
  }
  public boolean isParsed(){
    return parsed;
  }
  public String getFullyQualifiedClassName(){
    return fullyQualifiedClassName;
  }
  public JavadocForMethod[] getMethods(){
    if(!parsed)throw new RuntimeException("javadoc for a class is not yet parsed");
    return (JavadocForMethod[])methods.toArray(new JavadocForMethod[0]);
  }
  public synchronized void parse(){//todo
    if(parsed)return;
    ExceptionUtil.noimpl();
    parsed=true;
    save();
  }
}