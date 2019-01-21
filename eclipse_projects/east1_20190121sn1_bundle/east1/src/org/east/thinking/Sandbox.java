package org.east.thinking;

public interface Sandbox{
  Object getDefiningPrinciple();
//  Object executeMethod(Object objectToExecuteMethodOn,
//                       String methodName,
//                       Class[] methodParameterTypes,
//                       Object[] methodArguments)throws Exception;
  Object getActiveValueForObject(Object object);
}
