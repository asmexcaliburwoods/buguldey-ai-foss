package org.east.e1;

import org.east.concepts.utility.Word;
import org.east.e1.semaction.Scope;

public class WordUtil{
  public static void wordParsed(Word w, WordClass wordClass, Scope ctx){
    ParsedWord value=new ParsedWord(w, wordClass);
    w.setParent(value);
    ctx.createLValue(wordClass.getInstanceName(),value);
  }
}
