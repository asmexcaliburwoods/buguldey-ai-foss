package corewars;

public interface Tiredness {
	void increase(float increment);
	boolean tired();
	void rest(long milliseconds);
}
