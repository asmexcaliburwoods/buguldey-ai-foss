package corewars.impl;

import java.beans.PropertyChangeSupport;

import corewars.ClassOfTrajectoriesModel;
import corewars.Situation;
import corewars.Subject;
import corewars.Trajectory;
import corewars.Will;
import corewars.ABRAXAS.WORK;

class TriangleSituationImpl implements Situation{
	private Subject subject;
	private TriangleClassOfTrajectoriesModelImpl tmodel;
	private final PropertyChangeSupport propertyChangeSupport=
		new PropertyChangeSupport(this);
	private Logger logger; 
	TriangleSituationImpl(Subject s, Logger logger){
		subject=s;
		this.logger=logger;
		tmodel=new TriangleClassOfTrajectoriesModelImpl(subject,logger);
	}
	/** [-pi..pi] */
	private float angle;
	private boolean subjectMeditating;
	@Override
	public Subject getSubject() {
		return subject;
	}
	
	public float getAngle() {
		return angle;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}
	public final static String PROPERTY_NAME_ANGLE="angle";
	
	private static final int STEPS_PER_MOVE=8;
	private static final float STEP=(float)(Math.PI/6/STEPS_PER_MOVE);
	
	//code so that situation visualizer will animate this movement
	public void moveAlong(Trajectory t){
		if(t instanceof WORK){//TODO
			Creator creator=(Creator) subject;
			creator.work((WORK)t);
		}
		if(subject instanceof HumanisticAIRobot){
			HumanisticAIRobot human=(HumanisticAIRobot) subject;
			human.getFeeling().feel();
		}
		if(t==null){
			subject.meditate();
			return;
		}
		TriangleTrajectoryImpl ti=(TriangleTrajectoryImpl)t;
		for(int i=0;i<STEPS_PER_MOVE;i++){
			float sign=ti.isForth()?1:-1;
			float old=angle;
			angle+=STEP*sign;
			propertyChangeSupport.firePropertyChange(PROPERTY_NAME_ANGLE, old, angle);
			if(subject instanceof AbstractLivingCreature){
				AbstractLivingCreature c=(AbstractLivingCreature) subject;
				c.getTiredness().increase(0.5f);
			}
			try{Thread.sleep(50);}catch(InterruptedException e){throw new RuntimeException(e);}
		}
	}
	@Override
	public ClassOfTrajectoriesModel getPossibilities() {
		tmodel.newModel();
		Will will=subject.getWill();
		if(will instanceof HomoSapiensSapiensWillInterfaceImpl){
			HomoSapiensSapiensWillInterfaceImpl w=(HomoSapiensSapiensWillInterfaceImpl) will;
			tmodel.setModel(w.isForth(),w.isNull());
		}else
			if(will instanceof HumanisticAIRobotWill){
				HumanisticAIRobotWill w=(HumanisticAIRobotWill) will;
				tmodel.setModel(w.isForth(),w.isNull());
			}else
				logger.log("UNKNOWN WILL KIND");
		return tmodel;
	}

	@Override
	public String getValuedDescription() {
		return TriangleSituationVisualiserImpl_Logger.getAngleDescription(angle);
	}

	@Override
	public boolean isSubjectMeditating() {
		return subjectMeditating;
	}

	@Override
	public void setSubjectMeditating(boolean meditating) {
		subjectMeditating=meditating;		
	}
}
