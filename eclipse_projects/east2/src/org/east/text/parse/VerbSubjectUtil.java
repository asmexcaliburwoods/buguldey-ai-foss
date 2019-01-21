package org.east.text.parse;

import org.east.concepts.NameableInstantiableConcept;
import org.east.concepts.utility.Node;
import org.east.e1.ParsedWord;
import org.east.pos.PartOfSpeech;
import org.east.pos.Verb;

import java.util.Iterator;
import java.util.Set;

public class VerbSubjectUtil{
  public static interface SubjectAction{
    void apply(NameableInstantiableConcept.NamedInstance subjectLabel) throws Exception;
  }
  /** Returns true iff found at least one subject */
  public static boolean applyActionToEachSubjectForAVerb(Node verb,SubjectAction action) throws Exception{
    boolean appliedToAtLeastOneSubject=false;
    Iterator it=verb.getLinks().iterator();
    while(it.hasNext()){
      Object o=it.next();
      if(o instanceof NameableInstantiableConcept.NamedInstance){
        NameableInstantiableConcept.NamedInstance n=
                (NameableInstantiableConcept.NamedInstance)o;
        if(n.getName().equals("subject")){
          action.apply(n);
          appliedToAtLeastOneSubject=true;
        }
      }
    }
    //pw(#$Be)(be)->"able-to"->"verb"->verb
    //pw(#$Be)(be)->"subject"->subject
    Node verbParent=verb.getParent();
    if(verbParent instanceof NameableInstantiableConcept.NamedInstance){
      NameableInstantiableConcept.NamedInstance n=
              (NameableInstantiableConcept.NamedInstance)verbParent;
      if(n.getName().equals("verb")){
        Node np=n.getParent();
        if(np instanceof NameableInstantiableConcept.NamedInstance){
          NameableInstantiableConcept.NamedInstance nn=
                  (NameableInstantiableConcept.NamedInstance)np;
          if(nn.getName().equals("able-to")){
            Node nnp=nn.getParent();
            if(nnp instanceof ParsedWord){
              ParsedWord pw=(ParsedWord)nnp;
              Set wf=pw.getWord().getWordForms();
              Iterator it2=wf.iterator();
              while(it2.hasNext()){
                PartOfSpeech pos=(PartOfSpeech)it2.next();
                if(pos instanceof Verb){
                  Verb v=(Verb)pos;
                  if(v.getDenotat().cyclify().equals("#$Be")){
                    Iterator it3=pw.getLinks().iterator();
                    while(it3.hasNext()){
                      Object o=it3.next();
                      if(o instanceof NameableInstantiableConcept.NamedInstance){
                        NameableInstantiableConcept.NamedInstance n4=
                                (NameableInstantiableConcept.NamedInstance)o;
                        if(n4.getName().equals("subject")){
                          action.apply(n);
                          appliedToAtLeastOneSubject=true;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return appliedToAtLeastOneSubject;
  }
}
