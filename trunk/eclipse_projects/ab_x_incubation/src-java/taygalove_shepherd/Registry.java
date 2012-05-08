package taygalove_shepherd;

import java.util.HashSet;
import java.util.Set;

public class Registry {
    private static boolean holdOn=false;
    private static Set<Object> frames=new HashSet<Object>();
    public static synchronized <E> void objectShown(E f){
        frames.add(f);
    }
    public static <E> void objectDisposed(E f){
        boolean b;
        synchronized(Registry.class){
            frames.remove(f);
            b=checkExit();
        }
        if(b)
            exit();
    }
    private static void exit(){
      System.exit(0);//GTD vereteno/fish/biology/diversity/stability/taygalove_shepherd data preservation
    }
    private static boolean checkExit(){
        return !holdOn&&frames.isEmpty();
    }
    public static boolean isHoldOn(){
        return holdOn;
    }
    public static void setHoldOn(boolean holdOn){
        boolean b;
        synchronized(Registry.class){
            Registry.holdOn=holdOn;
            b=checkExit();
        }
        if(b)
            exit();
    }
}
