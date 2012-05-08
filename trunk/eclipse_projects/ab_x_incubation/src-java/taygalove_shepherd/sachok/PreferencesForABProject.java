package taygalove_shepherd.sachok;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.util.OSVersionUtil;

public class PreferencesForABProject {
    private static PersistenceStore store=new PersistenceStore();
    
    /** Internal */
    public static void initLibrary(NamedCaller nc){
    	store.init(nc);
    }

    private static final int ROOT_SYSTEM=1;
    private static final int ROOT_USER=2;

    private PreferencesForABProject(){throw new UnsupportedOperationException();}
    
    private final int root;
    public static final PreferencesForABProject systemNode=new PreferencesForABProject(ROOT_SYSTEM,".");
    public static final PreferencesForABProject userNodeForABProject=new PreferencesForABProject(ROOT_USER,".");
    public static final PreferencesForABProject getUserNodeInstance(){ return userNodeForABProject; } 
    private PreferencesForABProject(int root,String path){
        this.root=root;
        this.path=path;
    }
    private final String path;
    public String getPath(){
        return path;
    }
    /** path may contain backslashes */
    public PreferencesForABProject node(String path){
        return new PreferencesForABProject(root,this.path+'\\'+path);
    }
    public void putInt(NamedCaller nc, String key,int value)throws Exception{
        ByteArrayOutputStream os=new ByteArrayOutputStream(8);
        DataOutputStream dos=new DataOutputStream(os);
        dos.writeInt(value);
        putBytes(nc, key,os.toByteArray());
    }
    public void putObject(NamedCaller nc, String key,Object value)throws Exception{
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        ObjectOutputStream dos=new ObjectOutputStream(os);
        dos.writeObject(value);
        putBytes(nc, key,os.toByteArray());
    }
    private void putBytes(NamedCaller nc, String key,byte[] bytes)throws Exception{
        putBytes(nc, root,path,key,bytes);
    }
    
    private static  void putBytes(NamedCaller nc, int root,String path,String key,byte[] bytes)throws Exception{
    	store.putBytes(nc, path+'\\'+key, bytes);
    }

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
    private static  byte[] getBytes(int root,String path,String key)throws Exception{
    	return store.getBytes(path+'\\'+key);
    }

    /** enumerates nodes, not keys */
//    public String[] childrenNames()throws Exception{
//        List l=new LinkedList();
//        enumNodeNames(root,path,l);
//        return (String[]) l.toArray(new String[0]);
//    }
//    private static  void enumNodeNames(int root,String path,List result)throws Exception{throw new Unsuppo}

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
    public void removeNode(NamedCaller nc)throws Exception{
        removeNode(nc, root,path);
    }
    private static  void removeNode(NamedCaller nc, int root,String path)throws Exception{
    	store.removeNode(nc, path+'\\');
    }

//    /** tests for node, not key */
//    public boolean nodeExists()throws Exception{
//        return nodeExists(root,path);
//    }

//    private static  boolean nodeExists(int root,String path)throws Exception{return false;}

//    /** tests for node, not key */
//    public boolean nodeExists(String path)throws Exception{
//        return node(path).nodeExists();
//    }
    public void putLong(NamedCaller nc, String key,long value)throws Exception{
        ByteArrayOutputStream os=new ByteArrayOutputStream(8);
        DataOutputStream dos=new DataOutputStream(os);
        dos.writeLong(value);
        putBytes(nc, key,os.toByteArray());
    }
    public void putString(NamedCaller nc, String key,String value)throws Exception{
        ByteArrayOutputStream os=new ByteArrayOutputStream(8);
        DataOutputStream dos=new DataOutputStream(os);
        dos.writeUTF(value);
        putBytes(nc, key,os.toByteArray());
    }
    public static void main(String[] args){
    	NamedCaller nc=new NamedCaller() {
			
			@Override
			public String name() {
				return "PreferencesForABProject.Test";
			}
		};
        try{
    		initLibrary(nc);
            byte[] bytes=new byte[5];
            int i;
            byte[] sample="Test".getBytes();
            for(i=0;i<bytes.length;i++)bytes[i]=i>=4?(byte)0:sample[i];
            putBytes(nc, ROOT_USER,".\\test","key",bytes);
            byte[] got=getBytes(ROOT_USER,".\\test","key");
            StringBuffer sb=new StringBuffer();
            for(i=0;i<got.length;i++){
                if(i>0)sb.append(",");
                sb.append(got[i]);
            }
            System.out.println("got: {"+sb+"}");
            removeNode(nc, ROOT_USER,".\\test");//GTD parse out . and .. from paths, convert \\ into /
//            try{
//                boolean b=nodeExists(ROOT_USER,".");
//                System.err.println(". exists: "+b);
//            }catch(Exception e){
//                System.err.println(". does not exist.");
//                System.err.println(""+e);
//            }
//            try{
//                boolean b=nodeExists(ROOT_USER,".\\test2");
//                System.err.println(".\\test2"+" exists: "+b);
//            }catch(Exception e){
//                System.err.println(".\\test2"+" does not exist.");
//                System.err.println(""+e);
//            }
//            List result=new LinkedList();
//            System.out.println("Enum ROOT_SYSTEM:");
//            enumNodeNames(ROOT_SYSTEM,"",result);
//            Iterator it=result.iterator();
//            while (it.hasNext()) System.out.println("" + it.next());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String toString() {
        return root2string()+"\\"+path;
    }

    private static final String ROOT_SYSTEM_NAME=OSVersionUtil.isMelkosoftOkna()?"HK_SYSTEM":"SYSTEM";
    private static final String ROOT_USER_NAME=OSVersionUtil.isMelkosoftOkna()?"HKCU":"USER";
    private String root2string() {
        switch(root){
            case ROOT_SYSTEM: return ROOT_SYSTEM_NAME;
            case ROOT_USER: return ROOT_USER_NAME;
            default: throw new RuntimeException("root "+root);
        }
    }
//  private static final int INDENT=2;
//  public String dump() throws Exception {
//    StringBuffer sb=new StringBuffer();
//    sb.append(this).append("\\\r\n");
//    dump(sb,this,INDENT);
//    return sb.toString();
//  }
//
//  private static void dump(StringBuffer sb, Preferences p, int indent) throws Exception {
//    String indentString = indent(indent);
//    sb.append(indentString).append("{");
//    String[] childern=p.childrenNames();
//    int indent2 = indent + INDENT;
//    indentString = indent(indent2);
//    for (int i = 0; i < childern.length; i++) {
//      String s = childern[i];
//      sb.append("\r\n").append(indentString).append(s).append("\\");
//      dump(sb,p.node(s),indent2);
//    }
//  }
//
//  private static String indent(int indent) {
//    StringBuffer sb=new StringBuffer(indent);
//    while(indent>0){
//      sb.append(' ');
//      indent--;
//    }
//    return sb.toString();
//  }
}
