package extract_patterns;

public interface Frame {
	Locality<Object> getSubframe(int fromInclusive, int length);
	/**
	 * returns false if no more input
	 */
	boolean fillInput(int neededNumberOfElementsToHave);
	void discardFirstElement();
}
