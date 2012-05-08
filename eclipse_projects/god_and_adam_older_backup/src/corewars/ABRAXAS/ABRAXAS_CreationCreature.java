package corewars.ABRAXAS;

import corewars.impl.Creator;

public class ABRAXAS_CreationCreature implements CreationCreature{
	private static final long serialVersionUID = 4150129944769068662L;
	private ThreadLocal<Creator> creators=new ThreadLocal<Creator>();
	ABRAXAS_CreationCreature(){}
	public void feel(Creator creator){
		creators.set(creator);
	}
}
