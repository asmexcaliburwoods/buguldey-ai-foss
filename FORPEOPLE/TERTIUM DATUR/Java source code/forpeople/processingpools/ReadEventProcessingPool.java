package forpeople.processingpools;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import forpeople.events.ReadEvent;
import forpeople.machinebrain.MachineBrain;

/**
 * TODO Temporary implementation; to be replaced with multiprocessing one: ???Java multi-cpu API???.
 * For input streams, sequential processing is necessary, for other types of pools, other types of processing are necessary.
 *
 * @author E.G.Philippov
 */
public class ReadEventProcessingPool {
	private final Queue<ReadEvent> queue=new ConcurrentLinkedQueue<ReadEvent>();
	private final MachineBrain brain; 
	
	public ReadEventProcessingPool(String debugName, MachineBrain brain){
		if(brain==null)throw new NullPointerException("brain (of type MachineBrain) is null");
		this.brain=brain;
		new Thread("ReadEventProcessingPool dispatcher "+debugName){
			public void run(){
				while(true){
					ReadEvent ev=queue.poll();
					if(ev==null){Thread.yield();continue;}
					new_thread_with_process_event(ev);
				}
			}
		}.start();
	}

	public void addReadEvent(ReadEvent ev) {
		queue.add(ev);
	}

	protected void new_thread_with_process_event(final ReadEvent ev) {
		new Thread("ReadEventProcessorThread: "+ev.getClass().getName()){
			public void run(){
				eval(ev);		
			}
		}.start();
	}

	/**
	 * <pre>
		(* eval should diagnose if a part of a program is formal, nonformal, or tertium datur *)
		(* co-goal with users *)
		(* "co-goal with users" --- это более точно означает, что пользователи --- это начальники 
		   для машины. У начальников есть свои междуначальнические (междупользовательские) отношения 
		   и обычаи и tertium datur. *)

		(* this is a formal eval which evaluates a formal program which reads and evaluates 
		   tertium datur programs from users *)
	 * </pre>
	 * @param dataReference A reference to data which was read
	 */
	protected void eval(ReadEvent dataReference) {
		brain.eval(dataReference);
	}
}
