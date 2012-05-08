package phial.vl.java.v1.ide.runtimeSource;

public class Source {

	/**
	 * @param parameters currently required to be empty command line.
	 */
	public static void main(String[] parameters) {
		if(parameters.length!=0){
			System.err.println("Error: a command line must be empty.");
			System.exit(-1);
		}
		SwingIDEImplRuntimeSource.flow();
	}
}
