package org.east.deliberation;

import org.east.brain.Brain;

import java.io.Serializable;

public class DeliberationEngine implements Serializable{
  private static final long serialVersionUID = -7842631329188076752L;
  private Brain brain;
  public DeliberationEngine(Brain brain){
    this.brain=brain;
  }
  public Brain getBrain(){
    return brain;
  }
}
