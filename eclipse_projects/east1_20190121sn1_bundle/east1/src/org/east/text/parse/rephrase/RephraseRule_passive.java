package org.east.text.parse.rephrase;

import org.east.concepts.utility.Node;
import org.east.concepts.utility.Word;
import org.east.concepts.NameableInstantiableConcept;
import org.east.e1.ParsedWord;
import org.east.pos.PartOfSpeech;
import org.east.pos.Verb;

import java.util.*;

public class RephraseRule_passive{
  /**
   * <pre>
   * x->"verb"(v0)->ParsedWord(#$Be)(be)->
   *    "verb"(v1)->[vs],
   * be->"subject"(s0)->s1
   * be->[links]
   * ==>
   * for each v in [vs]{
   *   unify v->[linksV]
   *   removeLink([links],v1)
   *   removeLink([links],s0)
   *   removeLink(v0,be)
   *   v0->new Verb(v.getDenotat(),Verb.DOES)(v2)
   *   v2.links.AddAll([linksV])
   *   v2->"passive"
   *   v2->"object"->s1
   *   v2.links.AddAll([links])
   * }
   * </pre>
    */
  public static void rephrase(Node node) throws Exception{
    rephrase0(node,new HashSet());
  }
  public static void rephrase0(Node node,Set traversedNodes) throws Exception{
    if(node==null||!traversedNodes.add(node))return;
    //new ArrList to prevent ConcModifExc
    Iterator it=node.getLinks().iterator();
    while(it.hasNext()){
      Node v0_=(Node)it.next();
      if(v0_ instanceof NameableInstantiableConcept.NamedInstance){
        NameableInstantiableConcept.NamedInstance v0=
                (NameableInstantiableConcept.NamedInstance)v0_;
        if(v0.getName().equals("verb")){
          Iterator itv0=new ArrayList(v0.getLinks()).iterator();
          while(itv0.hasNext()){
            Node m_=(Node)itv0.next();
            if(m_ instanceof ParsedWord){
              ParsedWord be=(ParsedWord)m_;
              Set mwf=be.getWord().getWordForms();
              if(mwf.size()==1){
                PartOfSpeech mpos=(PartOfSpeech)mwf.iterator().next();
                if(mpos instanceof Verb){
                  Verb mv=(Verb)mpos;
                  String mcyc=mv.getDenotat().cyclify();
                  if(mcyc.equals("#$Be")){
                    Iterator itm=new ArrayList(be.getLinks()).iterator();
                    while(itm.hasNext()){
                      Node v1_=(Node)itm.next();
                      if(v1_ instanceof NameableInstantiableConcept.NamedInstance){
                        NameableInstantiableConcept.NamedInstance v1=
                                (NameableInstantiableConcept.NamedInstance)v1_;
                        if(v1.getName().equals("verb")){
                          List vs=v1.getLinks();
                          /**
                           * x->"verb"(v0)->ParsedWord(Be)(be)->
                           *    "verb"(v1)->[vs],
                           * be->"subject"(s0)->[s1]
                           * be->[links]
                           * ==>
                           * for each v in [vs]{
                           *   removeLink([links],v1)
                           *   removeLink([links],s0)
                           *   removeLink(v0,be)
                           *   v0->new Verb(v.getDenotat(),Verb.DOES)(v2)
                           *   v2->"passive"
                           *   v2->"object"->[s1]
                           *   v2.links.AddAll([links])
                           * }
                            */
                          Iterator itm2=new ArrayList(be.getLinks()).iterator();
                          while(itm2.hasNext()){
                            Node s0_=(Node)itm2.next();
                            if(s0_ instanceof NameableInstantiableConcept.NamedInstance){
                              NameableInstantiableConcept.NamedInstance s0=
                                      (NameableInstantiableConcept.NamedInstance)s0_;
                              if(s0.getName().equals("subject")){
                                List s1=new ArrayList(s0.getLinks());
                                List links=new ArrayList(be.getLinks());
                                Iterator itvs=vs.iterator();
                                while(itvs.hasNext()){
                                  ParsedWord v=(ParsedWord)itvs.next();
                                  List linksV=v.getLinks();
                                  links.remove(v1);
                                  links.remove(s0);
                                  v0.getLinks().remove(be);
                                  Node passive=NameableInstantiableConcept.getInstance()
                                          .newInstance("passive");
                                  Set wordForms=new HashSet();
                                  Iterator wfit=v.getWord().getWordForms().iterator();
                                  while(wfit.hasNext()){
                                    PartOfSpeech pos=(PartOfSpeech)wfit.next();
                                    if(!(pos instanceof Verb)){
                                      wordForms.add(pos);
                                      continue;
                                    }
                                    Verb vv=(Verb)pos;
                                    if(!(vv.getKind()==Verb.DID)){
                                      wordForms.add(pos);
                                      continue;
                                    }
                                    wordForms.add(new Verb(vv.getDenotat(),Verb.DOES));
                                  }
                                  ParsedWord v2=new ParsedWord(
                                    new Word(v.getWord(),wordForms),v.getWordClass());
                                  v0.getLinks().add(v2);
//                                      v2.links.AddAll([linksV])
//                                  *   v2->"passive"
//                                  *   v2->"object"->[s1]
//                                  *   v2.links.AddAll([links])
                                  v2.getLinks().addAll(linksV);
                                  v2.getLinks().add(passive);
                                  Node object=NameableInstantiableConcept.getInstance()
                                          .newInstance("object");
                                  v2.getLinks().add(object);
                                  object.setLinks(s1);
                                  v2.getLinks().addAll(links);
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
          }
        }
      }
    }

    //recurse
    it=node.getLinks().iterator();
    while(it.hasNext()){
      Node node1=(Node)it.next();
      rephrase0(node1,traversedNodes);
    }
  }
}
