package org.east.e1.semaction;

import org.east.e1.semaction.expr.LValue;

import java.util.Map;
import java.util.HashMap;

public class Scope{
  private Map varName2lvalue=new HashMap();
  private Object returnValue;
  private Scope parent;
  public Object getDefiningObject(){
    return definingObject;
  }
  public Scope(Object definingObject){
    if(definingObject==null)throw new NullPointerException();
    this.definingObject=definingObject;
  }
  public Scope(Object definingObject,Scope parent){
    this(definingObject);
    this.parent=parent;
  }
  private Object definingObject;
  public Object getReturnValue(){
    return returnValue;
  }
  public void setReturnValue(Object returnValue){
    this.returnValue=returnValue;
  }
  public Object getValue(String varName){
    LValue lValue=getLValue(varName,false);
    if(lValue!=null)return lValue.getValue();
    if (parent!=null){
      lValue=parent.getLValue(varName);
      return lValue.getValue();
    }else return throwNoSuchVar(varName);
  }
  public LValue getLValue(String varName){
    LValue lv=getLValue(varName,false);
    if(lv==null&&parent!=null)lv=parent.getLValue(varName,false);
    if(lv==null)throwNoSuchVar(varName);
    return lv;
  }
  private LValue getLValue(String varName,boolean throw_){
    LValue lvalue=get0(varName);
    if(throw_&&lvalue==null)return throwNoSuchVar(varName);
    return lvalue;
  }
  private LValue throwNoSuchVar(String varName){
    throw new RuntimeException("no variable "+varName+" in "+definingObject);
  }
  public LValue createLValue(String varName,Object value){
    LValue lvalue=get0(varName);
    if(lvalue!=null)
      throw new RuntimeException("variable "+varName+" already exists in "+definingObject);
    lvalue=new LValue();
    lvalue.setValue(value);
    varName2lvalue.put(varName,lvalue);
    //System.out.println(varName+"="+lvalue.getValue());
    return lvalue;
  }
  private LValue get0(String varName){
    return (LValue)varName2lvalue.get(varName);
  }
  public String toString(){
    return String.valueOf(definingObject);
  }
}
