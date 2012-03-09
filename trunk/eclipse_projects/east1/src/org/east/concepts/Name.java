package org.east.concepts;

import org.east.util.ExceptionUtil;
import org.east.concepts.utility.NameInstance;
import org.east.concepts.utility.MeaningAllocator;
import org.east.concepts.utility.ConceptIdentity;

import java.io.*;
import java.util.Set;
import java.util.Collections;
import java.util.Iterator;

public class Name extends Concept{
  private static final long serialVersionUID = 4068359267504143372L;
  private static final File FOLDER_FOR_NAMES=new File("names");
  private String name;
  protected Name(String name){
    this.name=name;
  }
  public String getName(){
    return name;
  }
  public Set<NameInstance> getNameInstances(){
    return Collections.emptySet();//class is abstract
  }
  public Concept getSingleNamedConcept(){
    Set<NameInstance> nameInstances=getNameInstances();
    if(nameInstances.size()!=1)
      throw new RuntimeException("Name "+getName()+" has more than one meaning!");
    Iterator<NameInstance> iterator=nameInstances.iterator();
    if(iterator.hasNext()){
      Concept concept=iterator.next().getConcept();
      return Concept.resolve(concept.getConceptId());
    }
    throw new RuntimeException("Name \""+getName()+"\" is meaningless!");
  }
  protected synchronized void saveName(){
    File nameFile=new File(FOLDER_FOR_NAMES, getName()+".dat");
    try{
      FileOutputStream os=new FileOutputStream(nameFile);
      DataOutputStream dos=new DataOutputStream(os);
      try{
        dos.writeLong(getConceptId());
      }finally{
        try{dos.close();}catch(IOException e){}
      }
      save();
    }catch(Exception e){
      ExceptionUtil.handleDatabaseIsNowCorruptException(e,
              "NAME CORRUPTED: \""+getName()+"\"");
      nameFile.delete();
    }
  }
  public static Name resolve(String name){
    File nameFile=new File(FOLDER_FOR_NAMES, name+".dat");
    if(!nameFile.exists())return null;
    try{
      FileInputStream is=new FileInputStream(nameFile);
      DataInputStream dis=new DataInputStream(is);
      long conceptId;
      try{
        conceptId=dis.readLong();
      }finally{
        try{dis.close();}catch(IOException e){}
      }
      return (Name)Concept.resolve(conceptId);
    }catch(Exception e){
      ExceptionUtil.handleDatabaseIsNowCorruptException(e,
              "NAME CORRUPTED: \""+name+"\"");
      nameFile.delete();
      return null;
    }
  }
  public static Concept resolveSingleConcept(String name){
    Name n=resolve(name);
    return n==null?null:n.getSingleNamedConcept();
  }
  public static NameInstance define(String name,TextualContext context,Concept concept){
    Name n=resolve(name);
    if(n==null)n=new NameForASetOfConcepts(name);
    Set<NameInstance> nameInstances=n.getNameInstances();
    Iterator<NameInstance> it=nameInstances.iterator();
    while(it.hasNext()){
      NameInstance nameInstance=it.next();
      if(nameInstance.getConcept().equals(concept)&&nameInstance.getNamingContext().equals(context))
        return nameInstance;
    }
    NameForASetOfConcepts ns=(NameForASetOfConcepts)n;
    NameInstance nameInstance=new NameInstance();
    nameInstance.setConcept(concept);
    nameInstance.setName(n);
    nameInstance.setNamingContext(context);
    ns.addNamedConcept(nameInstance);
    return nameInstance;
  }
  public static NameInstance define(String name,TextualContext context, final Class<?> conceptClass,MeaningAllocator alloc){
    return define(name,context,new ConceptIdentity(){
      public boolean isIdenticalTo(Concept concept){
        return conceptClass.equals(concept.getClass());
      }
    },alloc);
  }
  public static NameInstance define(String name,TextualContext context, ConceptIdentity conceptIdentity,MeaningAllocator alloc){
    Name n=resolve(name);
    if(n==null)n=new NameForASetOfConcepts(name);
    Set<NameInstance> nameInstances=n.getNameInstances();
    Iterator<NameInstance> it=nameInstances.iterator();
    while(it.hasNext()){
      NameInstance nameInstance=it.next();
      if(conceptIdentity.isIdenticalTo(nameInstance.getConcept())&&
              nameInstance.getNamingContext().equals(context))
        return nameInstance;
    }
    Concept concept=alloc.allocate();
    NameForASetOfConcepts ns=(NameForASetOfConcepts)n;
    NameInstance nameInstance=new NameInstance();
    nameInstance.setConcept(concept);
    nameInstance.setName(n);
    nameInstance.setNamingContext(context);
    ns.addNamedConcept(nameInstance);
    return nameInstance;
  }
}
