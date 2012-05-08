package extract_patterns;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import extract_patterns.MatchResultsRecorder.MatchResult;
import extract_patterns.impl.allTimeMemoryImpl.AllTimeMemoryImplFactory;

/**
 * extract-patterns algorithm.
 * <pre>
	match some_locality against all-time-memory
	rule1:
		match <f1...fN(some_locality)> against all-time-memory
		f1..fM=классы гласных-согласных, xor классов гласных-согласных локальности, локальность с 
		выброшенной одной, двумя буквами, всеми гласными.
	rule2:
		for i in {i_1...i_N}: locality_i=frame[local-index-i/2...local-index+i/2]
		match some_locality:=locality_i against all-time-memory (store number of occurences)
 * </pre>
 *
 */
public class ExtractPatternsMain {
	private static Frame frame;
	private static Queue<Locality<Object>> localities;
	private static Queue<Locality<Object>> interpretations;
	private static AllTimeMemory alltimememory;
	private static MatchResultsRecorder matchResultsRecorder;

	public static void main(String[] args){
		try{
			init();
			while(true){
				fillInput();
//				for i in {i_1...i_N}: locality_i=frame[local-index-i/2...local-index+i/2]
				
				for(int framelength=1;framelength<=MAX_LOCALITY_LENGTH;framelength++){
					Locality<Object> loc=frame.getSubframe(0,framelength);
					if(loc==null)break;//length exceeded buffer boundary
					localities.add(loc);
				}
				runMatchers();
				Thread.sleep(50);
			}
		}catch(Throwable tr){
			tr.printStackTrace();
			System.exit(1);
		}
	}

	private static void init() {
		frame=new InputFrameImpl();
		localities=new LinkedList<Locality<Object>>();
		interpretations=new LinkedList<Locality<Object>>();
		alltimememory=AllTimeMemoryImplFactory.createAllTimeMemoryImpl();
		matchResultsRecorder=new MatchResultsRecorder() {
			
			private List<MatchResult> matchedPatterns;

			@Override
			public List<MatchResult> getMatchedPatterns() {
				return matchedPatterns;
			}
			
			@Override
			public void clear() {
				matchedPatterns=new LinkedList<MatchResult>();
			}
			
			@Override
			public void addMatch(MatchResult mr) {
				matchedPatterns.add(mr);
			}
		};
	}

	private static final int MAX_LOCALITY_LENGTH=20;
	
	private static void runMatchers() {
		while(true){
			Locality<Object> loc=localities.poll();
			if(loc==null)break;
			matchResultsRecorder.clear();
//			match <f1...fN(some_locality)> against all-time-memory
			addInterpretations(loc);
			while(true){
				Locality<Object> interpretation=interpretations.poll();
				if(interpretation==null)break;
//				match some_locality against all-time-memory (store number of occurences)
				match(interpretation, loc);
			}
			print_matchResultsRecorder_contents(loc);
		}
		
		shiftFrameByOne();
	}

	private static void print_matchResultsRecorder_contents(Locality<Object> originalInput) {
		if(matchResultsRecorder.getMatchedPatterns().isEmpty())return;
		System.out.print(""+originalInput+": ");
		for(MatchResult mr:matchResultsRecorder.getMatchedPatterns()){
			System.out.print(""+mr.getPattern()+" ("+mr.getMatchCount()+")");
		}
		System.out.println();
	}

	private static void addInterpretations(Locality<Object> loc) {
//		f1..fM=классы гласных-согласных, xor классов гласных-согласных локальности, локальность с 
//		выброшенной одной, двумя буквами, всеми гласными.
		interpretations.add(loc);
		//addInterpretationsClassesOfVowelsConsonants(loc);
		//addInterpretation_XorOfClasses();
		//addInterpretationsOneOrTwoLettersRemoved(loc);
		//addInterpretationAllVowelsRemoved(loc);
	}

	private static void match(Locality<Object> interpretation, Locality<Object> originalInput) {
		alltimememory.match(matchResultsRecorder, interpretation, originalInput);
	}

	private static void shiftFrameByOne() {
		frame.discardFirstElement();
	}

	private static void fillInput() {
		if(!frame.fillInput(MAX_LOCALITY_LENGTH)){
			alltimememory.store(new File("./alltimememory.ser"));
			alltimememory.dump();
			try {
				Thread.sleep(30*1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
