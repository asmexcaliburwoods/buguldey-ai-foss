package org.east.concepts;

import org.east.concepts.utility.NameInstance;

import java.util.Set;
import java.util.Iterator;

public final class EastProjectDialogueTextualContext extends TextualContext{
  private static final long serialVersionUID = 5765522860105689346L;
  private EastProjectDialogueTextualContext(){}
  public static EastProjectDialogueTextualContext getInstance(){
    String name="EastProjectDialogueTextualContext";
    Name n=Name.resolve(name);
    if(n==null)n=new NameForASetOfConcepts(name);
    Set<NameInstance> nameInstances=n.getNameInstances();
    Iterator<NameInstance> it=nameInstances.iterator();
    while(it.hasNext()){
      NameInstance nameInstance=it.next();
      if(nameInstance.getConcept().getClass().equals(
              EastProjectDialogueTextualContext.class)&&
         nameInstance.getNamingContext().getClass().equals(
              EastProjectDialogueTextualContext.class))
        return (EastProjectDialogueTextualContext)nameInstance.getConcept();
    }
    EastProjectDialogueTextualContext concept=new EastProjectDialogueTextualContext();
    NameForASetOfConcepts ns=(NameForASetOfConcepts)n;
    NameInstance nameInstance=new NameInstance();
    nameInstance.setConcept(concept);
    nameInstance.setName(n);
    nameInstance.setNamingContext(concept);
    ns.addNamedConcept(nameInstance);
    return concept;
  }
}
