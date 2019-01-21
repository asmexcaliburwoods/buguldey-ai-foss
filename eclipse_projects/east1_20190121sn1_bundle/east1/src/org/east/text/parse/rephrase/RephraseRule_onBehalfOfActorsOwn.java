package org.east.text.parse.rephrase;

import org.east.concepts.utility.Node;
import org.east.concepts.NameableInstantiableConcept;

import java.util.Iterator;
import java.util.ArrayList;

public class RephraseRule_onBehalfOfActorsOwn{
  //"on-behalf-of"->"own"->actor => "on-behalf-of"->actor
  public static void rephrase(Node lo){
    if(lo==null)return;
    if(lo instanceof NameableInstantiableConcept.NamedInstance){
      NameableInstantiableConcept.NamedInstance no=
              (NameableInstantiableConcept.NamedInstance)lo;
      if(no.getName().equals("on-behalf-of")){
        Iterator it=new ArrayList(no.getLinks()).iterator();
        while(it.hasNext()){
          Node lo2=(Node)it.next();
          if(lo2 instanceof NameableInstantiableConcept.NamedInstance){
            NameableInstantiableConcept.NamedInstance no2=
                    (NameableInstantiableConcept.NamedInstance)lo2;
            if(no2.getName().equals("own")){
              no.getLinks().remove(no2);
              no.getLinks().addAll(no2.getLinks());
            }
          }
        }
      }
    }
    Iterator it2=lo.getLinks().iterator();
    while(it2.hasNext()){
      Node lo3=(Node)it2.next();
      rephrase(lo3);
    }
  }
}
