package org.east.javadoc;

import org.east.concepts.Concept;

public class JavadocForMethod extends Concept{
  private JavadocForClass clazz;
  private String methodName;
  private boolean isFinal;
  private int publicProtectedPrivatePackageModifier;
  private JavadocForMethodArgument[] args;
  public JavadocForClass getJavadocForClass(){
    return clazz;
  }
  public String getMethodName(){
    return methodName;
  }
  public boolean isFinal(){
    return isFinal;
  }
  public int getPublicProtectedPrivatePackageModifier(){
    return publicProtectedPrivatePackageModifier;
  }
  public JavadocForMethod(JavadocForClass clazz,
                          String methodName,
                          boolean isFinal,
                          int publicProtectedPrivatePackageModifier,
                          JavadocForMethodArgument[] args){
    this.clazz=clazz;
    this.methodName=methodName;
    this.isFinal=isFinal;
    this.publicProtectedPrivatePackageModifier=publicProtectedPrivatePackageModifier;
    this.args=args;
  }
  public JavadocForMethodArgument[] getArguments(){
    return args;
  }
}
