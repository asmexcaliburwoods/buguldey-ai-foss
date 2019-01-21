package org.east.e1;

import java.util.StringTokenizer;

public class SmartTokenizer{
  private StringTokenizer st;
  private String next;
  public SmartTokenizer(String s){
    st=new StringTokenizer(s," ",true);
    getNext();
  }
  private void getNext(){
    while(true){
      getNext0();
      if(next==null)return;
      if(next.equals(" "))continue;
      return;
    }
  }
  private void getNext0(){
    String nextToken=st.hasMoreElements()?st.nextToken():null;
    if(nextToken!=null&&nextToken.startsWith("\"")){
      if(nextToken.length()>1&&nextToken.endsWith("\"")){
        this.next=nextToken;
        return;
      }
      while(true){
        String add=st.hasMoreElements()?st.nextToken():null;
        if(add==null){
          this.next=nextToken;
          return;
        }
        nextToken=nextToken+add;
        if(nextToken.length()>1&&nextToken.endsWith("\"")){
          this.next=nextToken;
          return;
        }
      }
    }
    this.next=nextToken;
  }
  public boolean hasMoreTokens(){
    return next!=null;
  }
  public boolean hasMoreElements(){
    return hasMoreTokens();
  }
  public String nextToken(){
    String next=this.next;
    getNext();
    return next;
  }
  public Object nextElement(){
    return nextToken();
  }
}
