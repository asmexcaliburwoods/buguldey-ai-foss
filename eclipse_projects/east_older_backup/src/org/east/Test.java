package org.east;

import org.east.util.ExceptionUtil;

public class Test{
  public static void main(String[] args){
    try{
//      Object o=CycLink.cyc.getELCycTerm("tcp");
//      System.out.println(o);
//      o=CycLink.cyc.getELCycTerm("protocol");
//      System.out.println(o);
//      o=CycLink.cyc.getELCycTerm("echo server");
//      System.out.println(o);
    }catch(Throwable t){
      ExceptionUtil.handleException(t);
    }
  }
}
