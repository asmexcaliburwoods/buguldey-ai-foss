package taygalove_shepherd.util;

import taygalove_shepherd.NamedCaller;

public class ThreadUtil {
    public static void spawnThread(final NamedCaller nc, String threadName,final Runnable r){
        new Thread(threadName){
            public void run(){
                try{
                    r.run();
                }catch(Throwable tr){
                    ExceptionUtil.handleException(nc, tr);
                }
            }
        }.start();
    }
}
