package org.east.concepts;

public abstract class Action extends Concept{
  private static final long serialVersionUID = -2075917728505017244L;
  protected Action(){}
  public abstract void perform(String[] arguments)throws Exception;
  public final void perform(String firstArg) throws Exception{
    perform(new String[]{firstArg});
  }
}
