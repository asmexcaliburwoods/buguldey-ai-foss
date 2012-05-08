package phial.vl.java.v1.ide.runtimeSource;

import phial.vl.java.v1.ide.runtime.SwingIDEImplRuntime;

public class SwingIDEImplRuntimeSource {
	private SwingIDEImplRuntimeSource(){
		new SwingIDEImplRuntime();
		//and let it behave.
	}
	public static void flow() {
		new SwingIDEImplRuntimeSource();
		//and let it behave.
	}
}
