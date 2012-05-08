package kernel.util;


public class ThreadUtil {
    public static void spawnThread(String threadName,final Runnable r){
        new Thread(threadName){
            public void run(){
                try{
                    r.run();
                }catch(Throwable tr){
                    ExceptionUtil.handleException(tr);
                }
            }
        }.start();
    }
}
