package org.east.concepts;

import org.east.concepts.utility.NameInstance;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NameForASetOfConcepts extends Name{
  private Set namedConcepts=new HashSet(1);
  public NameForASetOfConcepts(String name){super(name);}
  public Set getNameInstances(){
    return Collections.unmodifiableSet(namedConcepts);
  }
  public synchronized void addNamedConcept(NameInstance c){
    namedConcepts.add(c);
    c.getConcept().addName(this.getName());
    //if(!namedConcepts.add(c))MsgUtil.msg("CONCEPT OVERWRITTEN: "+c.getCongetConceptId());
    saveName();
  }
}
