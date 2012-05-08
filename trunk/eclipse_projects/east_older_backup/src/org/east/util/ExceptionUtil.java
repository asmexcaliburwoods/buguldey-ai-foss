package org.east.util;

public class ExceptionUtil{
  public static void noimpl(){
    throw new RuntimeException("not implemented");
  }
  public static synchronized void handleDatabaseIsNowCorruptException(Throwable e,String msg){
    System.err.println("DATABASE IS NOW CORRUPT: "+msg);
    handleException(e);
  }
  public static synchronized void handleException(Throwable e){
    e.printStackTrace();
  }
  public static void unsupportedOperation(){
    throw new UnsupportedOperationException();
  }
}
