package org.east.text.parse.rephrase;

import org.east.concepts.utility.Node;
import org.east.concepts.NameableInstantiableConcept;
import org.east.e1.ParsedWord;
import org.east.pos.PartOfSpeech;
import org.east.pos.Verb;

import java.util.*;

public class RephraseRule_modalVerbs2{
  /**
   * <pre>
   * x->"verb"(v0)->ParsedWord(Must|Should|Can)(m)->
   *    "verb"(v1)->[vs],
   * m->"subject"(s0),
   * m->[links]
   * ==>
   * [links] remove v1 and s0
   * removeLink(v0,m)
   * removeLink(m,v1)
   * removeLink(m,s0)
   * "verb"(v0)->[v]
   * for each v in [vs] v->"modality"(mo)->m,v->s0,
   * if([links].size>0) v->"unknown-links"->[links]
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
              ParsedWord m=(ParsedWord)m_;
              Set mwf=m.getWord().getWordForms();
              if(mwf.size()==1){
                PartOfSpeech mpos=(PartOfSpeech)mwf.iterator().next();
                if(mpos instanceof Verb){
                  Verb mv=(Verb)mpos;
                  String mcyc=mv.getDenotat().cyclify();
                  if(mcyc.equals("#$Must")||
                     mcyc.equals("#$Can")||
                     mcyc.equals("#$Should")){
                    Iterator itm=new ArrayList(m.getLinks()).iterator();
                    while(itm.hasNext()){
                      Node v1_=(Node)itm.next();
                      if(v1_ instanceof NameableInstantiableConcept.NamedInstance){
                        NameableInstantiableConcept.NamedInstance v1=
                                (NameableInstantiableConcept.NamedInstance)v1_;
                        if(v1.getName().equals("verb")){
                          List vs=v1.getLinks();
                          Iterator itm2=new ArrayList(m.getLinks()).iterator();
                          while(itm2.hasNext()){
                            Node s0_=(Node)itm2.next();
                            if(s0_ instanceof NameableInstantiableConcept.NamedInstance){
                              NameableInstantiableConcept.NamedInstance s0=
                                      (NameableInstantiableConcept.NamedInstance)s0_;
                              if(s0.getName().equals("subject")){
                                List links=new ArrayList(m.getLinks());
                                links.remove(v1);
                                links.remove(s0);
                                v0.getLinks().remove(m);
                                m.getLinks().remove(v1);
                                m.getLinks().remove(s0);
                                v0.getLinks().addAll(vs);
                                Iterator itvs=vs.iterator();
                                while(itvs.hasNext()){
                                  Node v=(Node)itvs.next();
                                  Node mo=NameableInstantiableConcept.getInstance()
                                          .newInstance("modality");
                                  v.getLinks().add(s0);
                                  v.getLinks().add(mo);
                                  mo.getLinks().add(m);
                                  if(links.size()>0){
                                    Node ul=NameableInstantiableConcept.getInstance()
                                            .newInstance("unknown-links");
                                    v.getLinks().add(ul);
                                    ul.setLinks(links);
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
    }

    //recurse
    it=node.getLinks().iterator();
    while(it.hasNext()){
      Node node1=(Node)it.next();
      rephrase0(node1,traversedNodes);
    }
  }
}
