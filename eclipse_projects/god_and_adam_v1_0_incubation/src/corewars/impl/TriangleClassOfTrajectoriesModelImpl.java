package corewars.impl;

import corewars.ClassOfTrajectoriesModel;
import corewars.Subject;
import corewars.Trajectory;

class TriangleClassOfTrajectoriesModelImpl implements ClassOfTrajectoriesModel{
  private boolean forth;
  private boolean isNull;
  private Trajectory trajBack=new TriangleTrajectoryImpl(false);
  private Trajectory trajForth=new TriangleTrajectoryImpl(true);
  private Trajectory[] looks=new Trajectory[]{trajForth,trajBack};
  private int nextLook;
  private Subject subject;
  private Logger logger;
  TriangleClassOfTrajectoriesModelImpl(Subject s, Logger logger){
	  this.subject=s;
	  this.logger=logger;
	  newModel();
  }
  void setModel(boolean forth,boolean isNull){this.forth=forth;this.isNull=isNull;}
  public boolean isForth(){return forth;}
  public boolean isNull(){return isNull;}
  @Override
  public float getPossibility(Trajectory trajectory) {
	  TriangleTrajectoryImpl t=(TriangleTrajectoryImpl) trajectory;
	  float possibility=t==null?0.5f:isNull==(t==null) && (t!=null&&(forth==t.isForth()))?1:-1;
	  logger.log(subject.getLookerDescription(null)+" ESTIMATED POSSIBILITY, IT IS "+possibility);
	  return possibility;
  }
  @Override
  public Trajectory look() {
	  Trajectory t=nextLook<looks.length?looks[nextLook++]:null;
	  if(t==null)logger.log(subject.getLookerDescription(null)+" DOES NOT LOOK ANYMORE");
	  else logger.log(subject.getLookerDescription(null)+" LOOKED AT "+t.getLookedAtDescription());
	  return t;
  }
  @Override
  public void newModel() {
	  nextLook=0;
  }
}
