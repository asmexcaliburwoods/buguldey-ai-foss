package org.east.thinking;

import java.util.List;

/** Initially a hypothesis is nether valid nor invalid, it is unknown. */
public interface Hypothesis extends Assertion{
  public void setPreviousHypothesis(Hypothesis h);
  boolean isValid();
  boolean isInvalid();
  /** @return List of Assertion */
  List getConsequences();
  void addConsequence(Assertion a);
  String hypothesisToString();
  SetOfHypotheses getSetOfHypotheses();
  HypothesisSandbox getSandbox();
}
