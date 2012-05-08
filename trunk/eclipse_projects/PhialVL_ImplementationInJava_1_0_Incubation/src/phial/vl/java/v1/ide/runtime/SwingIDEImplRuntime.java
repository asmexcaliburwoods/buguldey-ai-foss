package phial.vl.java.v1.ide.runtime;

import java.io.File;

public class SwingIDEImplRuntime {
	private enum tryLoadingResultType {loaded,error};
	public SwingIDEImplRuntime(){
		tryLoadingResultType result=tryLoadingFromCurrentFSFolder();
		switch(result){
		case error:
			System.err.println("Error creating/loading a current workspace.");
			System.exit(-2);
			break;
		case loaded:
			break;
		default: throw new AssertionError();
		}
	}

	/**
	 * 
	 * @return result code
	 */
	private tryLoadingResultType tryLoadingFromCurrentFSFolder() {
		java.io.File wsCfgRef=createWorkspaceConfigFileRef();
		if(!workspaceConfigFileExists(wsCfgRef))
			if(aborted_dialogTryCreatingANewWorkspace(wsCfgRef))
				return tryLoadingResultType.error;
		return tryLoadingWorkspace(wsCfgRef);
	}

	private File createWorkspaceConfigFileRef() {
		return new File("./phial_workspace.cfg");
	}

	private boolean workspaceConfigFileExists(File wscfg) {
		return wscfg.exists();
	}
}
