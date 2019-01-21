package org.east.e1;

import org.east.concepts.Concept;
import org.east.concepts.Name;
import org.east.concepts.utility.CycConcept;
import org.east.cyc.CycLink;

public class WordFormFactory{
  public static Object newInstance(String wordForm){
    if(wordForm.startsWith("#$")){
      CycConcept cycConcept=null;
      try{
        cycConcept=CycLink.resolveSingleConcept(wordForm);
      } catch(Exception e){
        throw new RuntimeException(e);
      }
      if(cycConcept!=null)return cycConcept;
      return unrecognized(wordForm);
    }
    Concept concept=Name.resolveSingleConcept(wordForm);
    if(concept!=null)return concept;
    return unrecognized(wordForm);
  }
  private static Object unrecognized(String wordForm){
    throw new RuntimeException("Unrecognized word form: "+wordForm);
  }
}
