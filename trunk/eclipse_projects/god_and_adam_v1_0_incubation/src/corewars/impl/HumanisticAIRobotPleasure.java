package corewars.impl;

import corewars.Joy;
import corewars.Pleasure;

public class HumanisticAIRobotPleasure implements Pleasure {
	
	private HumanisticAIRobot human;
	private Joy joy=new HumanisticAIRobotJoy(this);

	HumanisticAIRobotPleasure(HumanisticAIRobot human){
		this.human=human;
	}

	@Override
	public void feel() {
		human.getLogger().log(human.getLookerDescription(null)+" FEELS PLEASURE");
	}

	public HumanisticAIRobot getHuman() {
		return human;
	}
}
