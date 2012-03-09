package org.east.concepts;

public abstract class InstantiableConcept extends Concept{
  private static final long serialVersionUID = -6604416103844451019L;
  public abstract Object newInstance(Object[] args);
}
