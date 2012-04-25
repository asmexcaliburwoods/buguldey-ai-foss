package corewars.impl;

import adios.ADIOS;
import corewars.Feeling;

public class Cognition {
	private ADIOS adios=new ADIOS();
	{
		adios.resume();
	}

	public void cognize(Object object){
		Feeling f=cognitionBringsFeelings(object);
		if(f!=null)f.feel();
	}

	private Feeling cognitionBringsFeelings(Object object) {
		return Math.random()<0.25?getFeeling(object):null;
	}

	public Feeling getFeeling(Object object) {
		return null;
		//		return object==null?(Math.random()<0.5?pleasure:joy);

	}
	
	public ADIOS getStructuralIntellect(){
		return adios;
	}
}
