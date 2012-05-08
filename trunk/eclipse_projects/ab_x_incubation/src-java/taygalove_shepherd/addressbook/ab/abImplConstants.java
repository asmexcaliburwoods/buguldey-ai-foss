package taygalove_shepherd.addressbook.ab;

import java.util.Date;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class abImplConstants {
    public static final int IO_FILE_BUFFER_SIZE_BYTES = 8*1024*1024;

    private abImplConstants(){}

    public static final String APP_TITLE0;
    private static final String APP_VENDOR,APP_VERSION_MAJOR,BUILD_NUMBER;

    public static final Date BUILD_DATE;
    public static final String APP_TITLE;
    public static final String APP_VERSION;
    public static final String APP_NAME;
    private static final String APP_VERSION_POSTFIX;

    static{
        InputStream is=abImplConstants.class.getResourceAsStream("/version.properties");
        boolean error=true;
        try{
            Properties p=new Properties();
            p.load(is);
            APP_TITLE0=p.getProperty("app.title");
            APP_VENDOR=p.getProperty("app.vendor");
            APP_VERSION_MAJOR=p.getProperty("app.version.major");
            APP_VERSION_POSTFIX=p.getProperty("app.version.postfix");
            BUILD_NUMBER=p.getProperty("build.number");
            BUILD_DATE=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(p.getProperty("build.date"));//GTD form version.properties in ant buildfile
            APP_VERSION=APP_VERSION_MAJOR+", build "+BUILD_NUMBER+APP_VERSION_POSTFIX;
            APP_TITLE=APP_TITLE0;
            APP_NAME=APP_TITLE+" "+APP_VERSION;
            error=false;
        }catch(Exception e){
            //will never happen?
            throw new RuntimeException(e);//todo handle this in Swing error box.
        }finally{
            try{is.close();}catch(IOException e){
                if(!error)throw new RuntimeException(e);//todo handle this in Swing error box.
            }
        }
    }
    public static final String REGISTRY_KEY=APP_TITLE;
}
