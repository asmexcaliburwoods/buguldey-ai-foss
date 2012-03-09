package org.east.concepts;

import org.east.concepts.utility.NameInstance;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class NameForASetOfConcepts extends Name{
  private static final long serialVersionUID = -292231924859779645L;
  private Set<NameInstance> namedConcepts=new HashSet<NameInstance>(1);
  public NameForASetOfConcepts(String name){super(name);}
  public Set<NameInstance> getNameInstances(){
    return Collections.unmodifiableSet(namedConcepts);
  }
  public synchronized void addNamedConcept(NameInstance c){
    namedConcepts.add(c);
    c.getConcept().addName(this.getName());
    //if(!namedConcepts.add(c))MsgUtil.msg("CONCEPT OVERWRITTEN: "+c.getCongetConceptId());
    saveName();
  }
}
