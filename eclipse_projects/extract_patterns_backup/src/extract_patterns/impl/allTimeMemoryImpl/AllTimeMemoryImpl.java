package extract_patterns.impl.allTimeMemoryImpl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import extract_patterns.AllTimeMemory;
import extract_patterns.Locality;
import extract_patterns.MatchResultsRecorder;
import extract_patterns.Pattern;

public class AllTimeMemoryImpl implements AllTimeMemory {
	private static final long serialVersionUID = 3893076426408682995L;
	private Map<Locality<Object>,ATMMatchResult> memory=new HashMap<Locality<Object>, ATMMatchResult>();

	@Override
	public synchronized void match(MatchResultsRecorder matchResultsRecorder,
			final Locality<Object> interpretation, final Locality<Object> originalInput) {
		ATMMatchResult mr=memory.get(interpretation);
		if(mr==null){
			mr=new ATMMatchResult();
			mr.origInput=originalInput;
			mr.function=interpretation;
			mr.pattern=new Pattern() {
				public String toString(){return interpretation.toString();}
			};
			mr.countMatched=0;
			memory.put(interpretation, mr);
		}else{
			int count=mr.countMatched;
			if(count<Integer.MAX_VALUE)mr.countMatched=count+1;
			matchResultsRecorder.addMatch(mr);
		}
	}
	
	private static class Freq2MR{
		List<ATMMatchResult> mrs=new LinkedList<ATMMatchResult>();
		public String toString(){
			StringBuilder sb=new StringBuilder();
			mrs=new ArrayList<ATMMatchResult>(mrs);
			Collections.sort(mrs, new Comparator<ATMMatchResult>(){
				@Override
				public int compare(ATMMatchResult o1, ATMMatchResult o2) {
					return o1.getOriginalInput().toString().compareTo(o2.getOriginalInput().toString());
				}});
			Iterator<ATMMatchResult> it=mrs.iterator();
			while(it.hasNext()){
				ATMMatchResult mr=it.next();
				if(sb.length()>0)sb.append(", ");
				sb.append(mr.getOriginalInput());
			}
			return sb.toString();
		}
	}
	public synchronized void dump(){
		System.out.println("Dumping All time memory... sorting...");
		Map<Integer,Freq2MR> dump=new HashMap<Integer,Freq2MR>(); 
		Iterator<Map.Entry<Locality<Object>,ATMMatchResult>> iter=memory.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry<Locality<Object>,ATMMatchResult> mr=iter.next();
			Freq2MR f=dump.get(mr.getValue().getMatchCount());
			if(f==null){
				f=new Freq2MR();
				dump.put(mr.getValue().getMatchCount(), f);
			}
			f.mrs.add(mr.getValue());
		}
		ArrayList<Integer> ints=new ArrayList<Integer>(dump.keySet());
		Collections.sort(ints);
		for(int i=ints.size()-1;i>=0;--i){
			//dump[i]
			Freq2MR f=dump.get(i);
			if(f==null)continue;
			if(i==0)break;
			System.out.println(""+i+":\t"+f.toString());
		}
		System.out.println("Dump done.");
	}
	
	{
		Runnable r=new Runnable() {
			
			@Override
			public void run() {
				try{
				while(true){
					int margin=0;
					while(Runtime.getRuntime().freeMemory()<20*1024){
						synchronized(AllTimeMemoryImpl.this){
							System.out.println("no free memory, cleaning All time memory, removing all with count<="+margin);
							int removed=0;
							//remove all with count<=margin
							Iterator<Map.Entry<Locality<Object>,ATMMatchResult>> iter=memory.entrySet().iterator();
							while(iter.hasNext()){
								Map.Entry<Locality<Object>,ATMMatchResult> mr=iter.next();
								if(mr.getValue().countMatched<=margin){
									iter.remove();
									++removed;
								}
							}
							System.out.println(""+removed+" elements removed.");
							System.gc();
							System.gc();
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								e.printStackTrace();
								return;
							}
							//increase margin
							++margin;
						}
					}
					margin=1;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
				}	
				}catch(Throwable tr){
					tr.printStackTrace();
				}
			}
		};
		Thread t=new Thread(r,"all time memory cleaner");
		t.start();
	}

	@Override
	public void store(File file) {
		FileOutputStream fos=null;
		try{
			BufferedOutputStream bos=new BufferedOutputStream(fos,64*1024);
			ObjectOutputStream oos=new ObjectOutputStream(bos);
			oos.writeObject(this);
			oos.flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(fos!=null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
