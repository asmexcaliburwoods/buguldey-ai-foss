package corewars.impl;

import corewars.Tiredness;

public class LivingCreatureTiredness implements Tiredness {
	private float tirednessPercents;
	@Override
	public void increase(float increment) {
		tirednessPercents+=increment;
	}

	@Override
	public void rest(long milliseconds) {
		float seconds=milliseconds/1000f;
		float decrement=seconds*100f;
		tirednessPercents-=decrement;
		if(tirednessPercents<0)tirednessPercents=0;
	}

	@Override
	public boolean tired() {
		return tirednessPercents>=100;
	}

}
