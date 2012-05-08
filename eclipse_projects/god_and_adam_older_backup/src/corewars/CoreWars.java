package corewars;

import corewars.impl.HumanisticAIRobot;

public interface CoreWars{
  Situation getCurrentSituation(Subject subject);
  ClassOfTrajectoriesModel getPossibilities(Situation situation);
  /** move from eternity start until eternity end along all trajectories */
  void eternity();
  boolean realityEvaluate(Trajectory trajectory);
  void describeSubjects();
  void setMeditating(Subject subject, boolean meditating);
}

