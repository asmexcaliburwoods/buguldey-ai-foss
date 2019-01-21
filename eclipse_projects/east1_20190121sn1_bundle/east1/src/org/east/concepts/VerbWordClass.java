package org.east.concepts;

import org.east.concepts.utility.MeaningAllocator;
import org.east.pos.PartOfSpeech;
import org.east.pos.Verb;

public class VerbWordClass extends WordClassConcept{
  public boolean matchesWordForm(PartOfSpeech wf) throws Exception{
    return wf instanceof Verb;
  }
  public static void define(){
    Name.define("VerbWordClass",EastProjectDialogueTextualContext.getInstance(),
            VerbWordClass.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new VerbWordClass();
              }
            });
  }
}
