package extract_patterns;

import java.io.File;
import java.io.Serializable;

public interface AllTimeMemory extends Serializable{
	void store(File file);
	void match(MatchResultsRecorder matchResultsRecorder, Locality<Object> interpretation, Locality<Object> originalInput);
	void dump();
}
