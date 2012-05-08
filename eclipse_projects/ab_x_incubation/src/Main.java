import java.util.*;
import java.io.*;
class Main{
  public static void main(String[] args){
    try{
      String abfn=System.getProperty("user.home")+"/"+System.getProperty("user.name")+".ab";
      Debug.debug("reading '"+abfn+"'");
      File abf=null;
      try{
        abf=new File(abfn);
        final AB ab;
        if(!abf.exists())ab=new AB();
        else{
          
      /*
      Debug.debug("sp:");
      Properties p=System.getProperties();
      Iterator it=p.keySet().iterator();
      while(it.hasNext()){
        Object k=it.next();
        Debug.debug("  "+k+":"+p.get(k));
      }
      */
      //new MainForm().setVisible(true);
    }catch(Throwable tr){
      Debug.error(tr);
    }
  }
}
      
        