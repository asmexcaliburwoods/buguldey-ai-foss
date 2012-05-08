package taygalove_shepherd.i18n.m;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Properties;
import java.util.StringTokenizer;

import taygalove_shepherd.NamedCaller;
import taygalove_shepherd.util.ExceptionUtil;

public class MLoader {
    private static final String LOCALE_FILES_FOLDER="/taygalove_shepherd/";
    public static Properties load(NamedCaller nc){
    	String RESPATH=LOCALE_FILES_FOLDER+"LOCALE.cfg";
        InputStream localeConfigFile=MLoader.class.getResourceAsStream(RESPATH);
        byte[] buf=new byte[6];
        {
        	BufferedInputStream dis=null;
            try{
                dis=new BufferedInputStream(localeConfigFile, 6);
                int pos=0;
                while(dis.available()>0){
                	int read=dis.read(buf, pos, dis.available());
                	if(read<=0) throw new AssertionError("read must be > 0");
                	pos+=read;
                }
            } catch(FileNotFoundException e){
                System.err.println(RESPATH+" resource not found");
                e.printStackTrace();
            } catch(IOException e){
                System.err.println("Error reading "+RESPATH+" resource: "+e);
                e.printStackTrace();
            }finally{
                if(dis!=null)try{dis.close();}catch(Exception e){
                    ExceptionUtil.handleException(nc, e);
                }
            }
        }
        try{
            String locale=new String(buf,"ASCII").trim();
            String lang=locale.substring(0,2);
            String country=locale.substring(2,4);
            String variant=locale.substring(4);
            M.JAVA_LOCALE_LANGUAGE=lang;
            M.JAVA_LOCALE_COUNTRY=country;
            M.JAVA_LOCALE_VARIANT=variant;
            M.DEFAULT_LOCALE=new Locale(lang,country,variant);//test variable values specified

            String PATH2=LOCALE_FILES_FOLDER+locale+".encoding";
            InputStream localeEncodingIS=MLoader.class.getResourceAsStream(PATH2);
            String localeEncoding=null;
            {
                DataInputStream dis=null;
                byte[] bufEncoding=new byte[6];
                try{
                    dis=new DataInputStream(localeEncodingIS);
                    int len=dis.read(bufEncoding);
                    localeEncoding=new String(bufEncoding,0,len,"ASCII");
                } catch(FileNotFoundException e){
                    System.err.println(PATH2+" resource not found");
                } catch(IOException e){
                    System.err.println("Error reading "+PATH2+" resource: "+e);
                }finally{
                    if(dis!=null)try{dis.close();}catch(Exception e){e.printStackTrace();}
                }
            }
            String PATH3=LOCALE_FILES_FOLDER+locale+".locale";
            InputStream localeStringsIS=MLoader.class.getResourceAsStream(PATH3);
            Properties p=new Properties();
            {
                BufferedReader dis=null;
                try{
                    dis=new BufferedReader(
                            new InputStreamReader(localeStringsIS, localeEncoding),
                            64*1024);
                    while(true){
                        String s=dis.readLine();
                        if(s==null)break;
                        StringTokenizer st=new StringTokenizer(s,"=",true);
                        String var=st.nextToken().trim();
                        st.nextToken();//skip "="
                        String val="";
                        if(st.hasMoreTokens()){
                            val=st.nextToken("").trim().replaceAll("\\\\r","\r").replaceAll("\\\\n","\n");
                        }
                        p.put(var,val);
                    }
                    return p;
                } catch(FileNotFoundException e){
                    System.err.println(PATH3+" resource not found");
                } catch(IOException e){
                    System.err.println("Error reading "+PATH3+" resource: "+e);
                }finally{
                    if(dis!=null)try{dis.close();}catch(Exception e){e.printStackTrace();}
                }
            }
        } catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Error loading locale files");
    }
}
