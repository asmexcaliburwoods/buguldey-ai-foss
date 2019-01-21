package org.east.concepts;

import org.east.concepts.utility.ConceptIdentity;
import org.east.concepts.utility.MeaningAllocator;

import java.io.*;

public class FileConcept extends Concept{
//  private Text content;
  public String getFileName(){
    return fileName;
  }
  private String fileName;
  private FileConcept(String fileName){
    this.fileName=fileName;
  }
  public static FileConcept define(final String fileName){//todo HACK! must be accomplished via custom NamingContexts
    return (FileConcept)Name.define("file \'"+fileName+"\'",
            EastProjectDialogueTextualContext.getInstance(),
            new ConceptIdentity(){
              public boolean isIdenticalTo(Concept concept){
                if(!(concept instanceof FileConcept))return false;
                FileConcept fc=(FileConcept)concept;
                return fc.fileName.equals(fileName);
              }
            },new MeaningAllocator(){
      public Concept allocate(){
        return new FileConcept(fileName);
      }
    }).getConcept();
  }
//  public InputStream getInputStream()throws IOException{
//    return new BufferedInputStream(new FileInputStream(fileName),16*1024);
//  }
  public String getContent()throws IOException{
//    if(content==null){
    InputStream is=new BufferedInputStream(new FileInputStream(fileName),16*1024);
    String content;
    try{
      StringWriter sw=new StringWriter();
      byte[] buf=new byte[1024*64];
      while(true){
        int read=is.read(buf);
        if(read<0)break;
        sw.write(new String(buf,0,read,"ASCII"));
      }
      content=sw.toString();
    }finally{
      is.close();
    }

    return content;
  }
  private TextUnderstandingArc textUnderstandingArc=new TextUnderstandingArc(this);
  public TextUnderstandingArc getTextUnderstandingArc(){
    return textUnderstandingArc;
  }
}
