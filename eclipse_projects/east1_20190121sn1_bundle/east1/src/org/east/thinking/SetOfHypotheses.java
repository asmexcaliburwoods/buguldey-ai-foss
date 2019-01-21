package org.east.thinking;

import java.util.List;
import java.io.Serializable;

public interface SetOfHypotheses extends Serializable{
  void pickNextAlternative() throws Exception;
  List getHypotheses();
  void addHypothesis(Hypothesis h);
}
