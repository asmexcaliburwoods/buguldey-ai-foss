package corewars.impl;

import corewars.Joy;

public class HumanisticAIRobotJoy implements Joy {

	private HumanisticAIRobotPleasure humanPleasure;

	public HumanisticAIRobotJoy(HumanisticAIRobotPleasure humanPleasure) {
		this.humanPleasure=humanPleasure;
	}

	@Override
	public void feelJoy() {
		HumanisticAIRobot human = humanPleasure.getHuman();
		human.getLogger().log(human.getLookerDescription(null)+" FEELS JOY");
	}

	@Override
	public void feel() {
		feelJoy();
	}
}
