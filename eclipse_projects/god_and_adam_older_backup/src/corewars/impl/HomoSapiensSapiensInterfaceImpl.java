package corewars.impl;

import corewars.CoreWars;
import corewars.HomoSapiensSapiensInterface;
import corewars.Subject;
import corewars.Trajectory;
import corewars.Will;

public class HomoSapiensSapiensInterfaceImpl implements HomoSapiensSapiensInterface {
	private HomoSapiensSapiensWillInterfaceImpl hssWillInterface;
	
	HomoSapiensSapiensInterfaceImpl(Logger logger){
		hssWillInterface=new HomoSapiensSapiensWillInterfaceImpl(logger);
	}
	
	@Override
	public Will getWill() {
		return hssWillInterface;
	}
	@Override
	public String getLookerDescription(Subject investigator) {
		return investigator==null?"Homo sapiens sapiens interface":"UNKNOWN";
	}
	public CoreWars getCoreWars() {
		return hssWillInterface.getCoreWars();
	}
	public void setCoreWars(CoreWars corewars) {
		hssWillInterface.setCoreWars(corewars);
	}

	@Override
	public boolean evaluate(Trajectory trajectory) {
		return true;
	}

	@Override
	public void meditate() {}
}
