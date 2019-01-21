package org.east.text.parse;

import org.east.concepts.ConceptWithInputStream;
import org.east.concepts.Name;
import org.east.concepts.EastProjectDialogueTextualContext;
import org.east.concepts.Concept;
import org.east.concepts.utility.MeaningAllocator;
import org.east.javadoc.JavadocLearnAction;

import java.io.*;

public final class ReadTextFromInputStreamAction extends ReadTextAction{
  private ReadTextFromInputStreamAction(){}
  public static void define(){
    Name.define("read text from input stream",
            EastProjectDialogueTextualContext.getInstance(),
            ReadTextFromInputStreamAction.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new ReadTextFromInputStreamAction();
              }
            });
  }
  public void perform(String[] arguments) throws Exception{
    if(arguments==null)
      throw new IllegalArgumentException("No arguments. "+
              JavadocLearnAction.class.getName()+" requires one argument: inputStreamName");
    if(arguments.length!=1)
      throw new IllegalArgumentException("Wrong number of arguments: "+arguments.length+". "+
              JavadocLearnAction.class.getName()+" requires one argument: inputStreamName");
    readTextFromInputStream(arguments[0]);
  }
  private void readTextFromInputStream(String inputStreamName) throws IOException{
    ConceptWithInputStream inputStream=
            (ConceptWithInputStream)
              Name.resolveSingleConcept(inputStreamName);
    System.out.println("Reading text from "+((Concept)inputStream).getNames()[0]);
    InputStream is=inputStream.getInputStream();
    char[] buf=new char[1024];
    try{
      Reader r=new InputStreamReader(is);
      while(true){
        int read=r.read(buf);
        if(read<0)break;
        System.out.print(new String(buf,0,read));
      }
      System.out.println();
    }finally{
      is.close();
    }
  }
}
