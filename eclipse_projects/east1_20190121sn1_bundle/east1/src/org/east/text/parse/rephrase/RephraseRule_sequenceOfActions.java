package org.east.text.parse.rephrase;

import org.east.concepts.utility.Node;
import org.east.concepts.NameableInstantiableConcept;
import org.east.e1.ParsedWord;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class RephraseRule_sequenceOfActions{
  /**
   <pre>
   REPHRASE1.rule1{
     if(unify "treesFrame"(treesFrame)
                ->(link0)subject->(link1)"sequence-of-actions"->(link2)[listOfElements])
     {
       treesFrame->"sequence-of-events"(seq2);
       forall i in 0...listOfElements.size()-1 do {
         //if(listOfElements.get(i).getWordClass.getConceptName.equals("actionVerb"))
         seq2->listOfElements.get(i);
         if(listOfElements.get(i).equals("verb"))
           listOfElements.get(i)->"subject"->subject;
       }
       link1.remove();
       link2.removeAll();
       unify subject->[listOfElements2];
       if(listOfElements2.isEmpty())
         link0.remove();
   }
   </pre>
  */
  public static void rephrase(Node sentence){
    if(sentence==null)return;
    //new ArrList to prevent ConcModifExc
    Iterator it=new ArrayList(sentence.getLinks()).iterator();
    while(it.hasNext()){
      Node link0=(Node)it.next();
      if(link0 instanceof ParsedWord){
        ParsedWord subject=(ParsedWord)link0;
        Iterator it2=new ArrayList(subject.getLinks()).iterator();
        while(it2.hasNext()){
          Node link1=(Node)it2.next();
          if(link1 instanceof NameableInstantiableConcept.NamedInstance){
            NameableInstantiableConcept.NamedInstance link1_=
                    (NameableInstantiableConcept.NamedInstance)link1;
            if(link1_.getName().equals("sequence-of-actions")){
              List listOfElements=link1_.getLinks();

              //treesFrame->"sequence-of-events"(seq2);
              NameableInstantiableConcept.NamedInstance seq2=
                NameableInstantiableConcept.getInstance().
                        newInstance("sequence-of-events");
              sentence.getLinks().add(seq2);

              //forall i in 0...listOfElements.size()-1 do
              //  if(listOfElements.get(i).equals("verb"))
              //    seq2->listOfElements.get(i)->"subject"->subject;
              Iterator it3=listOfElements.iterator();
              while(it3.hasNext()){
                Node lo3=(Node)it3.next();
                seq2.getLinks().add(lo3);
                boolean unify4=false;
                if(lo3 instanceof NameableInstantiableConcept.NamedInstance){
                  NameableInstantiableConcept.NamedInstance n3=
                    (NameableInstantiableConcept.NamedInstance)lo3;
                  if(n3.getName().equals("verb"))unify4=true;
                }
                if(unify4){
                  NameableInstantiableConcept.NamedInstance subject2=
                    NameableInstantiableConcept.getInstance().
                            newInstance("subject");
                  lo3.getLinks().add(subject2);
                  subject2.getLinks().add(subject);
                }
              }

              //link1.remove();
              subject.getLinks().remove(link1);

              //link2.removeAll();
              link1.getLinks().removeAll(listOfElements);

              //unify object->[listOfElements2];
              List listOfElements2=subject.getLinks();

              //       if(listOfElements2.isEmpty())
              //         link0.remove();
              if(listOfElements2.isEmpty())
                sentence.getLinks().remove(link0);
            }
          }
        }
      }
    }
  }
}
