package org.east.concepts;

import org.east.concepts.utility.NodeImpl;
import org.east.concepts.utility.MeaningAllocator;

public class NameableInstantiableConcept extends InstantiableConcept{
  private static final String NAME="NameableInstantiableConcept";
  public static void define(){
    Name.define(NAME, EastProjectDialogueTextualContext.getInstance(),
            NameableInstantiableConcept.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new NameableInstantiableConcept();
              }
            });
  }
  public static NameableInstantiableConcept getInstance(){
    return (NameableInstantiableConcept)Name.resolveSingleConcept(NAME);
  }
  public Object newInstance(Object[] args){
    return newInstance((String)args[0]);
  }
  public NamedInstance newInstance(String name){
    return new NamedInstance(name);
  }
  public static class NamedInstance extends NodeImpl{
    protected NamedInstance(String name){
      super(name);
    }
    /** Must match Object.equals(Object), otherwise we will remove
     * incorrect objects on Node.getLinks().remove(Object) */
    public final boolean equals(Object o){
      return super.equals(o);
//      if(o==null||!(o instanceof NamedInstance))
//        return false;
//      NamedInstance n=(NamedInstance)o;
//      return n.name.equals(name);
    }
  }
}
