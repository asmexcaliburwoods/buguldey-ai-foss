package org.east.text;

import org.east.Constants;
import org.east.concepts.Name;
import org.east.concepts.NameableInstantiableConcept;
import org.east.concepts.TextUnderstandingArc;
import org.east.concepts.utility.Node;
import org.east.concepts.utility.NodeImpl;
import org.east.concepts.utility.Sentence;
import org.east.concepts.utility.Text;
import org.east.e1.E1Language;
import org.east.e1.E1Parser;
import org.east.e1.IndexInSentence;
import org.east.e1.ParsedWord;
import org.east.gui.workbench.treesFrame.TreesFrame;
import org.east.text.parse.*;
import org.east.text.parse.rephrase.*;

import java.io.IOException;
import java.util.Iterator;

public class TextUnderstandingEngine{
//  public static void setSentenceParsedListener(SentenceParsedListener sentenceParsedListener){
//    TextUnderstandingEngine.sentenceParsedListener=sentenceParsedListener;
//  }
//  private static SentenceParsedListener sentenceParsedListener;
//  public static interface SentenceParsedListener{
//    void sentenceParsed(SentenceParsedEvent e);
//  }
//  public static class SentenceParsedEvent extends EventObject{
//    private Sentence sentence;
//    public Sentence getSentence(){
//      return sentence;
//    }
//    public Node getParsedSentence(){
//      return parsedSentence;
//    }
//    private Node parsedSentence;
//    private SentenceParsedEvent(Sentence sentence,Node parsedSentence){
//      super(TextUnderstandingEngine.class);
//      this.sentence=sentence;
//      this.parsedSentence=parsedSentence;
//    }
//  }
  public static void understandTextAndDeriveModel(TextUnderstandingArc arc) throws Exception{
    Text text=arc.getText();
    printText(text);
    interpretText(text);
    arc.save();
    //store the final result
    deriveModelFromUnderstanding(arc);
  }
  private static void deriveModelFromUnderstanding(TextUnderstandingArc arc){
    arc.getModel().setAssertionsForVerbs(arc.getText().getTextUnderstanding().getAssertionsForVerbs());
  }
  private static void printText(Text text){
    System.out.println("Text title (IGNORED): "+text.getTitle());
    System.out.println("Text content:");
    System.out.println(text.getContent());
    System.out.println("End of the text (ALL SECTIONS EXCEPT FOR THE FIRST ONE ARE IGNORED).");
  }
  private static void interpretText(Text text) throws Exception, IOException{
    TextUnderstanding tu=new TextUnderstanding();
    text.setTextUnderstanding(tu);
    Node parsedText=NameableInstantiableConcept.getInstance().
            newInstance("text");
    Node assertionsForVerbsText=new NodeImpl("assertions-for-verbs-text");
    parsedText.getLinks().add(assertionsForVerbsText);
    tu.setAssertionsForVerbs(assertionsForVerbsText);
    tu.setTextUnderstandingNode(parsedText);
    for(int i=0;i<text.getSections().size();i++){
      Object se=text.getSections().get(i);
      if(se instanceof Sentence){
        Sentence sentence=(Sentence)se;
        NameableInstantiableConcept.NamedInstance sent=
                NameableInstantiableConcept.getInstance().newInstance("sentence");
        Node sentenceParsingProcessNotes=new NodeImpl("sentence-parsing-process-notes");

        Node parsedSentence=interpretSentence(sentenceParsingProcessNotes,sentence);
        NameableInstantiableConcept.NamedInstance spelling=
                NameableInstantiableConcept.getInstance().newInstance("spelling");
        NameableInstantiableConcept.NamedInstance sentenceTree=
                NameableInstantiableConcept.getInstance().newInstance("sentence-tree");
        parsedText.getLinks().add(sent);
        sent.getLinks().add(spelling);
        sent.getLinks().add(sentenceParsingProcessNotes);
        sent.getLinks().add(sentenceTree);
        spelling.getLinks().add(
                NameableInstantiableConcept.getInstance().newInstance(se.toString()));
        if(parsedSentence!=null){
          sentenceTree.getLinks().add(parsedSentence);
          ApplyVerbFrames.applyVerbFrames(parsedSentence);
          Node assertionsForVerbs=new NodeImpl("assertions-for-verbs-sentence");
          Node actionContexts=new NodeImpl("action-contexts");//ignored
          sentenceTree.getLinks().add(assertionsForVerbs);
          IdentifyActionsThatTakePlace.identifyActionsThatTakePlace(
                  parsedSentence,
                  sentence.getSentenceContext(),
                  assertionsForVerbs,
                  actionContexts);
          assertionsForVerbsText.getLinks().addAll(assertionsForVerbs.getLinks());
        }else{
          sentenceTree.getLinks().add(new NodeImpl("Can't parse"));
        }
      }else if(se instanceof Text){
        Text section=(Text)se;
        interpretText(section);
      }else throw new RuntimeException("Structural element class handling not implemented: "+
              se.getClass().getName());
    }

    if(Constants.isLogLevelTrace())
      TreesFrame.addTree("Text - verb frames applied",parsedText,"null");
    Node entities=IdentifyEntities.identifyEntities(parsedText);
    parsedText.getLinks().add(entities);
    if(Constants.isLogLevelDebug())
      TreesFrame.addTree("Text - entities identified",parsedText,"null");
  }
  private static Node interpretSentence(Node sentenceParsingProcessNotes,Sentence sentence) throws Exception, IOException{
    E1Language e1Language=((E1Parser)Name.resolveSingleConcept("E1Parser")).getE1Language();
    IndexInSentence index=new IndexInSentence(sentence);
    sentence.setSentenceParsingProcessContext(
            new SentenceParsingProcessContext(sentenceParsingProcessNotes,sentence));
    Node parsedSentence=(Node)e1Language.match(index,sentence,sentenceParsingProcessNotes);
    sentence=sentence.getActiveForm();
    filterOutNonMatchingWordForms(parsedSentence);
    PronounResolver.resolvePronouns(sentence,parsedSentence);
    RephraseRule_sequenceOfActions.rephrase(parsedSentence);
    RephraseRule_onBehalfOfActorsOwn.rephrase(parsedSentence);
    RephraseRule_moveSubjectFromVerbLabelToVerbItself.rephrase(parsedSentence);
    if(Constants.isLogLevelTrace())
      TreesFrame.addTree("stage 001 - after initial rephrasings: "+sentence.getTitle(),parsedSentence,"Can't parse");
    RephraseRule_modalVerbs2.rephrase(parsedSentence);
    if(Constants.isLogLevelTrace())
      TreesFrame.addTree("stage 002 - after RephraseRule_modalVerbs2: "+sentence.getTitle(),
              parsedSentence,"Can't parse");
    //passive is after modal
    RephraseRule_passive.rephrase(parsedSentence);
    if(Constants.isLogLevelDebug())
      TreesFrame.addTree("stage 003 - after RephraseRule_passive: "+sentence.getTitle(),
              parsedSentence,"Can't parse");
    return parsedSentence;
  }
//  private static void fireSentenceParsed(Sentence sentence, Node parsedSentence){
//    if(sentenceParsedListener!=null)
//      sentenceParsedListener.sentenceParsed(
//              new SentenceParsedEvent(sentence,parsedSentence));
//  }
  private static void filterOutNonMatchingWordForms(Object o) throws Exception{
    if(o!=null){
      if(o instanceof ParsedWord){
        ParsedWord w=(ParsedWord)o;
        w.getWordClass().filterWordForms(w.getWord());
      }
      Node lo=(Node)o;
      Iterator it=lo.getLinks().iterator();
      while(it.hasNext()){
        Object o1=it.next();
        filterOutNonMatchingWordForms(o1);
      }
    }
  }
}
