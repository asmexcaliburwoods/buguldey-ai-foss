package corewars.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoggerImpl_Console implements Logger {
	
	private CoreWarsImpl corewars;

	LoggerImpl_Console(CoreWarsImpl corewars){
		this.corewars=corewars;
	}
	@Override
	public boolean areAnglesLogged() {
		return true;
	}

	@Override
	public void log(String s) {
		System.out.println(s);
	}

	@Override
	public void pause() {
		corewars.pause();
	}

	@Override
	public void resume() {
		corewars.resume();
	}

	@Override
	public void sleep() {
		corewars.sleep();		
	}
	private BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
	@Override
	public String input() {
		try {
			System.out.print("GOD'S WORD: ");
			String line=in.readLine();
			if(line==null)System.out.println();
			return line;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void enqueueMouseWheelRotation(int steps) {
		throw new UnsupportedOperationException("enqueueMouseWheelRotation()");
	}
	@Override
	public void enqueueWord(String in) {
		throw new UnsupportedOperationException("enqueueWord()");
	}
}
