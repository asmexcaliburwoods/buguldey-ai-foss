package org.east;

import org.east.brain.Brain;
import org.east.concepts.*;
import org.east.concepts.utility.MeaningAllocator;
import org.east.e1.E1Parser;
import org.east.implementSoftware.ImplementSoftwareGoal;
import org.east.javadoc.JavadocLearnAction;
import org.east.javadoc.PublicProtectedPrivatePackageModifier;
import org.east.reasons.popupGoalReasons.SpecifiedByEastProjectProgrammersReason;
import org.east.text.parse.ReadTextFromInputStreamAction;
import org.east.util.ExceptionUtil;

public class East extends Concept{
  private static final long serialVersionUID = -3762372032417631327L;
  private static boolean applicationTerminating;
  private East(){}
  private static void define(){
    Name.define("EastProject",
            EastProjectDialogueTextualContext.getInstance(),
            East.class,
            new MeaningAllocator(){
      public Concept allocate(){
        return new East();
      }
    });
  }
  private static void learnAllBasicStuff(){
    East.define();
    JavadocLearnAction.define();
    PublicProtectedPrivatePackageModifier.define();
    ReadTextFromInputStreamAction.define();
    VerbWordClass.define();
    DoWordClass.define();
    E1Parser.define();
    NameableInstantiableConcept.define();
    NounWordClass.define();
    AdverbWordClass.define();
  }
  public static void main(String[] args){
    try{
      System.out.println("Defining basic concepts");
      learnAllBasicStuff();
      System.out.println("Initialising natural language parser");
      E1Parser e1Parser=(E1Parser)Name.resolveSingleConcept("E1Parser");
      E1Parser.setNLParserUpdated(e1Parser.update());
      System.out.println("Loaded.");
//      Action a=(Action)Name.resolveSingleConcept("read javadoc");
//      a.perform("java.net.Socket");
      System.out.println("Starting brain");
      Brain brain=Brain.getInstance();
      brain.getWill().addDesire(new ImplementSoftwareGoal("work.txt",
              new SpecifiedByEastProjectProgrammersReason()));
      while(brain.getWill().hasDesires())Thread.sleep(50);
      setApplicationTerminating(true);
      while(!brain.isSuspended())Thread.sleep(50);
      brain.save();
//      TextUnderstandingArc arc=
//              (TextUnderstandingArc)
//              Concept.resolve(Name.define("CurrentTextUnderstandingArc",
//              EastProjectDialogueTextualContext.getInstance(),
//              TextUnderstandingArc.class,
//              new MeaningAllocator(){
//                public Concept allocate(){
//                  return new TextUnderstandingArc();
//                }
//              }).getConcept().getConceptId());
//      TextUnderstandingEngine.setSentenceParsedListener(new TextUnderstandingEngine.SentenceParsedListener(){
//        public void sentenceParsed(TextUnderstandingEngine.SentenceParsedEvent e){
//          TreesFrame.addTree(e.getSentence().toString(),
//                  e.getParsedSentence(),
//                  "Can't parse");
//        }
//      });
    }catch(Throwable tr){
      ExceptionUtil.handleException(tr);
    }
  }
  public static boolean isApplicationTerminating(){
    return applicationTerminating;
  }
  public static void setApplicationTerminating(boolean applicationTerminating){
    East.applicationTerminating=applicationTerminating;
    if(applicationTerminating)Brain.getInstance().setSuspended(true);
  }
}
