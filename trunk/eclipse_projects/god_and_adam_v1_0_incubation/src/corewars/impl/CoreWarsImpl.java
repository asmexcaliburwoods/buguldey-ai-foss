package corewars.impl;

import java.util.HashMap;
import java.util.Map;

import corewars.ClassOfTrajectoriesModel;
import corewars.CoreWars;
import corewars.HomoSapiensSapiensInterface;
import corewars.Situation;
import corewars.Subject;
import corewars.Trajectory;
import corewars.ABRAXAS.ABRAXAS;

class CoreWarsImpl implements CoreWars{
  private String getVersionName() {
	  return "2012_FEB_2__0001";
  }
  
  private Map<Subject,Situation> subject2situation=new HashMap<Subject,Situation>();
  private Logger logger;
  
  public void setLogger(Logger logger) {
	this.logger = logger;
  }

/** move from eternity start until eternity end along all trajectories */
  public void eternity(){
		  for(Map.Entry<Subject,Situation> entry:subject2situation.entrySet()){
			  Subject subject=entry.getKey();
			  final Situation situation=entry.getValue();
			  if(!(subject instanceof HomoSapiensSapiensInterface)){
				  final AbstractLivingCreature animate=(AbstractLivingCreature) subject;
				  Runnable cognition=new Runnable(){
					@Override
					public void run() {
						try{
						while(true){
							boolean meditates;
							synchronized (CoreWarsImpl.this) {
								meditates=situation.isSubjectMeditating();
							}
							if(!meditates)
								animate.getCognition().cognize(animate.getObjectForCognition());
							try{Thread.sleep(33);}catch(InterruptedException e){throw new RuntimeException(e);}
						}
						}catch(Throwable tr){
							tr.printStackTrace();
						}
					}
				  };
				  new Thread(cognition, "COGNITION THREAD OF "+subject.getLookerDescription(null)).start();
			  }
		  }

		  for(Map.Entry<Subject,Situation> entry:subject2situation.entrySet()){
			  final Subject subject=entry.getKey();
			  if(!(subject instanceof HomoSapiensSapiensInterface)){
				  Runnable entity=new Runnable(){
					@Override
					public void run() {
						try{
						while(true){
							do_(subject);
							//try{Thread.sleep(33);}catch(InterruptedException e){throw new RuntimeException(e);}
						}
						}catch(Throwable tr){
							tr.printStackTrace();
						}
					}
				  };
				  new Thread(entity, "ENTITY THREAD OF "+subject.getLookerDescription(null)).start();
			  }
		  }
		  HomoSapiensSapiensInterface god=null;
		  for(Map.Entry<Subject,Situation> entry:subject2situation.entrySet()){
			  Subject subject=entry.getKey();
			  if(subject instanceof HomoSapiensSapiensInterface){god=(HomoSapiensSapiensInterface)subject;break;}
		  }
  		while(eternityLasts(god)){
  			do_(god);
		}
  	}

  private boolean eternityLasts(HomoSapiensSapiensInterface god) {
	  return true;
  }

  private void do_(Subject subject){
	  Situation situation=subject2situation.get(subject);
	  ClassOfTrajectoriesModel model=getPossibilities(situation);
	  boolean decided=false;
	  Trajectory trajectory=null;
	  for(int e=0;e<16;e++){
		  for(int i=0;i<16;i++){
			  trajectory=model.look();
			  float possibility=model.getPossibility(trajectory);
			  if(possibility<=0)continue;
			  break;
		  }
		  decided=subject.evaluate(trajectory);
		  logger.log(subject.getLookerDescription(null)+" DECIDED: "+(decided?"YES":"NO"));
		  if(decided){
			  decided=realityEvaluate(trajectory);
			  logger.log("REALITY CHECK "+(decided?"OK":"MISSING"));
		  }
		  if(decided)break;
	  }
	  if(!decided)return;
	  situation.moveAlong(trajectory);
  }

  public Situation getCurrentSituation(Subject subject){
    Situation s=subject2situation.get(subject);
    if(s==null){
      //TODO differentiate between God/Human and non-God.
      s=new HomoSapiensSapiensInterfaceSituationImpl(subject,logger);
      subject2situation.put(subject,s);
    }
    return s;
  }

  public ClassOfTrajectoriesModel getPossibilities(Situation situation){
	  return situation.getPossibilities();
  }
  public boolean realityEvaluate(Trajectory trajectory){
	  return prayABRAXAS().checkBounds(trajectory);
  }

  @Override
  public void describeSubjects() {
	  logger.log("ENUMERATING SUBJECTS");
//	    enumerate_subjects:
	  for(Map.Entry<Subject,Situation> entry:subject2situation.entrySet()){
		  Subject subject=entry.getKey();
		  Situation situation=entry.getValue();
		  logger.log(subject.getLookerDescription(null)+":\n   "+situation.getValuedDescription());
	  }
  }

  public void pause() {//TODO
  }

  public void resume() {//TODO	
  }

  /** save to safe storage - application is exiting */ 
  public void sleep() {//TODO
//	    enumerate_subjects:
      for(Map.Entry<Subject,Situation> entry:subject2situation.entrySet()){
        Subject subject=entry.getKey();
//        Situation situation=entry.getValue();
        logger.log(subject.getLookerDescription(null)+" IS GOING TO SLEEP");
        if(subject instanceof Creator){
        	Creator cogniter=(Creator) subject;
        	cogniter.getCognition().getStructuralIntellect().sleep();
        }
      }
      ABRAXAS ABRAXAS = corewars.ABRAXAS.ABRAXAS.pray();
      ABRAXAS.coda();
  }

  @Override
  public synchronized void setMeditating(Subject subject, boolean meditating) {
	  Situation situation=getCurrentSituation(subject);
	  situation.setSubjectMeditating(meditating);
  }

  public ABRAXAS prayABRAXAS() {
	ABRAXAS ABRAXAS=corewars.ABRAXAS.ABRAXAS.pray();
	return ABRAXAS;
  }

  public String getMultiverseName() {
	  return "Vis vitalis to a computer. Version name: "+getVersionName();
  }
  
  public String getIncompleteDescription(){
	  return "Use mouse wheel and words to control Homo sapiens sapiens interface. Possible words: S";
  }
}
