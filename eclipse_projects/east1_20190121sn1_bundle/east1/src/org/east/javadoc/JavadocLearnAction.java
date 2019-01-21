package org.east.javadoc;

import org.east.concepts.*;
import org.east.concepts.utility.MeaningAllocator;
import org.east.util.ExceptionUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Properties;

public class JavadocLearnAction extends Action{
  private static JavadocConfig javadocConfig=new JavadocConfig();
  static{
    String propFileName="javadoc.properties";
    try{
      Properties prop=new Properties();
      FileInputStream fis=new FileInputStream(propFileName);
      try{
        prop.load(new BufferedInputStream(fis,1024*16));
      }finally{
        try{fis.close();}catch(Exception e){}
      }
      String property=prop.getProperty("javadoc_zipfile");
      if(property==null)
        throw new NullPointerException("javadoc_zipfile property is null in "+
                propFileName);
      javadocConfig.setDocsZipFile(new File(property));
      property=prop.getProperty("path_prefix_in_javadoc_zipfile");
      if(property==null)
        throw new NullPointerException("path_prefix_in_javadoc_zipfile property is null in "+
                propFileName);
      javadocConfig.setPathPrefixInDocsZipFile(property);
    }catch(Exception e){
      ExceptionUtil.handleException(e);
      throw new RuntimeException("error while parsing the file "+propFileName);
    }
  }
  private JavadocLearnAction(){}

  /** Save to db */
  public static void define(){
    Name.define("read javadoc",
            JavaLanguageTextualContext.getInstance(),
            JavadocLearnAction.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new JavadocLearnAction();
              }
            });
  }
  public void read(String fullyQualifiedClassName){
    JavadocForClass j=new JavadocForClass(javadocConfig,fullyQualifiedClassName);
    j.parse();
  }
  public void perform(String[] arguments)throws Exception{
    if(arguments==null)
      throw new IllegalArgumentException("No arguments. "+
              JavadocLearnAction.class.getName()+" requires one argument: fullyQualifiedClassName");
    if(arguments.length!=1)
      throw new IllegalArgumentException("Wrong number of arguments: "+arguments.length+". "+
              JavadocLearnAction.class.getName()+" requires one argument: fullyQualifiedClassName");
    read(arguments[0]);
  }
}
