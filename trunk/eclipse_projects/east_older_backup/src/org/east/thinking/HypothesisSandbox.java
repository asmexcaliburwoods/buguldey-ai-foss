package org.east.thinking;

public interface HypothesisSandbox extends Sandbox{
  Hypothesis getHypothesis();
  void setHypothesis(Hypothesis hypothesis);
  void setPreviousHypothesis(Hypothesis h);
  void replace(Object oldValue,Object newValue);
}
