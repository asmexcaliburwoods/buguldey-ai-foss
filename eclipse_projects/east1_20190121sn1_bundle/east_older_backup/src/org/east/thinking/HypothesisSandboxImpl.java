package org.east.thinking;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

public class HypothesisSandboxImpl implements HypothesisSandbox,Serializable{
  private Hypothesis hypothesis;
  private Hypothesis previousHypothesis;
  public HypothesisSandboxImpl(){
  }
  public Hypothesis getHypothesis(){
    if(this.hypothesis==null)throw new RuntimeException();
    return hypothesis;
  }
  public Object getDefiningPrinciple(){
    return getHypothesis();
  }
//  public Object executeMethod(Object objectToExecuteMethodOn,
//                              String methodName,
//                              Class[] methodParameterTypes,
//                              Object[] methodArguments) throws Exception{
//    return objectToExecuteMethodOn.getClass().
//            getMethod(methodName,methodParameterTypes).
//            invoke(objectToExecuteMethodOn,methodArguments);
//  }
  private Map map;
  public Object getActiveValueForObject(Object object){
    if(previousHypothesis!=null)
      object=previousHypothesis.getSandbox().getActiveValueForObject(object);
    if(map!=null)return map.get(object);
    return object;
  }
  public void setHypothesis(Hypothesis hypothesis){
    if(this.hypothesis!=null)throw new RuntimeException();
    this.hypothesis=hypothesis;
  }
  public void setPreviousHypothesis(Hypothesis h){
    this.previousHypothesis=h;
  }
  public void replace(Object oldValue, Object newValue){
    if(map==null)map=new HashMap();
    if(map.keySet().contains(oldValue))
      throw new RuntimeException("oldValue: "+oldValue+"; newValue: "+newValue);
    map.put(oldValue,newValue);
  }
}
