package corewars.impl;

import corewars.Tiredness;

public abstract class AbstractLivingCreature {
	private Cognition cognition=createCognition();
	Cognition getCognition(){return cognition;}
	protected abstract Cognition createCognition();
	public abstract Object getObjectForCognition();
	private Tiredness tiredness=new LivingCreatureTiredness();
	public Tiredness getTiredness(){return tiredness;}
}
