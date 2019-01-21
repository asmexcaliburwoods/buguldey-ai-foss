package org.east.concepts;

import org.east.util.ExceptionUtil;
import org.east.exceptions.DatabaseIsCorruptException;

import java.io.*;
import java.util.*;

public class Concept implements Serializable{
  private static final Map conceptId2concept=new HashMap();
  private static final File CONCEPTS_HINT_FILE=new File("concepts_hint.dat");
  private static final File FOLDER_FOR_CONCEPTS=new File("concepts");
  private final long conceptId;
  private String[] names=new String[0];
  protected Concept(){
    conceptId=generateConceptId();
    save();
  }
  public String[] getNames(){
    return names;
  }
  public synchronized void addName(String name){
    List list=Arrays.asList(names);
    Set n=new HashSet(list.size()+1);
    n.addAll(list);
    n.add(name);
    names=(String[])n.toArray(new String[0]);
    save();
  }
  private static synchronized long generateConceptId(){
    long hint=0;
    try{
      DataInputStream dis=new DataInputStream(new FileInputStream(CONCEPTS_HINT_FILE));
      try{
        hint=dis.readLong();
      }finally{
        try{dis.close();}catch(Exception e){}
      }
    }catch(Exception e){
      hint=FOLDER_FOR_CONCEPTS.list().length;
    }
    File hinted;
    while(true){
      hinted=new File(FOLDER_FOR_CONCEPTS,hint+".dat");
      if(!hinted.exists())break;
      hint++;
    }
    try{
      DataOutputStream dos=new DataOutputStream(new FileOutputStream(CONCEPTS_HINT_FILE));
      try{
        dos.writeLong(hint+1);
      }finally{
        try{dos.close();}catch(Exception e){}
      }
    }catch(Exception e){
      CONCEPTS_HINT_FILE.delete();
    }
    return hint;
  }
  public long getConceptId(){
    return conceptId;
  }
  public synchronized void save(){
    File conceptFile=new File(FOLDER_FOR_CONCEPTS, getConceptId()+".dat");
    try{
        FileOutputStream os=new FileOutputStream(conceptFile);
        ObjectOutputStream oos=new ObjectOutputStream(os);
        try{
          oos.writeObject(this);
        }finally{
          try{oos.close();}catch(IOException e){}
        }
    }catch(Exception e){
      ExceptionUtil.handleDatabaseIsNowCorruptException(e,
              "CONCEPT CORRUPTED: concept id: "+getConceptId());
      conceptFile.delete();
      throw new RuntimeException(e);
    }
  }
  public static synchronized Concept resolve(long conceptId){
    Long conceptIdLong=new Long(conceptId);
    Concept concept=(Concept)conceptId2concept.get(conceptIdLong);
    if(concept!=null)return concept;
    concept=read(conceptId);
    conceptId2concept.put(conceptIdLong,concept);
    return concept;
  }
  private static Concept read(long conceptId) throws DatabaseIsCorruptException{
    File conceptFile=new File(FOLDER_FOR_CONCEPTS, conceptId+".dat");
    try{
        FileInputStream os=new FileInputStream(conceptFile);
        ObjectInputStream oos=new ObjectInputStream(os);
        try{
          return (Concept)oos.readObject();
        }finally{
          try{oos.close();}catch(IOException e){}
        }
    }catch(Exception e){
      ExceptionUtil.handleDatabaseIsNowCorruptException(e,
              "CONCEPT CORRUPTED: concept id: "+conceptId);
      conceptFile.delete();
      throw new DatabaseIsCorruptException();
    }
  }
  public boolean equals(Object o){
    if(o==null||!(o instanceof Concept))return false;
    Concept c=(Concept)o;
    return c.conceptId==conceptId;
  }
  public int hashCode(){
    return new Long(conceptId).hashCode();
  }
}
