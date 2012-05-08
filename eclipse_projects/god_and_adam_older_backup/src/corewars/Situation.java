package corewars;

public interface Situation{
  Subject getSubject();
  ClassOfTrajectoriesModel getPossibilities();
  void moveAlong(Trajectory t);
  /** Get a description which has value */
  String getValuedDescription();
  boolean isSubjectMeditating();
  void setSubjectMeditating(boolean meditating);
}
