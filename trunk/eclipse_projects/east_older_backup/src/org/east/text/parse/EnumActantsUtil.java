package org.east.text.parse;

import org.east.concepts.NameableInstantiableConcept;
import org.east.concepts.utility.Node;

import java.util.Iterator;
import java.util.ArrayList;

public class EnumActantsUtil{
  public static interface EnumActantsAction{
    void apply(String actantLabel,Object actantInstance);
  }
  public static void enumActants(Node verb, final EnumActantsAction action) throws Exception{
    Iterator it=new ArrayList(verb.getLinks()).iterator();
    while(it.hasNext()){
      Object o=it.next();
      if(o instanceof NameableInstantiableConcept.NamedInstance){
        NameableInstantiableConcept.NamedInstance n=
                (NameableInstantiableConcept.NamedInstance)o;
        String name=n.getName();
        if(name.equals("to"))handleActantLabel(name,n,action);
        else if(name.equals("from"))handleActantLabel(name,n,action);
        else if(name.equals("object"))handleActantLabel(name,n,action);
        else if(name.equals("subject")){
          VerbSubjectUtil.applyActionToEachSubjectForAVerb(
            verb,
            new VerbSubjectUtil.SubjectAction(){
            public void apply(NameableInstantiableConcept.NamedInstance subjectLabel)
                    throws Exception{
              handleActantLabel(subjectLabel.getName(),subjectLabel,action);
            }
          });
        }
      }
    }
  }
  private static void handleActantLabel(String name,
                                        NameableInstantiableConcept.NamedInstance n,
                                        EnumActantsAction action){
    Iterator it=new ArrayList(n.getLinks()).iterator();
    while(it.hasNext()){
      Object o=it.next();
      action.apply(name,o);
    }
  }
}
