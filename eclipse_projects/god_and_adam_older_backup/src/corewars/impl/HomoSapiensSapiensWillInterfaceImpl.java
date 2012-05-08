package corewars.impl;

import corewars.CoreWars;
import corewars.FreeWill;

public class HomoSapiensSapiensWillInterfaceImpl implements FreeWill {
	private CoreWars corewars;
	private Logger logger;
	public CoreWars getCoreWars() {
		return corewars;
	}
	public void setCoreWars(CoreWars corewars) {
		this.corewars = corewars;
	}
	HomoSapiensSapiensWillInterfaceImpl(Logger logger){
		this.logger=logger;
	}
	public boolean isForth() {
		for(;;){
			String will=logger.input();
			if(will!=null)will=will.toUpperCase();
			if("S".equals(will)){
				if(corewars==null)throw new AssertionError();
				corewars.describeSubjects();
				continue;
			}
			if(will.startsWith(WorldVisualiser_Swing.MousWheelRotationEvent.PREFIX)){
				will=will.substring(WorldVisualiser_Swing.MousWheelRotationEvent.PREFIX.length());
			}
			try{
				int i=Integer.parseInt(will);
				boolean forth=i>0;
				logger.log("IT MEANS: "+(forth?"GO FORTH":"GO BACK"));
				return forth;
			}catch(NumberFormatException e){
				logger.log("LITTLE MEANING");
				continue;
			}
		}
	}
	public boolean isNull() {
		return false;
	}
}
