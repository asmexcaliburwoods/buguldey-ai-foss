package org.east.text.parse;

import org.east.concepts.NameableInstantiableConcept;
import org.east.concepts.UIDGenerator;
import org.east.concepts.utility.Node;
import org.east.concepts.utility.Word;
import org.east.concepts.utility.NodeImpl;
import org.east.concepts.utility.Context;
import org.east.e1.ParsedWord;
import org.east.pos.PartOfSpeech;
import org.east.pos.Verb;
import org.east.thinking.*;
import org.east.gui.workbench.treesFrame.TreeFactory;
import org.east.cyc.CycLink;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class IdentifyActionsThatTakePlace{
  //Tree locations that apply:
  //"verb"->verb->...
  public static void identifyActionsThatTakePlace(Node text,SentenceContext sentenceContext,Node assertions,Node actionContexts) throws Exception{
    if(text==null)throw new RuntimeException();
    identifyActionsThatTakePlace0(text,sentenceContext,assertions,actionContexts);
  }
  private static void identifyActionsThatTakePlace0(Node node,SentenceContext sentenceContext,Node assertions,Node actionContexts) throws Exception{
    if(node instanceof NameableInstantiableConcept.NamedInstance){
      NameableInstantiableConcept.NamedInstance nn=
              (NameableInstantiableConcept.NamedInstance)node;
      String nnn=nn.getName();
      if(nnn.equals("verb")){
        Iterator it=nn.getLinks().iterator();
        while(it.hasNext()){
          Node verb=(Node)it.next();
          verb(verb,sentenceContext,assertions,actionContexts);
        }
      }
    }
    Iterator it2=node.getLinks().iterator();
    while(it2.hasNext()){
      Object o=it2.next();
      if(!(o instanceof Node))continue;
      Node node2=(Node)o;
      identifyActionsThatTakePlace0(node2,sentenceContext,assertions,actionContexts);
    }
  }
  public static class ActionContext extends Context{
    private String actionContextName;
    private UIDGenerator.UID actionContextUID;
    private String actionEntityName;
    private UIDGenerator.UID actionEntityUID;
    private Verb verb;
    private Node actants;
    private SentenceContext sentenceContext;
    private ActionContext(Verb verb,Node actants,SentenceContext sentenceContext){
      super("ActionContext","action-context");
      this.verb=verb;
      this.actants=actants;
      this.actionContextUID=UIDGenerator.getInstance().createUID();
      this.actionEntityUID=UIDGenerator.getInstance().createUID();
      this.actionContextName=CycLink.getEastProjectConstantNamePrefix()+
              verb.getDenotat()+"-ActionContext-"+actionContextUID;
      this.actionEntityName=CycLink.getEastProjectConstantNamePrefix()+
              verb.getDenotat()+"-ActionEntity-"+actionEntityUID;
      this.sentenceContext=sentenceContext;
    }
    public String getActionContextName(){
      return actionContextName;
    }
    public UIDGenerator.UID getActionContextUID(){
      return actionContextUID;
    }
    public String getActionEntityName(){
      return actionEntityName;
    }
    public UIDGenerator.UID getActionEntityUID(){
      return actionEntityUID;
    }
    public Verb getVerb(){
      return verb;
    }
    public Node getActants(){
      return actants;
    }
    public SentenceContext getSentenceContext(){
      return sentenceContext;
    }
    public DefaultMutableTreeNode toTree(TreeFactory tf){
      DefaultMutableTreeNode actionContext=super.toTree(tf);
      tf.addNamedChild(actionContext,"verb",verb);
      tf.addNamedChild(actionContext,"action-context-name",actionContextName);
      tf.addNamedChild(actionContext,"action-entity-name",actionEntityName);
      DefaultMutableTreeNode actantsNode=tf.createTreeNode("actants");
      actants.toTreeFlat(actantsNode,tf);
      tf.addChild(actionContext,actantsNode);
      tf.addChild(actionContext,sentenceContext.toTree(tf));
      return actionContext;
    }
  }
  //for example:
  //(ea-actionTakesPlaceInContext
  //  ConnectingClientToServer
  //  (ea-ActionContextFn ea-ConnectingClientToServer-ActionContext15)
  //  (ea-ActionEntityFn ea-ConnectingClientToServer-ActionEntity13)
  //  (ea-EnumeratedSetFn
  //    (ea-SubjectFn Ent9)
  //    (ea-ToFn Ent7)
  //  )
  //)
  public static class ActionTakesPlaceInContextAssertionHypothesis
          extends AssertionHypothesis{
    private ActionContext actionContext;
    public ActionTakesPlaceInContextAssertionHypothesis(
            SetOfHypotheses set,
            ActionContext actionContext){
      super(set, new NodeImpl());
      this.actionContext=actionContext;
    }
    public DefaultMutableTreeNode toTree(TreeFactory tf){
      return tf.named("action-takes-place-in-context-assertion-hypothesis",
              actionContext.toTree(tf));
    }
    public ActionContext getActionContext(){
      return actionContext;
    }
  }
  private static void verb(Node verb,SentenceContext sentenceContext,Node assertions,Node actionContexts) throws Exception{
    if(!(verb instanceof ParsedWord))throw new RuntimeException();
    ParsedWord vpw=(ParsedWord)verb;
    Word vw=vpw.getWord();
    Set wordForms=vw.getWordForms();
    {
      Iterator it=new ArrayList(wordForms).iterator();
      SetOfHypotheses set=new SetOfHypothesesImpl();
      while(it.hasNext()){
        PartOfSpeech pos=(PartOfSpeech)it.next();
        if(!(pos instanceof Verb))throw new RuntimeException();
        Verb v=(Verb)pos;
        new WordHasWordFormHypothesis(set,vpw,v);
      }
      set.pickNextAlternative();
    }
    //now, we have single meaning for this verb
    //now, we add assertions, for example:
    //(ea-actionTakesPlaceInContext
    //  ConnectingClientToServer
    //  (ea-ActionContextFn ea-ConnectingClientToServer-ActionContext15)
    //  (ea-ActionEntityFn ea-ConnectingClientToServer-ActionEntity13)
    //  (ea-EnumeratedSetFn
    //    (ea-SubjectFn Ent9)
    //    (ea-ToFn Ent7)
    //  )
    //)

    //First, we create action context
    Node actants=new NodeImpl("actants");
    enumActants(verb,actants);
    Set wordForms2=vw.getWordForms();
    if(wordForms2.size()!=1)throw new RuntimeException("wordForms.size()!=1 for "+vw);
    Verb v=(Verb)wordForms2.iterator().next();
    ActionContext actionContext=new ActionContext(v,actants,sentenceContext);
    actionContexts.getLinks().add(actionContext);
    //Next, we create assertion hypothesis
    SetOfHypotheses set=new SetOfHypothesesImpl();//will have single assertion
//    Node assertionBody=new NodeImpl();
//    assertionBody.getLinks().add(new AssertionToken(
//            CycLink.getEastProjectConstantNamePrefix()+
//            "actionTakesPlaceInContext"));
//    assertionBody.getLinks().add(new AssertionToken(v.getDenotat().toString()));
//    Node actionContextFn=new NodeImpl();
//    actionContextFn.getLinks().add(
//            CycLink.getEastProjectConstantNamePrefix()+"ActionContextFn"
//    );
//    actionContextFn.getLinks().add()
    ActionTakesPlaceInContextAssertionHypothesis ah=
            new ActionTakesPlaceInContextAssertionHypothesis(set, actionContext);
    assertions.getLinks().add(ah);
  }
  private static void enumActants(Node verb, final Node actants) throws Exception{
    EnumActantsUtil.enumActants(verb, new EnumActantsUtil.EnumActantsAction(){
      public void apply(String actantLabel, Object actantInstance){
        actants.getLinks().add(new NodeName(actantLabel,actantInstance));
      }
    });
  }
}
