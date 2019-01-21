package org.east.e1.semaction;

import java.io.Serializable;

public abstract class Statement implements Serializable{
  public abstract void execute(Scope ctx) throws Exception;
}
