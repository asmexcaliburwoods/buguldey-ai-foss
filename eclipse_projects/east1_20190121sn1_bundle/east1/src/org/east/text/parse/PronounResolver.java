package org.east.text.parse;

import org.east.concepts.utility.Sentence;
import org.east.concepts.utility.Node;
import org.east.concepts.utility.Word;
import org.east.e1.ParsedWord;
import org.east.pos.PartOfSpeech;
import org.east.pos.Pronoun;
import org.east.pos.Noun;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class PronounResolver{
  public static void resolvePronouns(Sentence sentence, Node parsedSentence) throws Exception{
    if(parsedSentence==null)return;
    Set pronouns=new HashSet();
    AssignParents.assignParents(parsedSentence);
    enumPronouns(pronouns,parsedSentence);
    Iterator it=pronouns.iterator();
    List s=sentence.getWords();
    while(it.hasNext()){
      ParsedWord pw=(ParsedWord)it.next();
      Word w=pw.getWord();
      Pronoun pronoun=(Pronoun)w.getWordForms().iterator().next();
      tryFinding(w, s, pronoun, pw);
    }
  }
  private static void tryFinding(Word w, List s, Pronoun pronoun, ParsedWord pw) throws Exception{
    int i=w.getPositionInSentence()-1;
    boolean replaced=false;
    while(i>=0){
      Word test=(Word)s.get(i);
      replaced=tryReplacingWord(test,pronoun,pw);if(replaced)break;
      i--;
    }
    if(!replaced){
      i=w.getPositionInSentence()+1;
      while(i<s.size()){
        Word test=(Word)s.get(i);
        replaced=tryReplacingWord(test,pronoun,pw);if(replaced)break;
        i++;
      }
    }
    if(!replaced)System.out.println("Cannot resolve a pronoun "+w);
  }
  private static boolean tryReplacingWord(Word test,Pronoun pronoun,ParsedWord pw) throws Exception{
    Set testWordForms=test.getWordForms();
    if(!testWordForms.isEmpty()){
      Iterator it2=testWordForms.iterator();
      while(it2.hasNext()){
        PartOfSpeech pos=(PartOfSpeech)it2.next();
        if(pos instanceof Noun){
          Noun n=(Noun)pos;
          if(n.isPlural()==pronoun.isPlural()){
            replacePronounWithWord(pw,test,pronoun);
            return true;
          }
        }
      }
    }
    return false;
  }
  private static void replacePronounWithWord(
          ParsedWord pronounPW, Word w, Pronoun pronoun) throws Exception{
    //new HashSet() is to prevent ConcModifExc
    System.out.println("Resolved pronoun "+pronoun+" to the word "+w);
    Iterator it=new HashSet(w.getWordForms()).iterator();
    while(it.hasNext()){
      PartOfSpeech pos=(PartOfSpeech)it.next();
      if(!(pos instanceof Noun)){
        removeWordForm(w,pos,pronoun);
        continue;
      }
      Noun noun=(Noun)pos;
      if(noun.isPlural()!=pronoun.isPlural()){
        removeWordForm(w,pos,pronoun);
        continue;
      }
    }
    int pos=pronounPW.getParent().getLinks().indexOf(pronounPW);
    pronounPW.getParent().getLinks().remove(pos);
    pronounPW.getParent().getLinks().add(pos,w.getParent());
  }
  private static void removeWordForm(Word w, PartOfSpeech wf, Pronoun pronoun){
    w.removeWordForm(wf,"The word is a substitute for pronoun "+pronoun);
  }
  private static void enumPronouns(Set pronouns, Node lo) throws Exception{
    if(lo instanceof ParsedWord){
      ParsedWord w=(ParsedWord)lo;
      Set wordForms=w.getWord().getWordForms();
      if(wordForms.size()==1){
        PartOfSpeech pos=(PartOfSpeech)wordForms.iterator().next();
        if(pos instanceof Pronoun)pronouns.add(w);
      }
    }
    Iterator it=lo.getLinks().iterator();
    while(it.hasNext()){
      Node o=(Node)it.next();
      enumPronouns(pronouns,o);
    }
  }
}
