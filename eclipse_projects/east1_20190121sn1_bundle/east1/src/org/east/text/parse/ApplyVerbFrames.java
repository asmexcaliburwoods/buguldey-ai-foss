package org.east.text.parse;

import org.east.concepts.NameableInstantiableConcept;
import org.east.concepts.utility.Node;
import org.east.concepts.utility.Word;
import org.east.cyc.CycLink;
import org.east.e1.ParsedWord;
import org.east.gui.workbench.treesFrame.DisplayableNode;
import org.east.gui.workbench.treesFrame.TreeFactory;
import org.east.pos.Noun;
import org.east.pos.PartOfSpeech;
import org.east.pos.Verb;
import org.east.thinking.HypothesisImpl;
import org.east.thinking.SetOfHypotheses;
import org.east.thinking.SetOfHypothesesImpl;
import org.opencyc.cycobject.CycConstant;
import org.opencyc.cycobject.CycList;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

public class ApplyVerbFrames{
  //Tree locations that apply:
  //"verb"->verb->...
  public static void applyVerbFrames(Node text) throws Exception{
    if(text!=null){
      applyVerbFrames0(text);
      AssignParents.assignParents(text);
    }
  }
  private static void applyVerbFrames0(Node node) throws Exception{
    if(node instanceof NameableInstantiableConcept.NamedInstance){
      NameableInstantiableConcept.NamedInstance nn=
              (NameableInstantiableConcept.NamedInstance)node;
      String nnn=nn.getName();
      if(nnn.equals("verb")){
        Iterator it=nn.getLinks().iterator();
        while(it.hasNext()){
          Node verb=(Node)it.next();
          verb(verb);
        }
      }
    }
    Iterator it2=node.getLinks().iterator();
    while(it2.hasNext()){
      java.lang.Object o=it2.next();
      if(!(o instanceof Node))continue;
      Node node2=(Node)o;
      applyVerbFrames0(node2);
    }
  }
  private static void verb(Node verb) throws Exception{
    if(!(verb instanceof ParsedWord))throw new RuntimeException();
    ParsedWord vpw=(ParsedWord)verb;
    Word vw=vpw.getWord();
    Iterator it=vw.getWordForms().iterator();
    SetOfHypotheses set=new SetOfHypothesesImpl();
    while(it.hasNext()){
      PartOfSpeech pos=(PartOfSpeech)it.next();
      if(!(pos instanceof Verb))throw new RuntimeException();
      Verb v=(Verb)pos;
      verbMeaning(set,verb,v);
    }
    set.pickNextAlternative();
  }
  private static void verbMeaning(SetOfHypotheses set,Node verb,Verb v) throws Exception{
//(ac-verbFrame Writing
//   (ac-verbFrameFn
//      (ac-SubjectFn Agent-Generic)
//      (ac-ObjectFn ConceptualWork)))
    String vc=v.getDenotat().cyclify();
    CycList frames=CycLink.ask("(#$ea-verbFrame "+vc+" ?FRAME)","FRAME");
    if(frames==null){
      System.out.println("Warning: verb "+vc+" does not have a verb frame");
      return;
    }
    Iterator it=frames.iterator();
    while(it.hasNext()){
      CycList frame=(CycList)it.next();
      spawnHypothesis(set,verb,v,frame);
    }
  }
  public abstract static class VerbFrameHypothesis
          extends HypothesisImpl
          implements DisplayableNode{
    private CycList verbFrame;
    private static final String VERB_FRAME_HYPOTHESIS="verb-frame-hypothesis";
    private VerbFrameHypothesis(SetOfHypotheses set,CycList verbFrame){
      super(set);
      this.verbFrame=verbFrame;
    }
    public String hypothesisToString(){
      return VERB_FRAME_HYPOTHESIS+": "+verbFrameToString();
    }
    private String verbFrameToString(){
      return verbFrame.cyclify();
    }
    public DefaultMutableTreeNode toTree(TreeFactory tf){
      DefaultMutableTreeNode h=tf.createTreeNode(VERB_FRAME_HYPOTHESIS);
      tf.addChild(h,verbFrameToString());
      return h;
    }
    public CycList getVerbFrame(){
      return verbFrame;
    }
  }
  public static class GuessedByHypothesis extends NameableInstantiableConcept.NamedInstance{
    private VerbFrameHypothesis hypothesis;
    private static final String GUESSED_BY_HYPOTHESIS="guessed-by-hypothesis";
    private GuessedByHypothesis(String name,VerbFrameHypothesis h){
      super(name);
      this.hypothesis=h;
    }
    public VerbFrameHypothesis getHypothesis(){
      return hypothesis;
    }
    public DefaultMutableTreeNode toTree(TreeFactory tf){
      DefaultMutableTreeNode guessed=tf.createTreeNode(GUESSED_BY_HYPOTHESIS);
      tf.addChild(guessed,hypothesis.toTree(tf));
      DefaultMutableTreeNode tree=super.toTree(tf);
      tf.addChild(tree,guessed);
      return tree;
    }
  }
  private static void spawnHypothesis(SetOfHypotheses set,
                                      final Node verb,
                                      final Verb v,
                                      final CycList frame){
    new VerbFrameHypothesis(set,frame){
      private List removedWordForms_verb;
      private ParsedWord to;
      private List removedWordForms_to;
      private NameableInstantiableConcept.NamedInstance toGuessed;
      private ParsedWord object;
      private List removedWordForms_object;
      private NameableInstantiableConcept.NamedInstance objectGuessed;
      private ParsedWord subject;
      private List removedWordForms_subject;
      private NameableInstantiableConcept.NamedInstance subjectGuessed;
      private ParsedWord from;
      private List removedWordForms_from;
      private NameableInstantiableConcept.NamedInstance fromGuessed;
      protected void assertImpl() throws Exception{
        verb.getLinks().add(this);
        ParsedWord pw=(ParsedWord)verb;
        Iterator it=new HashSet(pw.getWord().getWordForms()).iterator();
        while(it.hasNext()){
          PartOfSpeech pos=(PartOfSpeech)it.next();
          if(pos==v)continue;
          if(removedWordForms_verb==null)removedWordForms_verb=new LinkedList();
          removedWordForms_verb.add(pos);
          pw.getWord().removeWordForm(pos, "hypothesis asserted: "+this);
        }

        it=frame.iterator();
        while(it.hasNext()){
          Object o=it.next();
          if(o instanceof CycConstant){
            CycConstant c=(CycConstant)o;
            String cc=c.cyclify();
            if(cc.equals("#$ea-verbFrameFn"))continue;
            throw new RuntimeException(cc);
          }
          CycList subframe=(CycList)o;
          String subframeName=((CycConstant)subframe.get(0)).cyclify();
          CycConstant subframeConstant=(CycConstant)subframe.get(1);
               if(subframeName.equals("#$ea-SubjectFn"))assertSubject(verb,subframeConstant);
          else if(subframeName.equals("#$ea-ObjectFn"))assertObject(verb,subframeConstant);
          else if(subframeName.equals("#$ea-ToFn"))assertTo(verb,subframeConstant);
          else if(subframeName.equals("#$ea-FromFn"))assertFrom(verb,subframeConstant);
          else throw new RuntimeException(subframeName);

          //may become invalid in assertXxxx(verb,subframeConstant)
          if(isInvalid())return;
        }
      }
      private void assertSubject(Node verb, final CycConstant subframeConstant) throws Exception{
        boolean found=VerbSubjectUtil.applyActionToEachSubjectForAVerb(verb,new VerbSubjectUtil.SubjectAction(){
          public void apply(NameableInstantiableConcept.NamedInstance subjectLabel)
                  throws Exception{
            doAssertSubject(subjectLabel,subframeConstant);
          }
        });
        if(!found)subjectGuessed=guess("subject",verb,subframeConstant);
      }
      private NameableInstantiableConcept.NamedInstance
              guess(String role,Node verb, CycConstant subframeConstant){
        NameableInstantiableConcept.NamedInstance guessed=
                new GuessedByHypothesis(role,this);
        Noun noun=new Noun(subframeConstant);
        Set forms=new HashSet();
        forms.add(noun);
        Word w=new Word("guessed-"+subframeConstant.cyclify(),forms);
        ParsedWord pw=new ParsedWord(w,null);
        verb.getLinks().add(guessed);
        guessed.getLinks().add(pw);
        return guessed;
      }
      private void removeGuess(Node verb,
                               NameableInstantiableConcept.NamedInstance guessed){
        verb.getLinks().remove(guessed);
      }
      private void doAssertSubject(NameableInstantiableConcept.NamedInstance n, CycConstant subframeConstant) throws Exception{
        int s=n.getLinks().size();
        if(s!=1)throw new RuntimeException("subject.links must have size 1, but it is "+s);
        Object o=n.getLinks().get(0);
        if(!(o instanceof ParsedWord))return;
        ParsedWord pw=(ParsedWord)o;
        subject=pw;
        Iterator it2=new HashSet(pw.getWord().getWordForms()).iterator();
        while(it2.hasNext()){
          PartOfSpeech pos=(PartOfSpeech)it2.next();
          if(!(pos instanceof Noun))continue;
          Noun noun=(Noun)pos;
          if(CycLink.cyc.isGenlOf(subframeConstant,noun.getDenotat(),CycLink.eastMt))
            continue;
          if(CycLink.cyc.isa(noun.getDenotat(),subframeConstant,CycLink.eastMt))
            continue;
          if(removedWordForms_subject==null)
            removedWordForms_subject=new LinkedList();
          removedWordForms_subject.add(pos);
          pw.getWord().removeWordForm(pos, "hypothesis asserted: "+this);
        }
      }
      private void assertObject(Node verb, CycConstant subframeConstant) throws Exception{
        Iterator it=verb.getLinks().iterator();
        while(it.hasNext()){
          Object o=it.next();
          if(o instanceof NameableInstantiableConcept.NamedInstance){
            NameableInstantiableConcept.NamedInstance n=
                    (NameableInstantiableConcept.NamedInstance)o;
            if(n.getName().equals("object")){
              doAssertObject(n, subframeConstant);
              return;
            }
          }
        }
        objectGuessed=guess("object",verb,subframeConstant);
      }
      private void doAssertObject(NameableInstantiableConcept.NamedInstance n, CycConstant subframeConstant) throws Exception{
        int s=n.getLinks().size();
        if(s!=1)throw new RuntimeException("object.links must have size 1, but it is "+s);
        Object o=n.getLinks().get(0);
        if(!(o instanceof ParsedWord))return;
        ParsedWord pw=(ParsedWord)o;
        object=pw;
        Iterator it2=new HashSet(pw.getWord().getWordForms()).iterator();
        while(it2.hasNext()){
          PartOfSpeech pos=(PartOfSpeech)it2.next();
          if(!(pos instanceof Noun))continue;
          Noun noun=(Noun)pos;
          if(CycLink.cyc.isGenlOf(subframeConstant,noun.getDenotat(),CycLink.eastMt))
            continue;
          if(CycLink.cyc.isa(noun.getDenotat(),subframeConstant,CycLink.eastMt))
            continue;
          if(removedWordForms_object==null)
            removedWordForms_object=new LinkedList();
          removedWordForms_object.add(pos);
          pw.getWord().removeWordForm(pos, "hypothesis asserted: "+this);
        }
      }
      private void assertTo(Node verb, CycConstant subframeConstant) throws Exception{
        Iterator it=verb.getLinks().iterator();
        while(it.hasNext()){
          Object o=it.next();
          if(o instanceof NameableInstantiableConcept.NamedInstance){
            NameableInstantiableConcept.NamedInstance n=
                    (NameableInstantiableConcept.NamedInstance)o;
            if(n.getName().equals("to")){
              doAssertTo(n, subframeConstant);
              return;
            }
          }
        }
        toGuessed=guess("to",verb,subframeConstant);
      }
      private void doAssertTo(NameableInstantiableConcept.NamedInstance n, CycConstant subframeConstant) throws Exception{
        int s=n.getLinks().size();
        if(s!=1)throw new RuntimeException("to.links must have size 1, but it is "+s);
        Object o=n.getLinks().get(0);
        if(!(o instanceof ParsedWord))return;
        ParsedWord pw=(ParsedWord)o;
        to=pw;
        Iterator it2=new HashSet(pw.getWord().getWordForms()).iterator();
        while(it2.hasNext()){
          PartOfSpeech pos=(PartOfSpeech)it2.next();
          if(!(pos instanceof Noun))continue;
          Noun noun=(Noun)pos;
          if(CycLink.cyc.isGenlOf(subframeConstant,noun.getDenotat(),CycLink.eastMt))
            continue;
          if(CycLink.cyc.isa(noun.getDenotat(),subframeConstant,CycLink.eastMt))
            continue;
          if(removedWordForms_to==null)
            removedWordForms_to=new LinkedList();
          removedWordForms_to.add(pos);
          pw.getWord().removeWordForm(pos, "hypothesis asserted: "+this);
        }
      }
      private void assertFrom(Node verb, CycConstant subframeConstant) throws Exception{
        Iterator it=verb.getLinks().iterator();
        while(it.hasNext()){
          Object o=it.next();
          if(o instanceof NameableInstantiableConcept.NamedInstance){
            NameableInstantiableConcept.NamedInstance n=
                    (NameableInstantiableConcept.NamedInstance)o;
            if(n.getName().equals("from")){
              doAssertFrom(n, subframeConstant);
              return;
            }
          }
        }
        fromGuessed=guess("from",verb,subframeConstant);
      }
      private void doAssertFrom(NameableInstantiableConcept.NamedInstance n, CycConstant subframeConstant) throws Exception{
        int s=n.getLinks().size();
        if(s!=1)throw new RuntimeException("from.links must have size 1, but it is "+s);
        Object o=n.getLinks().get(0);
        if(!(o instanceof ParsedWord))return;
        ParsedWord pw=(ParsedWord)o;
        from=pw;
        Iterator it2=new HashSet(pw.getWord().getWordForms()).iterator();
        while(it2.hasNext()){
          PartOfSpeech pos=(PartOfSpeech)it2.next();
          if(!(pos instanceof Noun))continue;
          Noun noun=(Noun)pos;
          if(CycLink.cyc.isGenlOf(subframeConstant,noun.getDenotat(),CycLink.eastMt))
            continue;
          if(CycLink.cyc.isa(noun.getDenotat(),subframeConstant,CycLink.eastMt))
            continue;
          if(removedWordForms_from==null)
            removedWordForms_from=new LinkedList();
          removedWordForms_from.add(pos);
          pw.getWord().removeWordForm(pos, "hypothesis asserted: "+this);
        }
      }
      protected void retractImpl(){
        NameableInstantiableConcept.NamedInstance guessed;
        verb.getLinks().remove(this);
        {
          ParsedWord pw=(ParsedWord)verb;
          if(removedWordForms_verb!=null){
            Iterator it=removedWordForms_verb.iterator();
            while(it.hasNext()){
              PartOfSpeech pos=(PartOfSpeech)it.next();
              pw.getWord().restoreWordForm(pos, "hypothesis retracted: "+this);
            }
          }
        }
        if(removedWordForms_subject!=null){
          Iterator it=removedWordForms_subject.iterator();
          while(it.hasNext()){
            PartOfSpeech pos=(PartOfSpeech)it.next();
            subject.getWord().restoreWordForm(pos, "hypothesis retracted: "+this);
          }
        }
        guessed=subjectGuessed;if(guessed!=null)removeGuess(verb,guessed);
        if(removedWordForms_object!=null){
          Iterator it=removedWordForms_object.iterator();
          while(it.hasNext()){
            PartOfSpeech pos=(PartOfSpeech)it.next();
            object.getWord().restoreWordForm(pos, "hypothesis retracted: "+this);
          }
        }
        guessed=objectGuessed;if(guessed!=null)removeGuess(verb,guessed);
        if(removedWordForms_to!=null){
          Iterator it=removedWordForms_to.iterator();
          while(it.hasNext()){
            PartOfSpeech pos=(PartOfSpeech)it.next();
            to.getWord().restoreWordForm(pos, "hypothesis retracted: "+this);
          }
        }
        guessed=toGuessed;if(guessed!=null)removeGuess(verb,guessed);
        if(removedWordForms_from!=null){
          Iterator it=removedWordForms_from.iterator();
          while(it.hasNext()){
            PartOfSpeech pos=(PartOfSpeech)it.next();
            from.getWord().restoreWordForm(pos, "hypothesis retracted: "+this);
          }
        }
        guessed=fromGuessed;if(guessed!=null)removeGuess(verb,guessed);
      }
      public String toString(){
        return "verb frame is active: node: "+verb+", verb: "+v+", frame: "+frame.cyclify();
      }
    };
  }
}
