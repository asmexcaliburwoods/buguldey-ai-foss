package corewars.impl;

import corewars.Feeling;
import corewars.Joy;
import corewars.Pleasure;
import corewars.Will;

public class HumanisticAIRobotCognition extends Cognition {
	@SuppressWarnings("unused")
	private HumanisticAIRobot subject;
	@SuppressWarnings("unused")
	private Will will;
	private final HumanisticAIRobotPleasure nullPleasure;
	private final HumanisticAIRobotJoy nullJoy;

	HumanisticAIRobotCognition(HumanisticAIRobot human){
		this.subject=human;
		this.will=human.getWill();
		nullPleasure=new HumanisticAIRobotPleasure(human);
		nullJoy=new HumanisticAIRobotJoy(nullPleasure);
	}

	public Feeling getFeeling(Object object) {
		return object==null?(Math.random()<0.5?nullPleasure:nullJoy):super.getFeeling(object);
	}
}
