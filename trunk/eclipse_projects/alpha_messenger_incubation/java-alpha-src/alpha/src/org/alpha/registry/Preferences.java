package org.alpha.registry;

import org.alpha.*;
import java.io.*;
import java.util.*;

public class Preferences{
  static{
    System.loadLibrary("prefs");
    initLibrary();
  }

  /** Internal */
  private static native void initLibrary();

  private static final int ROOT_SYSTEM=1;
  private static final int ROOT_USER=2;
  private static final String ROOT_FOLDER="SOFTWARE\\"+Constants.REGISTRY_KEY;
  private final int root;
  public static final Preferences userNode=new Preferences(ROOT_USER,ROOT_FOLDER);
  public static final Preferences systemNode=new Preferences(ROOT_SYSTEM,ROOT_FOLDER);
  private Preferences(int root,String path){
    this.root=root;
    this.path=path;
  }
  private final String path;
  public String getPath(){
    return path;
  }
  /** path may contain backslashes */
  public Preferences node(String path){
    return new Preferences(root,this.path+"\\"+path);
  }
  public void putInt(String key,int value)throws Exception{
    ByteArrayOutputStream os=new ByteArrayOutputStream(8);
    DataOutputStream dos=new DataOutputStream(os);
    dos.writeInt(value);
    putBytes(key,os.toByteArray());
  }
  public void putObject(String key,Object value)throws Exception{
    ByteArrayOutputStream os=new ByteArrayOutputStream();
    ObjectOutputStream dos=new ObjectOutputStream(os);
    dos.writeObject(value);
    putBytes(key,os.toByteArray());
  }
  private void putBytes(String key,byte[] bytes)throws Exception{
    putBytes(root,path,key,bytes);
  }
  private static native void putBytes(int root,String path,String key,byte[] bytes)throws Exception;

  public int getInt(String key,int defaultValue){
    byte[] b=getBytes(key,null);
    if(b==null)return defaultValue;
    DataInputStream is=new DataInputStream(new ByteArrayInputStream(b));
    try{
      return is.readInt();
    } catch(IOException e){
      return defaultValue;
    }
  }
  public Object getObject(String key,Object defaultValue){
    byte[] b=getBytes(key,null);
    if(b==null)return defaultValue;
    try{
      ObjectInputStream is=new ObjectInputStream(new ByteArrayInputStream(b));
      return is.readObject();
    } catch(Exception e){
      e.printStackTrace();
      return defaultValue;
    }
  }
  public byte[] getBytes(String key,byte[] defaultValue){
    try{
      return getBytes(root,path,key);
    } catch(Exception e){
      return defaultValue;
    }
  }
  private static native byte[] getBytes(int root,String path,String key)throws Exception;

  /** enumerates nodes, not keys */
  public String[] childrenNames()throws Exception{
    List l=new LinkedList();
    enumNodeNames(root,path,l);
    return (String[])l.toArray(new String[0]);
  }
  private static native void enumNodeNames(int root,String path,List result)throws Exception;

  public long getLong(String key,long defaultValue){
    byte[] b=getBytes(key,null);
    if(b==null)return defaultValue;
    DataInputStream is=new DataInputStream(new ByteArrayInputStream(b));
    try{
      return is.readLong();
    } catch(IOException e){
      return defaultValue;
    }
  }
  public String getString(String key,String defaultValue){
    byte[] b=getBytes(key,null);
    if(b==null)return defaultValue;
    DataInputStream is=new DataInputStream(new ByteArrayInputStream(b));
    try{
      return is.readUTF();
    } catch(IOException e){
      return defaultValue;
    }
  }
  /** removes node, not key */
  public void removeNode()throws Exception{
    removeNode(root,path);
  }
  private static native void removeNode(int root,String path)throws Exception;

  /** tests for node, not key */
  public boolean nodeExists()throws Exception{
    return nodeExists(root,path);
  }

  private static native boolean nodeExists(int root,String path)throws Exception;

  /** tests for node, not key */
  public boolean nodeExists(String path)throws Exception{
    return node(path).nodeExists();
  }
  public void putLong(String key,long value)throws Exception{
    ByteArrayOutputStream os=new ByteArrayOutputStream(8);
    DataOutputStream dos=new DataOutputStream(os);
    dos.writeLong(value);
    putBytes(key,os.toByteArray());
  }
  public void putString(String key,String value)throws Exception{
    ByteArrayOutputStream os=new ByteArrayOutputStream(8);
    DataOutputStream dos=new DataOutputStream(os);
    dos.writeUTF(value);
    putBytes(key,os.toByteArray());
  }
  public static void main(String[] args){
    try{
      byte[] bytes=new byte[5];
      int i;
      byte[] sample="Test".getBytes();
      for(i=0;i<bytes.length;i++)bytes[i]=i>=4?(byte)0:sample[i];
      putBytes(ROOT_USER,ROOT_FOLDER+"\\test","key",bytes);
      byte[] got=getBytes(ROOT_USER,ROOT_FOLDER+"\\test","key");
      StringBuffer sb=new StringBuffer();
      for(i=0;i<got.length;i++){
        if(i>0)sb.append(",");
        sb.append(got[i]);
      }
      System.out.println("got: {"+sb+"}");
      removeNode(ROOT_USER,ROOT_FOLDER+"\\test");
      try{
        boolean b=nodeExists(ROOT_USER,ROOT_FOLDER);
        System.err.println(ROOT_FOLDER+" exists: "+b);
      }catch(Exception e){
        System.err.println(ROOT_FOLDER+" does not exist.");
        System.err.println(""+e);
      }
      try{
        boolean b=nodeExists(ROOT_USER,ROOT_FOLDER+"\\test2");
        System.err.println(ROOT_FOLDER+"\\test2"+" exists: "+b);
      }catch(Exception e){
        System.err.println(ROOT_FOLDER+"\\test2"+" does not exist.");
        System.err.println(""+e);
      }
      List result=new LinkedList();
      System.out.println("Enum ROOT_SYSTEM:");
      enumNodeNames(ROOT_SYSTEM,"",result);
      Iterator it=result.iterator();
      while(it.hasNext())System.out.println(""+it.next());
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
