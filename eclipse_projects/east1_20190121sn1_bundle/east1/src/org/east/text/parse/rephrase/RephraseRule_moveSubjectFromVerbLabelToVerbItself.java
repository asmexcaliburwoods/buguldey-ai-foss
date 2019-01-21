package org.east.text.parse.rephrase;

import org.east.concepts.utility.Node;
import org.east.concepts.NameableInstantiableConcept;
import org.east.e1.ParsedWord;

import java.util.Iterator;
import java.util.ArrayList;

public class RephraseRule_moveSubjectFromVerbLabelToVerbItself{
  //"verb"(v)->"subject"(s) && v->ParsedWord(a) => v.removeLink(s); a->s;
  public static void rephrase(Node lo){
    if(lo==null)return;
    if(lo instanceof NameableInstantiableConcept.NamedInstance){
      NameableInstantiableConcept.NamedInstance v=
              (NameableInstantiableConcept.NamedInstance)lo;
      if(v.getName().equals("verb")){
        Iterator it=new ArrayList(v.getLinks()).iterator();
        while(it.hasNext()){
          Node lo2=(Node)it.next();
          if(lo2 instanceof NameableInstantiableConcept.NamedInstance){
            NameableInstantiableConcept.NamedInstance s=
                    (NameableInstantiableConcept.NamedInstance)lo2;
            if(s.getName().equals("subject")){
              Iterator it2=v.getLinks().iterator();
              while(it2.hasNext()){
                Node lo3=(Node)it2.next();
                if(lo3 instanceof ParsedWord){
                  ParsedWord a=(ParsedWord)lo3;
                  v.getLinks().remove(s);
                  a.getLinks().add(s);
                }
              }
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
