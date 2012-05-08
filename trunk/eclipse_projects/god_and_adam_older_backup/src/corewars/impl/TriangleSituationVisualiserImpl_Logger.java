package corewars.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import corewars.Situation;
import corewars.SituationVisualiser;

public class TriangleSituationVisualiserImpl_Logger implements SituationVisualiser {
	private TriangleSituationImpl situation;
	private Logger logger;

	TriangleSituationVisualiserImpl_Logger(Logger logger){
		this.logger=logger;
	}
	@Override
	public void eternityEnd() {
		logger.log(situation.getSubject().getLookerDescription(null)+" FEELS: ETERNITY ENDED");
	}

	@Override
	public void eternityStart() {
		assert(situation!=null);
		logger.log(situation.getSubject().getLookerDescription(null)+" FEELS: ETERNITY STARTED");
	}

	@Override
	public void set(Situation situation) {
		this.situation=(TriangleSituationImpl)situation;
		if(logger.areAnglesLogged())
			this.situation.getPropertyChangeSupport().addPropertyChangeListener(
				TriangleSituationImpl.PROPERTY_NAME_ANGLE, new PropertyChangeListener(){
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						float angle=(Float)evt.getNewValue();
						logger.log(TriangleSituationVisualiserImpl_Logger.this.situation.getSubject().getLookerDescription(null)+" FEELS: "+getAngleDescription(angle));
					}});
	}

	public static String getAngleDescription(float angle) {
		int degrees=Math.round(180/((float)Math.PI)*angle);
		String description = "ANGLE: "+degrees+" DEGREES";
		return description;
	}
}
