package org.east.javadoc;

import java.io.File;

public class JavadocConfig{
  private File docsZipFile;
  private String pathPrefixInDocsZipFile;

  public File getDocsZipFile(){
    return docsZipFile;
  }

  public void setDocsZipFile(File docsZipFile){
    this.docsZipFile=docsZipFile;
  }

  public String getPathPrefixInDocsZipFile(){
    return pathPrefixInDocsZipFile;
  }

  public void setPathPrefixInDocsZipFile(String pathPrefixInDocsZipFile){
    this.pathPrefixInDocsZipFile=pathPrefixInDocsZipFile;
  }
}
