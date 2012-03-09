package org.east.concepts;

import org.east.concepts.utility.MeaningAllocator;

public final class JavaLanguageTextualContext extends TextualContext{
  private static final long serialVersionUID = -3534176970214679021L;
  private JavaLanguageTextualContext(){}
  public static JavaLanguageTextualContext getInstance(){
    return (JavaLanguageTextualContext)Name.define("JavaLanguageTextualContext",
            EastProjectDialogueTextualContext.getInstance(),
            JavaLanguageTextualContext.class,
            new MeaningAllocator(){
              public Concept allocate(){
                return new JavaLanguageTextualContext();
              }
            }).getConcept();
  }
}
