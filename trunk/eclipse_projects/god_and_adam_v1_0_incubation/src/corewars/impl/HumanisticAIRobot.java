package corewars.impl;

import corewars.AIRobot;
import corewars.AIRobotVisVitalis;
import corewars.CoreWars;
import corewars.Feeling;
import corewars.Pleasure;
import corewars.Subject;
import corewars.Tiredness;
import corewars.Trajectory;
import corewars.Will;

public class HumanisticAIRobot extends Creator implements AIRobot{
	private HumanisticAIRobotWill will=new HumanisticAIRobotWill();
	private HumanisticAIRobotFeeling feeling=new HumanisticAIRobotFeeling(this);
	private String namenull;
	private CoreWars corewars;
	private final AIRobotVisVitalis vis_vitalis;

	public HumanisticAIRobot(String godsname, AIRobotVisVitalis vis_vitalis, Logger logger){
		super(logger);
		this.namenull=godsname;
		this.vis_vitalis=vis_vitalis;
	}
	
	@Override
	public CoreWars getCoreWars() {
		return corewars;
	}

	@Override
	public String getLookerDescription(Subject investigator) {
		return namenull==null?"HUMANISTIC AI ROBOT":(investigator==null?namenull:"HUMANISTIC AI ROBOT");
	}

	@Override
	public Will getWill() {
		return will;
	}

	@Override
	public void setCoreWars(CoreWars corewars) {
		this.corewars=corewars;
	}
	@Override
	protected Cognition createCognition() {
		return new HumanisticAIRobotCognition(this);
	}
	@Override
	public Object getObjectForCognition() {
		return null;//TODO
	}

	public Feeling getFeeling() {
		return feeling;
	}

	@Override
	public boolean evaluate(Trajectory trajectory) {
		return getTiredness().tired()?trajectory==null:true;
	}

	protected void setMeditating(boolean b) {
		corewars.setMeditating(this,b);
	}

	@Override
	public AIRobotVisVitalis getVisVitalis() {
		return vis_vitalis;
	}
}
