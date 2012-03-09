package org.east.concepts;

import org.east.concepts.utility.MeaningAllocator;
import org.east.pos.Noun;
import org.east.pos.PartOfSpeech;

public class NounWordClass extends WordClassConcept{
  private static final long serialVersionUID = 1165489285527120711L;
  public boolean matchesWordForm(PartOfSpeech wf) throws Exception{
    if(wf instanceof Noun)return true;
    return false;
  }
  public static void define(){
    Name.define("NounWordClass",EastProjectDialogueTextualContext.getInstance(),
            NounWordClass.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new NounWordClass();
              }
            });
  }
}
