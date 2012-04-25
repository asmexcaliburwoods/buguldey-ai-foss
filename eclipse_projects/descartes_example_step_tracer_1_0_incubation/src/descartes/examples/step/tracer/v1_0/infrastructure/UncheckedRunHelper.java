package descartes.examples.step.tracer.v1_0.infrastructure;

public class UncheckedRunHelper {
	public static void run(UncheckedRunnable runnable) {
		try{
			runnable.run();			
		}catch(Throwable tr){
			tr.printStackTrace();
		}		
	}
}
