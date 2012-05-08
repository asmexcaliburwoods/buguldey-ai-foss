package extract_patterns.impl.allTimeMemoryImpl;

import extract_patterns.AllTimeMemory;

public class AllTimeMemoryImplFactory {
	public static AllTimeMemory createAllTimeMemoryImpl(){
		return new AllTimeMemoryImpl(); 
	}
}
