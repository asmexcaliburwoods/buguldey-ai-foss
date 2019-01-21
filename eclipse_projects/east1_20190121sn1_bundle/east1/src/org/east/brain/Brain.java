package org.east.brain;

import org.east.androgyn.Prick;
import org.east.androgyn.Pussy;
import org.east.concepts.Concept;
import org.east.concepts.JavaLanguageTextualContext;
import org.east.concepts.Name;
import org.east.concepts.utility.MeaningAllocator;
import org.east.deliberation.DeliberationEngine;
import org.east.will.Will;

public class Brain extends Concept{
  private Brain(){}
  public static Brain getInstance(){
    Brain brain=(Brain)Concept.resolve(Name.define(
            "Brain",
            JavaLanguageTextualContext.getInstance(),
            Brain.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new Brain();
              }
            }).getConcept().getConceptId());
    brain.setSuspended(false);
    return brain;
  }
  private Will will=new Will(this);
  private DeliberationEngine deliberationEngine=new DeliberationEngine(this);
  private Prick prick=new Prick();
  private Pussy pussy=new Pussy();
  public DeliberationEngine getDeliberationEngine(){
    return deliberationEngine;
  }
  public Will getWill(){
    return will;
  }
  public Prick getPrick(){
    return prick;
  }
  public Pussy getPussy(){
    return pussy;
  }
  /** Suspends of unsuspends all components */
  public void setSuspended(boolean suspended){
    if(isSuspended()==suspended)return;
    will.setSuspended(suspended);
  }
  /** Checks to see if all components are suspended */
  public boolean isSuspended(){
    return will.isSuspended();
  }
}
