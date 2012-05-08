package extract_patterns;

import java.util.List;

public interface MatchResultsRecorder {
	static interface MatchResult{
		Locality<Object> getOriginalInput();
		Pattern getPattern();
		/**
		 * 0 for new patterns, &gt;=1 for frequently matched patterns: number of matches
		 */
		int getMatchCount();
	}
	void clear();
	void addMatch(MatchResult mr);
	List<MatchResult> getMatchedPatterns();
}
