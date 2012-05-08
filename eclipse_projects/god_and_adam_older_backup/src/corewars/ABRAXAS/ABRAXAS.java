package corewars.ABRAXAS;

import java.io.Serializable;

import corewars.Trajectory;

public class ABRAXAS implements Serializable{
	private static final ABRAXAS ABRAXAS = live();
	private corewars.ABRAXAS.ABRAXAS_CreationCreature creationCreature;

	private ABRAXAS(){
		//TODO enable throw new UnsupportedOperationException();
	}
	
	public void perform(){}

	private static ABRAXAS live() {
		//TODO load
		ABRAXAS ABRAXAS=new ABRAXAS();
		ABRAXAS.creationCreature=new ABRAXAS_CreationCreature();
		return ABRAXAS;
	}

	public static ABRAXAS pray() {
		return corewars.ABRAXAS.ABRAXAS.ABRAXAS;
	}
	
	public void coda(){
		//TODO save
	}

	public ABRAXAS_CreationCreature getCreationCreature() {
		return creationCreature;
	}

	public boolean checkBounds(Trajectory trajectory) {
		return !isDangerous(trajectory);
	}

	private boolean isDangerous(Trajectory trajectory) {
		return false;
	}
}
