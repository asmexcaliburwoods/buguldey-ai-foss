package org.east.text.parse;

import org.east.concepts.NameableInstantiableConcept;
import org.east.concepts.UIDGenerator;
import org.east.concepts.utility.Node;
import org.east.cyc.CycLink;
import org.east.e1.ParsedWord;
import org.east.gui.workbench.treesFrame.DisplayableNodeImpl;
import org.east.gui.workbench.treesFrame.TreeFactory;
import org.east.pos.Noun;
import org.east.pos.PartOfSpeech;
import org.east.thinking.Hypothesis;
import org.east.thinking.HypothesisImpl;
import org.east.thinking.SetOfHypotheses;
import org.east.thinking.SetOfHypothesesImpl;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;
import java.util.*;

public class IdentifyEntities{
  public static class Object implements Serializable{
    private final UIDGenerator.UID uid=UIDGenerator.getInstance().createUID();
    public UIDGenerator.UID getUid(){
      return uid;
    }
    private Node tree;
    public Node getTree(){
      return tree;
    }
    public String toString(){
      return CycLink.getEastProjectConstantNamePrefix()+"Entity-"+getUid();
    }
  }
  //Tree locations that apply:
  //"object"->object
  //"subject"->object
  //"on-behalf-of"
  public static Node identifyEntities(Node text) throws Exception{
    Node objects=
            NameableInstantiableConcept.getInstance().newInstance("entities");
    Set context=new HashSet();
    if(text!=null)enumEntities(text,objects,context);
    return objects;
  }
  private static void enumEntities(Node node, Node objects,Set ctx) throws Exception{
    if(node instanceof NameableInstantiableConcept.NamedInstance){
      NameableInstantiableConcept.NamedInstance nn=
              (NameableInstantiableConcept.NamedInstance)node;
      String nnn=nn.getName();
      if(nnn.equals("sentence-tree")){
        ctx.addAll(objects.getLinks());
      }else if(nnn.equals("object")||
              nnn.equals("subject")||
              nnn.equals("on-behalf-of")||
              nnn.equals("to")){
        Iterator it=nn.getLinks().iterator();
        while(it.hasNext()){
          java.lang.Object o=it.next();
          if(o instanceof Node){
            Node objectInstance=(Node)o;
            addMarkObject(objectInstance,objects,ctx);
          }
        }
      }
    }
    Iterator it2=node.getLinks().iterator();
    while(it2.hasNext()){
      java.lang.Object o=it2.next();
      if(!(o instanceof Node))continue;
      Node node2=(Node)o;
      enumEntities(node2,objects,ctx);
    }
  }
  private static void addMarkObject(final Node object, final Node objects, final Set ctx) throws Exception{
    //List qualifications=getQualifications(object);
    Iterator it=ctx.iterator();
    while(it.hasNext()){
      final Object object2=(Object)it.next();
      //List qualifications2=getQualifications(object2.getTree());
      if(nodeEquals(object2.getTree(), object)/*&&
              forestsAreEqual(qualifications, qualifications2)*/){
        spawnHypothesis(object, object2, objects, ctx);
        return;
      }
    }
    Object o=new Object();
    o.tree=object;
    objects.getLinks().add(o);
    ctx.add(o);
    markAsSubstituted(object, o, null); //
  }
  private static void spawnHypothesis(final Node object, final Object object2, final Node objects, final Set ctx) throws Exception{
    SetOfHypotheses set=new SetOfHypothesesImpl();
    new HypothesisImpl(set){
      protected void assertImpl(){
        markAsSubstituted(object, object2, this);
      }
      protected void retractImpl(){
        removeMarkAsSubstituted(object);
      }
      public String hypothesisToString(){
        return "phrases represent identical entities: "+object+" and "+object2+" ("+object2.getTree()+")";
      }
    };
    new HypothesisImpl(set){
      private Object o;
      protected void assertImpl(){
        o=new Object();
        o.tree=object;
        objects.getLinks().add(o);
        ctx.add(o);
        markAsSubstituted(object, o, this);
      }
      protected void retractImpl(){
        objects.getLinks().remove(o);
        ctx.remove(o);
        removeMarkAsSubstituted(object);
      }
      public String hypothesisToString(){
        return "phrases represent different entities: "+object+" and "+object2+" ("+object2.getTree()+")";
      }
    };
    set.pickNextAlternative();
  }
  public static class Substitute extends DisplayableNodeImpl{
    /** May return null if the substitute is unconditional */
    public Hypothesis getHypothesis(){
      return hypothesis;
    }
    private Hypothesis hypothesis;
    public Object getSubstitutedBy(){
      return substitutedBy;
    }
    private Object substitutedBy;
    private Substitute(Object substitutedBy, Hypothesis h){
      this.substitutedBy=substitutedBy;
      this.hypothesis=h;
    }
    public DefaultMutableTreeNode toTree(TreeFactory tf){
      DefaultMutableTreeNode subst=tf.createTreeNode("substituted-by-object");
      DefaultMutableTreeNode name=tf.addChild(subst,substitutedBy);
      tf.addChild(subst,hypothesis==null?
              tf.createTreeNode("hypothesis: unconditional"):hypothesis.toTree(tf));
      tf.addChild(name,substitutedBy.getTree());
      return subst;
    }
  }
  private static void removeMarkAsSubstituted(Node object){
    Iterator it=object.getLinks().iterator();
    while(it.hasNext()){
      java.lang.Object o=it.next();
      if(o instanceof Substitute){
        object.getLinks().remove(o);
        return;
      }
    }
  }
  private static void markAsSubstituted(Node object, Object object2, Hypothesis h){
    //check if already substituted
    Iterator it=object.getLinks().iterator();
    while(it.hasNext()){
      java.lang.Object o=it.next();
      if(o instanceof Substitute)
        return;//already marked
    }
    //mark as substituted
    object.getLinks().add(new Substitute(object2,h));
  }
  private static boolean nodeEquals(Node a1, Node a2) throws Exception{
    if(!a1.getClass().equals(a2.getClass()))return false;
    if(a1 instanceof NameableInstantiableConcept.NamedInstance){
      NameableInstantiableConcept.NamedInstance n1=
              (NameableInstantiableConcept.NamedInstance)a1;
      NameableInstantiableConcept.NamedInstance n2=
              (NameableInstantiableConcept.NamedInstance)a2;
      return(n1.getName().equals(n2.getName()));
    }
    if(a1 instanceof ParsedWord){
      ParsedWord n1=(ParsedWord)a1;
      ParsedWord n2=(ParsedWord)a2;
      if(n1.getWord().getSpelling().equalsIgnoreCase(n2.getWord().getSpelling()))
        return true;
      Iterator it1=n1.getWord().getWordForms().iterator();
      while(it1.hasNext()){
        PartOfSpeech pos1=(PartOfSpeech)it1.next();
        if(!(pos1 instanceof Noun))continue;
        Noun noun1=(Noun)pos1;
        Iterator it2=n2.getWord().getWordForms().iterator();
        while(it2.hasNext()){
          PartOfSpeech pos2=(PartOfSpeech)it2.next();
          if(!(pos2 instanceof Noun))continue;
          Noun noun2=(Noun)pos2;
          if(CycLink.cyc.isGenlOf(noun1.getDenotat(),noun2.getDenotat()))
            return true;
          if(CycLink.cyc.isGenlOf(noun2.getDenotat(),noun1.getDenotat()))
            return true;
        }
      }
      return false;
    }
    throw new RuntimeException();
  }
  private static boolean forestsAreEqual(
          List forest1, List forest2) throws Exception{
    List difference1=new ArrayList(forest1);
    Iterator it1=forest2.iterator();
    while(it1.hasNext()){
      Node node=(Node)it1.next();
      Iterator it11=difference1.iterator();
      while(it11.hasNext()){
        Node node1=(Node)it11.next();
        if(treeEquals(node,node1)){
          difference1.remove(node1);
          break;
        }
      }
    }
    if(!difference1.isEmpty())return false;
    List difference2=new ArrayList(forest2);
    it1=forest1.iterator();
    while(it1.hasNext()){
      Node node=(Node)it1.next();
      Iterator it11=difference2.iterator();
      while(it11.hasNext()){
        Node node1=(Node)it11.next();
        if(treeEquals(node,node1)){
          difference2.remove(node1);
          break;
        }
      }
    }
    return difference2.isEmpty();
  }
  private static boolean treeEquals(Node node1, Node node2) throws Exception{
    if(!nodeEquals(node1,node2))return false;
    return forestsAreEqual(node1.getLinks(),node2.getLinks());
  }
//  private static List getQualifications(Node object){
//    Iterator it=object.getLinks().iterator();
//    List quals=new ArrayList(object.getLinks().size());
//    while(it.hasNext()){
//      java.lang.Object o=it.next();
//      if(o instanceof Node){
//        Node node=(Node)o;
//        if(node instanceof NameableInstantiableConcept.NamedInstance){
//          NameableInstantiableConcept.NamedInstance nn=
//                  (NameableInstantiableConcept.NamedInstance)node;
//          if(nn.getName().equals("qualification")){
//            quals.addAll(nn.getLinks());
//          }
//        }
//      }
//    }
//    return quals;
//  }
}
