package org.east.concepts;

public abstract class Action extends Concept{
  protected Action(){}
  public abstract void perform(String[] arguments)throws Exception;
  public final void perform(String firstArg) throws Exception{
    perform(new String[]{firstArg});
  }
}
