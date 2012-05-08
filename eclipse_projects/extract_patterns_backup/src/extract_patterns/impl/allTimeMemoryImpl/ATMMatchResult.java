package extract_patterns.impl.allTimeMemoryImpl;

import extract_patterns.Locality;
import extract_patterns.MatchResultsRecorder;
import extract_patterns.Pattern;

public class ATMMatchResult implements MatchResultsRecorder.MatchResult {
	int countMatched;
	Locality<Object> origInput;
	Pattern pattern;
	Locality<Object> function;

	@Override
	public Locality<Object> getOriginalInput() {
		return origInput;
	}

	@Override
	public Pattern getPattern() {
		return pattern;
	}

	@Override
	public int getMatchCount() {
		return countMatched;
	}
}
