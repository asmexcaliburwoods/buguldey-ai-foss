package org.east.brain;

import org.east.androgyn.Penis;
import org.east.androgyn.Vagina;
import org.east.concepts.Concept;
import org.east.concepts.JavaLanguageTextualContext;
import org.east.concepts.Name;
import org.east.concepts.utility.MeaningAllocator;
import org.east.deliberation.DeliberationEngine;
import org.east.will.Will;

public class Brain extends Concept{
  private static final long serialVersionUID = 5257135262374225867L;
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
  private Penis penis=new Penis();
  private Vagina vagina=new Vagina();
  public DeliberationEngine getDeliberationEngine(){
    return deliberationEngine;
  }
  public Will getWill(){
    return will;
  }
  public Penis getPenis(){
    return penis;
  }
  public Vagina getVagina(){
    return vagina;
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
