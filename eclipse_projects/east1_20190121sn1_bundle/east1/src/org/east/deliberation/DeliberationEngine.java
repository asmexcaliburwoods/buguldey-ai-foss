package org.east.deliberation;

import org.east.brain.Brain;

import java.io.Serializable;

public class DeliberationEngine implements Serializable{
  private Brain brain;
  public DeliberationEngine(Brain brain){
    this.brain=brain;
  }
  public Brain getBrain(){
    return brain;
  }
}
