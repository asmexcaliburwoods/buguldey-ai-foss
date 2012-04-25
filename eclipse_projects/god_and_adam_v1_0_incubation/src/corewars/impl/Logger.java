package corewars.impl;

public interface Logger {
	void log(String s);

	boolean areAnglesLogged();

	void resume();

	void pause();

	/** save to safe storage - application is exiting */ 
	void sleep();
	
	String input();

	void enqueueMouseWheelRotation(int steps);

	void enqueueWord(String in);
}
