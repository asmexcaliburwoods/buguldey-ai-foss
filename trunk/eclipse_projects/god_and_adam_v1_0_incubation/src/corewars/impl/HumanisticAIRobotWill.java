package corewars.impl;

import corewars.FreeWill;

public class HumanisticAIRobotWill implements FreeWill {
	public boolean isForth() {
		return Math.random()>0.5;
	}

	public boolean isNull() {
		return Math.random()<0.1;
	}
}
