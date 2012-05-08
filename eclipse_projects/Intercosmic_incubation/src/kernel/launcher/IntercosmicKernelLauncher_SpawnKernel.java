package kernel.launcher;

import kernel.util.ExceptionUtil;
import kernel.util.NamedCaller;
import gui.util.ExceptionUtilGUI;

public class IntercosmicKernelLauncher_SpawnKernel {
	public static void main(String[] args){
		try{
			lockThiscomputerMutexForIntercosmicKernel();
			if(kernelExistsOnThisComputer()){
				unlockThiscomputerMutexForIntercosmicKernel();
				return;
			}
			installKernelOnThisComputer();
			unlockThiscomputerMutexForIntercosmicKernel();
		}catch(Throwable t){
			ExceptionUtil.handleException(t);
		}
	}
}
