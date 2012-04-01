package forpeople.events;

import java.io.IOException;
import java.io.InputStream;

import forpeople.machinebrain.InputStreamMachineBrain;
import forpeople.processingpools.ReadEventProcessingPool;

public class InputStreamEventGenerator {

	public static void install(final InputStream in, final String name, final InputStreamMachineBrain brain) {
		Thread t=new Thread("inputstream_read_event_generator: "+name){
			public void run(){
				ReadEventProcessingPool processingPool=new ReadEventProcessingPool("ProcessingPool for "+Thread.currentThread().getName(), brain);
				InputStream is=in;
				try{
					while(true){
						if(Thread.interrupted()){System.err.println(Thread.currentThread().getName()+": interrupted, exiting a thread.");return;}
						int av=is.available();
						if(av<0){System.out.println(Thread.currentThread().getName()+": available<0, exiting a thread.");return;}
						if(av==0){
							Thread.yield();
							continue;
						}
						final byte[] b=new byte[av];
						final int read=is.read(b);
						processingPool.addReadEvent(new InputStreamReadEvent() {

							@Override
							public byte[] getByteBuffer() {
								return b;
							}

							@Override
							public int getStartByteOffset() {
								return 0;
							}

							@Override
							public int getByteLength() {
								return read;
							}

							@Override
							public void debugToStdOut() {
								StringBuilder sb=new StringBuilder();
								sb.append(name+": bytes read: ");
								boolean first=true;
								for(int i=0; i<getByteLength(); i++){
									byte arg=getByteBuffer()[getStartByteOffset()+i];
									if(!first)sb.append(" ");
									sb.append(arg);
									first=false;
								}
								sb.append("\n");
								System.out.print(sb);
							}

							@Override
							public InputStream getSourceInputStream() {
								return in;
							}    	
						});
					}
				}catch(IOException e){
					e.printStackTrace();
					System.err.println(Thread.currentThread().getName()+": I/O error, exiting a thread.");
				}
			}
		};
		//t.setDaemon(true);
		t.start();
	}
}