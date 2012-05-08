package extract_patterns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class InputFrameImpl implements Frame {
	private final class LocalityImpl implements Locality<Object> {
		private final LinkedList<Object> list;

		private LocalityImpl(LinkedList<Object> list) {
			this.list = list;
		}

		public int hashCode(){return list.hashCode();}

		public boolean equals(Object l2){
			if(!(l2 instanceof LocalityImpl))return false;
			LocalityImpl loc2=(LocalityImpl) l2;
			return list.equals(loc2.list);
		}

		@Override
		public List<Object> getElements() {
			return list;
		}
		
		public String toString(){
			StringBuilder sb=new StringBuilder();
			Iterator<Object> it=list.iterator();
			while(it.hasNext()){
				Object o=it.next();
				if(o instanceof Character){
					char c=(Character) o;
					if(Character.isISOControl(c)){sb.append("<"+(int)c+">");continue;}
				}
				sb.append(o.toString());
			}
			return sb.toString();
		}
	}

	private int size;
	private LinkedList<Object> buffer=new LinkedList<Object>();

	@Override
	public Locality<Object> getSubframe(int fromInclusive, int length) {
		if(fromInclusive+length>size)return null;
		final LinkedList<Object> list=new LinkedList<Object>();
		Iterator<Object> iter=buffer.listIterator(fromInclusive);
		int len=0;
		while(iter.hasNext()){
			if(len>=length)break;
			list.add(iter.next());
			++len;
		}
		if(list.size()!=length)return null;
		return new LocalityImpl(list);
	}

	/**
	 * returns false if no more input
	 */
	@Override
	public boolean fillInput(int neededNumberOfElementsToHave) {
		while(size<neededNumberOfElementsToHave){
			if(addOneInputElement())continue;
			return false;
		}
		return true;
	}

	/**
	 * return false if cannot add
	 * @return
	 */
	private synchronized boolean addOneInputElement() {
		//if can add, add
		Object inputElement=readInputElement();
		if(inputElement!=null){
			buffer.add(inputElement);
			++size;
			return true;
		}
		return false;
	}

	//private BufferedReader dis=new BufferedReader(new InputStreamReader(System.in),64*1024);
	
	private static final String corpusLocation="/home/keep/devel/corpora/";
	private static final File corpRoot=new File(corpusLocation);
	static List<File> corpora=new LinkedList<File>();
	static Iterator<File> fileIter;
	private static BufferedReader dis;
	private static FileInputStream fis;
	static{
		if(!corpRoot.exists())throw new RuntimeException("corpora root dir not exists");
		recurse(corpRoot);
		fileIter=corpora.iterator();
		open();
	}
	
	private Object readInputElement() {
		while(true){
			try {
				int ch=dis.read();
				if(ch!=-1)return (char)ch;
			} catch (IOException e) {
				e.printStackTrace();
			}
			open();
			if(dis==null)return null;
		}
	}

	private static void open() {
		while(true){
			if(fileIter.hasNext()){
				if(dis!=null){try{dis.close();}catch(IOException e){e.printStackTrace();} dis=null;}
				if(fis!=null){try{fis.close();}catch(IOException e){e.printStackTrace();} fis=null;}
				try {
					File fff=fileIter.next();
					try {
						System.out.println("Reading "+fff.getCanonicalPath());
					} catch (IOException e) {
						System.out.println("Reading "+fff.getAbsolutePath());
					}
					dis=new BufferedReader(new InputStreamReader(fis=new FileInputStream(fff)),64*1024);
					break;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}else break;
		}
	}

	private static void recurse(File f) {
		if(f.isDirectory()){
			File[] files=f.listFiles();
			if(files!=null)for(File ff:files){recurse(ff);}
			return;
		}
		if(f.isFile()){
			corpora.add(f);
			try {
				System.out.println("Added corpus: "+f.getCanonicalPath());
			} catch (IOException e) {
				System.out.println("Added corpus: "+f.getAbsolutePath());
			}
		}
	}

	@Override
	public synchronized void discardFirstElement() {
		--size;
		buffer.remove();
	}
}
