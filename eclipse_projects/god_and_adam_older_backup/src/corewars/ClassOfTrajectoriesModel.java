package corewars;

public interface ClassOfTrajectoriesModel{
  /** @return [-1...1] */
  float getPossibility(Trajectory trajectory);
  /** @return null if no more trajectories */
  Trajectory look();
  void newModel();
}
