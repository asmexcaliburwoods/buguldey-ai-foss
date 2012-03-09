package org.east.concepts;

import org.east.concepts.utility.MeaningAllocator;
import org.east.pos.Adverb;
import org.east.pos.PartOfSpeech;

public class AdverbWordClass extends WordClassConcept{
  private static final long serialVersionUID = 1993831705745133830L;
  public boolean matchesWordForm(PartOfSpeech wf) throws Exception{
    if(wf instanceof Adverb)return true;
    return false;
  }
  public static void define(){
    Name.define("AdverbWordClass",EastProjectDialogueTextualContext.getInstance(),
            AdverbWordClass.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new AdverbWordClass();
              }
            });
  }
}
