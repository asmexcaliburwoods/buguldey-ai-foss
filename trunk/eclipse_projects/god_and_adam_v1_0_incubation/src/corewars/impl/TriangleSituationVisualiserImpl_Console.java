package corewars.impl;

import corewars.Situation;
import corewars.SituationVisualiser;

public class TriangleSituationVisualiserImpl_Console implements SituationVisualiser {
	private TriangleSituationVisualiserImpl_Logger loggervis;

	TriangleSituationVisualiserImpl_Console(CoreWarsImpl corewars,Logger logger){
		loggervis=new TriangleSituationVisualiserImpl_Logger(logger);
	}
	
	@Override
	public void eternityEnd() {
		loggervis.eternityEnd();
	}

	@Override
	public void eternityStart() {
		loggervis.eternityStart();
	}

	@Override
	public void set(Situation situation) {
		loggervis.set(situation);
	}
}
