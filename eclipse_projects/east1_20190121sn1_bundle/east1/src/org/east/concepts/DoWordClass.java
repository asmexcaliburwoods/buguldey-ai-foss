package org.east.concepts;

import org.east.concepts.utility.MeaningAllocator;
import org.east.concepts.utility.Word;
import org.east.pos.PartOfSpeech;
import org.opencyc.api.CycApiException;

import java.io.IOException;
import java.util.Locale;

public class DoWordClass extends WordClassConcept{
  public boolean matchesWord(Word word) throws CycApiException, IOException{
    String w=word.getSpelling().toLowerCase(Locale.US);
    if(w.equals("do"))return true;
    if(w.equals("does"))return true;
    if(w.equals("doing"))return true;
    if(w.equals("did"))return true;
    if(w.equals("done"))return true;
    return false;
  }
  public static void define(){
    Name.define("doVerb",EastProjectDialogueTextualContext.getInstance(),
            DoWordClass.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new DoWordClass();
              }
            });
  }
  public boolean matchesWordForm(PartOfSpeech word) throws Exception, IOException{
    return false;
  }
}
