package org.east.javadoc;

import org.east.concepts.Concept;

public class JavadocForMethodArgument extends Concept{
  public boolean isFinal(){
    return isFinal;
  }
  private boolean isFinal;
  public String getArgumentName(){
    return argumentName;
  }
  public JavadocForClass getArgumentDeclaredClass(){
    return argumentDeclaredClass;
  }
  private String argumentName;
  private JavadocForClass argumentDeclaredClass;
  public JavadocForMethodArgument(boolean isFinal, String argumentName,JavadocForClass argumentDeclaredClass){
    this.argumentName=argumentName;
    this.argumentDeclaredClass=argumentDeclaredClass;
    this.isFinal=isFinal;
  }
}
