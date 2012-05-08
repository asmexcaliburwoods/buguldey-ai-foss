package corewars.impl;

import corewars.Joy;
import corewars.Pleasure;

public class HumanisticAIRobotFeeling implements Pleasure {
	
	private HumanisticAIRobot human;
	private HumanisticAIRobotPleasure pleasure;

	HumanisticAIRobotFeeling(HumanisticAIRobot human){
		this.human=human;
		pleasure=new HumanisticAIRobotPleasure(human);
	}

	@Override
	public void feel() {
		if(human.getTiredness().tired())
			human.getLogger().log(human.getLookerDescription(null)+" FEELS TIRED");
		pleasure.feel();
	}

	public HumanisticAIRobot getHuman() {
		return human;
	}
}
