package org.east.cyc;

import org.east.concepts.utility.EastProjectCycConstant;
import org.east.concepts.utility.CycConcept;
import org.east.util.ExceptionUtil;
import org.east.pos.*;
import org.opencyc.api.CycAccess;
import org.opencyc.api.CycApiException;
import org.opencyc.api.CycObjectFactory;
import org.opencyc.api.CycConnection;
import org.opencyc.cycobject.*;

import java.io.*;
import java.net.UnknownHostException;
import java.util.*;

public final class CycLink {
  public static String getEastProjectPredicateNamePrefix(){
    return getEastProjectConstantNamePrefix();
  }
  public static String getEastProjectConstantNamePrefix(){
    return "ea-";
  }
  public static class CycAccess2 extends CycAccess{
    private CycAccess2() throws CycApiException, IOException{
      super(CycConnection.DEFAULT_HOSTNAME,
//             4600,
             CycConnection.DEFAULT_BASE_PORT,
             CycConnection.DEFAULT_COMMUNICATION_MODE,
             CycAccess.DEFAULT_CONNECTION);
    }
    public Object[] converse(Object o) throws CycApiException, IOException{
      return super.converse(o);
    }
    public boolean converseBoolean2(String query) throws CycApiException, IOException{
      Object[] objects=converse(query);
      if(!(objects[0].equals(Boolean.TRUE)))
        throw new CycApiException(String.valueOf(objects[1]));
      return !objects[1].equals(CycObjectFactory.nil);
    }
  };
  public static boolean queryPredicate(String query) throws CycApiException, IOException{
    return cyc.converseBoolean2("(cyc-query '"+query+" #$EverythingPSC)");
  }
  private static final String EAST_MT_FILE_NAME="EastMt.txt";
  private CycLink(){}
  public final static CycAccess2 cyc;
  static{
    try{
      cyc=new CycAccess2();
//      cyc.traceOnDetailed();
    } catch(Exception e){
      throw new RuntimeException(e);
    }
  }
  public static CycFort eastMt;
  static{
    try{
      eastMt=cyc.find("EastMt");
    } catch(Exception e){
      throw new RuntimeException(e);
    }
  }
  private static CycFort everythingPSC;
  static{
    try{
      everythingPSC=cyc.getKnownConstantByName("EverythingPSC");
    } catch(Exception e){
      throw new RuntimeException(e);
    }
  }
  private static CycFort multiwordHint;
  static{
    try{
      multiwordHint=cyc.find("multiwordHint");
    } catch(Exception e){
      throw new RuntimeException(e);
    }
  }
  public static CycList ask(String query,String var) throws CycApiException, IOException{
    CycList result=cyc.askWithVariable(cyc.makeCycList(query),new CycVariable(var),everythingPSC);
    if(result.isEmpty())return null;
    return result;
  }
  private static CycList ask(String query,String[] vars) throws CycApiException, IOException{
    ArrayList varsList=new ArrayList(vars.length);
    for(int i=0;i<vars.length;i++){
      String var=vars[i];
      varsList.add(new CycVariable(var));
    }
    CycList result=cyc.askWithVariables(cyc.makeCycList(query),varsList,everythingPSC);
    if(result.isEmpty())return null;
    return result;
  }
  private static CycList mkList(CycList l){
    if(l==null)return new CycList();
    return l;
  }
  //(singular Pronoun-TheWord "pronoun")
  //    CycList result=cyc.converseList("(cyc-queryPredicate '"+queryPredicate.stringApiValue()+" #$EverythingPSC)");
  public static CycConcept resolveSingleConcept(String word) throws CycApiException, IOException{
    CycConcept[] cycConcepts=resolveConcepts(word, true);
    return cycConcepts==null?null:cycConcepts[0];
  }
  public static CycConcept[] resolveMultipleConcepts(String word) throws CycApiException, IOException{
    return resolveConcepts(word, false);
  }
  private static Map stringWord2setOfWordForms=new HashMap();
  /** @return List of PartOfSpeech */
  public static synchronized Set<PartOfSpeech> getWordForms(String spelling)throws Exception{
    Set wordForms=(Set)stringWord2setOfWordForms.get(spelling);
    if(wordForms==null){
      wordForms=new HashSet(1);
      stringWord2setOfWordForms.put(spelling,wordForms);
      addWordFormsSingular(wordForms,spelling,false);
      addVerbWordFormsVerbForm("#$ea-verbForm",Verb.UNSPECIFIED,spelling,wordForms);
      String low=spelling.toLowerCase();
      if(low.endsWith("es")){
        String s=spelling.substring(0, spelling.length()-2);
        //philosophies
        if(s.endsWith("i"))
          addWordFormsSingular(wordForms,s.substring(0,s.length()-1)+"y",true);
        else
          addWordFormsSingular(wordForms,s,true);
      }
      if(low.endsWith("s")){
        String s=spelling.substring(0, spelling.length()-1);
        addWordFormsSingular(wordForms,s,true);
        addWordFormsInfinitive(wordForms,s,Verb.DOES);
      }
      if(low.endsWith("ed")){
        String s=spelling.substring(0, spelling.length()-2);
        //implemented
        addWordFormsInfinitive(wordForms,s,Verb.DID);
        //saved
        addWordFormsInfinitive(wordForms,s+"e",Verb.DID);
      }
      addWordFormsPlural(wordForms,spelling);
      addWordFormsInfinitive(wordForms,spelling,Verb.INFINITIVE);
      addPronounWordForms("#$ea-pronoun",spelling,wordForms);
      addAdverbWordForms("#$ea-regularAdverb",spelling,wordForms);
    }
    return wordForms;
  }
  private static void addWordFormsPlural(Set wordForms, String spelling) throws Exception{
    addNounWordForms("#$ea-plural",spelling,wordForms,true);
  }
  private static void addWordFormsSingular(Set wordForms, String spelling,boolean plural)throws Exception{
    addNounWordForms("#$ea-singular",spelling,wordForms,plural);
  }
  private static void addWordFormsInfinitive(Set wordForms, String spelling,int verbKind)throws Exception{
    addVerbWordForms("#$ea-infinitive",verbKind,spelling,wordForms);
  }
  private static void addVerbWordForms(String predicate,int verbKind,String spelling,Set wordForms) throws Exception{
    CycList theWordList=ask("("+predicate+" ?THEWORD \""+spelling+"\")","THEWORD");
    if(theWordList!=null){
      Iterator it=theWordList.iterator();
      while(it.hasNext()){
        CycFort theWord=(CycFort)it.next();
        CycList denotationList=ask("(#$ea-denotation "+theWord.cyclify()+
                " #$Verb ?Number ?Denot)","Denot");
        if(denotationList!=null){
          Iterator it2=denotationList.iterator();
          while(it2.hasNext()){
            CycFort denot=(CycFort)it2.next();
            wordForms.add(new Verb(denot,verbKind));
          }
        }
      }
    }
  }
  private static void addVerbWordFormsVerbForm(String predicate,int verbKind,String spelling,Set wordForms) throws Exception{
    CycList theWordList=ask("("+predicate+" ?THEWORD ?FORM \""+spelling+"\")","THEWORD");
    if(theWordList!=null){
      Iterator it=theWordList.iterator();
      while(it.hasNext()){
        CycFort theWord=(CycFort)it.next();
        CycList denotationList=ask("(#$ea-denotation "+theWord.cyclify()+
                " #$Verb ?Number ?Denot)","Denot");
        if(denotationList!=null){
          Iterator it2=denotationList.iterator();
          while(it2.hasNext()){
            CycFort denot=(CycFort)it2.next();
            wordForms.add(new Verb(denot,verbKind));
          }
        }
      }
    }
  }
  private static void addNounWordForms(String predicate,String spelling,Set wordForms,boolean plural) throws Exception{
    CycList<?> theWordList=ask("("+predicate+" ?THEWORD \""+spelling+"\")","THEWORD");
    if(theWordList!=null){
      Iterator it=theWordList.iterator();
      while(it.hasNext()){
        CycFort theWord=(CycFort)it.next();
        CycList denotationList=ask("(#$and (#$ea-denotation "+theWord.cyclify()+
                " ?POS ?Number ?Denot) (#$genls ?POS #$Noun))","Denot");
        if(denotationList!=null){
          Iterator it2=denotationList.iterator();
          while(it2.hasNext()){
            CycFort denot=(CycFort)it2.next();
            wordForms.add(new Noun(denot,plural));
          }
        }
      }
    }
  }
  private static void addPronounWordForms(String predicate,String spelling,Set wordForms) throws Exception{
    CycList theWordList=ask("("+predicate+" ?THEWORD \""+spelling+"\")","THEWORD");
    if(theWordList!=null){
      Iterator it=theWordList.iterator();
      while(it.hasNext()){
        CycFort theWord=(CycFort)it.next();
        if(theWord instanceof CycNart){
          CycNart nart=(CycNart)theWord;
          if(nart.getFunctor().cyclify().equals("#$PronounFn")){
            List args=nart.getArguments();
            CycFort person=(CycFort)args.get(0);
            CycFort plural=(CycFort)args.get(1);
            CycFort gender=(CycFort)args.get(2);
            CycFort type=(CycFort)args.get(3);
            int personInt=3;
            if(!person.cyclify().equals("#$ThirdPerson-NLAttr"))
              throw new RuntimeException();
            boolean pluralBool=plural.cyclify().equals("#$Plural-NLAttr");
            int genderInt=Gender.NEUTRAL;
            String genderStr=gender.cyclify();
            if(!genderStr.equals("#$Ungendered-NLAttr")&&
                    !genderStr.equals("#$Neuter-NLAttr"))
              throw new RuntimeException();
            int pronounType;
            String typeStr=type.cyclify();
            if(typeStr.equals("#$ObjectPronoun"))pronounType=Pronoun.TYPE_OBJECT_PRONOUN;
            else if(typeStr.equals("#$PossessivePronoun-Pre"))pronounType=Pronoun.TYPE_POSSESSIVE_PRONOUN_PRE;
            else if(typeStr.equals("#$PossessivePronoun-Post"))pronounType=Pronoun.TYPE_POSSESSIVE_PRONOUN_POST;
            else
              throw new RuntimeException();
            wordForms.add(new Pronoun(nart,spelling,personInt,pluralBool,genderInt,pronounType));
          }else throw new RuntimeException();
        }else throw new RuntimeException();
      }
    }
  }
  private static void addAdverbWordForms(String predicate,String spelling,Set wordForms) throws Exception{
    CycList theWordList=ask("("+predicate+" ?THEWORD \""+spelling+"\")","THEWORD");
    if(theWordList!=null){
      Iterator it=theWordList.iterator();
      while(it.hasNext()){
        CycFort theWord=(CycFort)it.next();
        CycList denotationList=ask("(#$ea-denotation "+theWord.cyclify()+
                " #$Adverb ?Number ?Denot)","Denot");
        boolean breakit=false;
        if(denotationList==null){
          denotationList=theWordList;
          breakit=true;
        }
        Iterator it2=denotationList.iterator();
        while(it2.hasNext()){
          CycFort denot=(CycFort)it2.next();
          wordForms.add(new Adverb(denot));
        }
        if(breakit)return;
      }
    }
  }
  private static CycConcept[] resolveConcepts(String word,boolean mustBeSingle) throws CycApiException, IOException{
    if(word.startsWith("#$")){
      CycConstant cc=cyc.find(word);
      if(cc==null)return null;
      return new CycConcept[]{new EastProjectCycConstant(cc)};
    }
    Set results=new HashSet();
    results.addAll(mkList(ask("(#$termStrings ?CONCEPT \""+word+"\") ","CONCEPT")));
//    CycList result=ask("(#$wordFor ?WORDCONCEPT \""+word+"\")","WORDCONCEPT");
    CycList theWordList=ask("(#$singular ?THEWORD \""+word+"\")","THEWORD");
    if(theWordList!=null){
      if(theWordList.size()>1)
        throw new RuntimeException("more than one xxx-TheWord for spelling "+word+". Result: "+(theWordList==null?null:theWordList.cyclify()));
      CycConstant theWord=(CycConstant)theWordList.get(0);
      //(denotation Motorcar-TheWord CountNoun 0 Automobile)
      CycList denotationList=ask("(#$denotation "+theWord.cyclify()+
              " ?CountNoun ?Number ?Denot))","Denot");
      if(denotationList!=null){
        if(denotationList.size()>1)
        results.addAll(mkList(denotationList));
      }
    }
    CycList denotationList=ask("(#$ea-denotes ?D \""+word+"\")","D");
    results.addAll(mkList(denotationList));
    //denotationList=ask("(#$ea-verbStrings ?D \""+word+"\")","D");
    //results.addAll(mkList(denotationList));
//    }
//    if(result==null){
//      CycList theWordList=ask("(#$plural ?THEWORD \""+word+"\")","THEWORD");
//      if(theWordList!=null){
//        if(theWordList.size()>1)
//          throw new RuntimeException("more than one xxx-TheWord for spelling "+word+". Result: "+(theWordList==null?null:theWordList.cyclify()));
//        CycConstant theWord=(CycConstant)theWordList.get(0);
//        //(denotation Motorcar-TheWord CountNoun 0 Automobile)
//        CycList denotationList=ask("(#$denotation "+theWord.cyclify()+
//                " ?CountNoun ?Number ?Denot))","Denot");
//        if(denotationList!=null){
//          if(denotationList.size()>1)
//            throw new RuntimeException("more than one denotation for word "+word+". Result: "+(denotationList==null?null:denotationList.cyclify()));
////          CycConstant denotation=(CycConstant)denotationList.get(0);
//          result=denotationList;
//        }
//      }
//    }
    if(results.isEmpty())
      return null;
    if(mustBeSingle&&results.size()!=1)
      throw new RuntimeException("more than one meaning for a word "+word+": "+results);
//    System.out.println(result.get(0).getClass()+" "+result.cyclify());
    CycFort[] cycConstants=(CycFort[])results.toArray(new CycFort[0]);
    CycConcept[] cycConcepts=new CycConcept[cycConstants.length];
    for(int i=0;i<cycConcepts.length;i++){
      cycConcepts[i]=new EastProjectCycConstant(cycConstants[i]);
    }
    return cycConcepts;
//    java.lang.ArrayStoreException
  }
  public static String[] getMultiwordSpellings(String termHint) throws CycApiException, IOException{
    CycList query = cyc.makeCycList("(#$multiwordHint \""+termHint+"\" ?TERM)");
    CycVariable var=new CycVariable("TERM");
    CycList result=cyc.askWithVariable(query,var,eastMt);
    if(result.isEmpty())return null;
    return (String[])result.toArray(new String[0]);
  }
  public static CycFort getMultiwordHintConcept(){
    return multiwordHint;
  }
  public static CycList getWordHints(String word) throws CycApiException, IOException{
    CycList query = cyc.makeCycList("(#$wordHint \""+word+"\" ?WH)");
    CycVariable var=new CycVariable("WH");
    CycList result=cyc.askWithVariable(query,var,eastMt);
    if(result.isEmpty())return null;
    return result;
  }
  public static boolean isa(org.opencyc.cycobject.CycFort arg, org.opencyc.cycobject.CycFort collection) throws CycApiException, IOException{
    return cyc.isa(arg,collection);
  }
  public static CycList getAllAssertionsInMt(CycObject mt)
                               throws IOException, UnknownHostException, CycApiException {
    return cyc.converseList("(gather-mt-index "+mt.cyclify()+")");
  }
//(#$isa #$AutocoderProject #$Cyc-BasedProject)
//(#$cyclistPrimaryProject #$AutocoderAdministrator #$AutocoderProject)
//(#$isa #$AutocoderAdministrator #$HumanCyclist)
//(#$isa #$Autocoder1_0 #$IndexedInformationSource)
//(#$initialismString #$TransmissionControlProtocol "TCP")
//(#$multiwordHint "echo" "echo_server")
//(#$wordHint "echo" #$multiwordHint)
//(#$isa #$EchoServer-MWW #$MultiWordWord)
//(#$singular #$EchoServer-MWW "echo server")
//(#$denotation #$EchoServer-MWW #$CountNoun 1 #$EchoServer)
//(#$isa #$Protocol-TheWord #$SimpleWord)
//(#$baseForm #$Protocol-TheWord "protocol")
//(#$denotation #$Protocol-TheWord #$CountNoun 1 #$Protocol)
//(#$singular #$Protocol-TheWord "protocol")
//(#$isa #$Protocol-TheWord #$EnglishWord)
//(#$isa #$EastMt #$EnglishLexicalMicrotheory)
  public static void main(String[] args){
    try{
      dumpKB();
//      dumpExtentOfRegAdverb();
//      assertLexTxt();
//      dumpLexicalWords();
//      assertKB();
//      assertLexTxt();
//      reassertSingularAsAcSingular();
    }catch(Throwable e){
      ExceptionUtil.handleException(e);
    }
  }
  private static void reassertSingularAsAcSingular() throws Exception{
    CycFort autocoderLexicalMt=cyc.findOrCreate("AutocoderLexicalMt");
    if(autocoderLexicalMt==null)
      throw new Exception("#$AutocoderLexicalMt is not defined, define it first.");
    cyc.setCyclist("AutocoderAdministrator");
    CycList list=ask("(#$denotation ?A ?B ?C ?D)",new String[]{"A","B","C","D"});
    Iterator it=list.iterator();
    int i=0;
    while(it.hasNext()){
      ++i;
      CycList list2=(CycList)it.next();
      System.out.println(i+" of "+list.size()+": "+list2.cyclify());
      CycFort a=(CycFort)list2.get(0);
      CycFort b=(CycFort)list2.get(1);
      Integer c=(Integer)list2.get(2);
      Object d=list2.get(3);
      cyc.assertWithTranscript("(#$ea-denotation "+a.cyclify()+" "+b.cyclify()+" "+c+" "+(d instanceof CycFort?((CycFort)d).cyclify():d.toString())+")",autocoderLexicalMt);
    }
  }
  private static void assertKB()throws Exception{
    if(eastMt!=null)
      throw new Exception("#$EastMt is already defined, kill it first.");
    System.out.println("Creating a microtheory...");
    eastMt=cyc.createNewPermanent("EastMt");
    cyc.findOrCreate("AutocoderAdministrator");
    cyc.assertIsa("AutocoderAdministrator","HumanCyclist");
    cyc.setCyclist("AutocoderAdministrator");
    cyc.assertIsa("EastMt","Microtheory");
    System.out.println("Copying assertions...");
//    ObjectInputStream is=new ObjectInputStream(new BufferedInputStream(
//            new FileInputStream(EAST_MT_FILE_NAME),64*1024));
//    try{
//      List assertions=(List)is.readObject();
//      Iterator it=assertions.iterator();
//      int i=0;
//      while(it.hasNext()){
//        CycList formula=(CycList)it.next();
//        StringBuffer sb=new StringBuffer("(");
//        Iterator it2=formula.iterator();
//        while(it2.hasNext()){
//          Object o=it2.next();
//          if(sb.length()>0)sb.append(" ");
//          if(o instanceof CycConstant){
//            CycConstant cc=(CycConstant)o;
//            sb.append(cc.getName());
//            if(cc.getName()==null)throw new RuntimeException("constant name is not known");
//          }else sb.append(o);
//        }
//        sb.append(")");
//        System.out.println((++i)+" of "+assertions.size()+": "+sb);
//        cyc.assertWithTranscript(formula,eastMt);
//      }
//    }finally{
//      is.close();
//    }
    BufferedReader r=new BufferedReader(new FileReader(EAST_MT_FILE_NAME),64*1024);
    List assertions=new LinkedList();
    try{
      while(true){
        String line=r.readLine();
        if(line==null)break;
        assertions.add(line);
      }
    }finally{
      r.close();
    }
    Iterator it=assertions.iterator();
    while(it.hasNext()){
      String line=(String)it.next();
      System.out.println(line);
      StringTokenizer st=new StringTokenizer(line," ");
//      CycList c=new CycList();
      while(st.hasMoreTokens()){
        String token=st.nextToken();
        String constant=token;
        if(token.startsWith("("))constant=constant.substring(1);
        if(token.endsWith(")"))
          constant=constant.substring(0,constant.length()-1);
//        if(!constant.startsWith("#$"))
//          constant=constant.replaceAll("_"," ");
        if(constant.startsWith("#$"))cyc.findOrCreate(constant);
      }
      cyc.assertWithTranscript(line,eastMt);
    }
  }
  private static void assertLexTxt()throws Exception{
    System.out.println("Creating a microtheory...");
    CycFort autocoderLexicalMt=cyc.findOrCreate("AutocoderLexicalMt");
    cyc.findOrCreate("AutocoderAdministrator");
    cyc.assertIsa("AutocoderAdministrator","HumanCyclist");
    cyc.setCyclist("AutocoderAdministrator");
    cyc.assertIsa("AutocoderLexicalMt","Microtheory");

    System.out.println("Copying words...");
    BufferedReader r=new BufferedReader(new FileReader("lex.txt"),64*1024);
    List assertions=new ArrayList(20000);
    try{
      while(true){
        String line=r.readLine();
        if(line==null)break;
        if(line.indexOf("#$ProposedPublicConstant-NL")>=0)continue;
        assertions.add(line);
      }
    }finally{
      r.close();
    }
    Iterator it=assertions.iterator();
    boolean skip=false;
    int i=0;
    while(it.hasNext()){
      String line=(String)it.next();
      i++;
      if(!skip||((i%100)==0))System.out.println(i+" of "+assertions.size()+": "+line);
      StringTokenizer st=new StringTokenizer(line," ");
//      CycList c=new CycList();
      while(st.hasMoreTokens()){
        String token=st.nextToken();
        String constant=token;
        while(constant.startsWith("("))constant=constant.substring(1);
        while(constant.endsWith(")"))constant=constant.substring(0,constant.length()-1);
//        if(!constant.startsWith("#$"))
//          constant=constant.replaceAll("_"," ");
        if(!skip&&constant.startsWith("#$"))cyc.findOrCreate(constant);
      }
      if(!skip)cyc.assertWithTranscript(line,autocoderLexicalMt);
//      if(line.indexOf("(#$singular (#$WordWithPrefixFn #$Non_DeAdjectival-ThePrefix #$Standard-TheWord) \"nonstandard\")")
//              >=0)
//        skip=false;
    }
  }
  private static void dumpKB() throws IOException, CycApiException{
    Writer w=new BufferedWriter(new FileWriter(EAST_MT_FILE_NAME),64*1024);
    try{
      //(genlMt AutocoderLexicalMt UniversalVocabularyMt)
      {
        System.out.println("Dumping genlMt clauses...");
        String mt="#$EastMt";
        CycList preds=ask("(#$genlMt "+mt+" ?MT)","MT");
        Iterator it=preds.iterator();
        while(it.hasNext()){
          CycConstant constant=(CycConstant)it.next();
          String str="(#$genlMt "+mt+" "+constant.cyclify()+")";
          System.out.println(str);
          w.write(str+"\r\n");
        }
      }
      {
        String mt="#$AutocoderLexicalMt";
        CycList preds=ask("(#$genlMt "+mt+" ?MT)","MT");
        Iterator it=preds.iterator();
        while(it.hasNext()){
          CycConstant constant=(CycConstant)it.next();
          String str="(#$genlMt "+mt+" "+constant.cyclify()+")";
          System.out.println(str);
          w.write(str+"\r\n");
        }
      }
      {
        System.out.println("Dumping relations...");
        CycList preds=ask("(#$and (#$isa ?P #$Relation) (#$myCreator ?P #$AutocoderAdministrator))","P");
        Iterator it=preds.iterator();
        while(it.hasNext()){
          CycConstant constant=(CycConstant)it.next();
          CycList collections=cyc.getMinIsas(constant);
  //        if(collections.size()!=1)
  //          throw new RuntimeException("more than one collection for "+constant.cyclify()+": "+collections.cyclify());
          Iterator itc=collections.iterator();
          while(itc.hasNext()){
            CycConstant collection=(CycConstant)itc.next();
            String str="(#$isa "+constant.cyclify()+" "+collection.cyclify()+")";
            System.out.println(str);
            w.write(str+"\r\n");
          }
        }
      }
      System.out.println("Dumping collections...");
      CycList preds=ask("(#$and (#$isa ?P #$Collection) (#$myCreator ?P #$AutocoderAdministrator))","P");
      Iterator it=preds.iterator();
      while(it.hasNext()){
        CycConstant constant=(CycConstant)it.next();
        CycList collections=cyc.getMinIsas(constant);
//        if(collections.size()!=1)
//          throw new RuntimeException("more than one collection for "+constant.cyclify()+": "+collections.cyclify());
        Iterator itc=collections.iterator();
        while(itc.hasNext()){
          CycConstant collection=(CycConstant)itc.next();
          String str="(#$isa "+constant.cyclify()+" "+collection.cyclify()+")";
          System.out.println(str);
          w.write(str+"\r\n");
        }
      }
      System.out.println("Dumping assertions...");
      CycList l=getAllAssertionsInMt(eastMt);
      Iterator it2=l.iterator();
      int i=0;
      while(it2.hasNext()){
        CycAssertion a=(CycAssertion)it2.next();
        String aa=a.getFormula().cyclify();
        w.write(aa+"\r\n");//fetches the formula from the kb
        System.out.println((++i)+" of "+l.size()+": "+aa);
      }
      System.out.println("Done.");
    }finally{
      w.close();
    }
  }
  private static void dumpExtentOfRegAdverb() throws IOException, CycApiException{
    Writer w=new BufferedWriter(new FileWriter("lex.txt"),64*1024);
    try{
      dumpExtentOf("#$regularAdverb", w);
      System.out.println("Done.");
    }finally{
      w.close();
    }
  }
  private static void dumpLexicalWords() throws IOException, CycApiException{
    Writer w=new BufferedWriter(new FileWriter("lex.txt"),64*1024);
    try{
      {
        System.out.println("Dumping the extent of #$denotation...");
        CycList l=mkList(ask("(#$denotation ?A1 ?A2 ?A3 ?A4)",new String[]{"A1","A2","A3","A4"}));
        Iterator it2=l.iterator();
        int i=0;
        while(it2.hasNext()){
            CycList a=(CycList)it2.next();
          try{
            CycFort a1=(CycFort)a.get(0);
            CycFort a2=(CycFort)a.get(1);
            Integer a3=(Integer)a.get(2);
            Object a4=a.get(3);
            w.write("(#$denotation "+a1.cyclify()+" "+a2.cyclify()+" "+a3+" "+(a4 instanceof CycFort?((CycFort)a4).cyclify():a4.toString())+")\r\n");
            System.out.println((++i)+" of "+l.size()+": "+a4);
          }catch(ClassCastException e){
            e.printStackTrace();
          }
        }
      }
      dumpExtentOf("#$singular", w);
      dumpExtentOf("#$plural", w);
      dumpExtentOf("#$infinitive", w);
      System.out.println("Done.");
    }finally{
      w.close();
    }
  }
  private static void dumpExtentOf(String binaryLexicalPredicate,Writer w) throws CycApiException, IOException{
    System.out.println("Dumping the extent of "+binaryLexicalPredicate+"...");
    CycList l=mkList(ask("("+binaryLexicalPredicate+" ?A1 ?A2)",new String[]{"A1","A2"}));
    Iterator it2=l.iterator();
    int i=0;
    while(it2.hasNext()){
      CycList a=(CycList)it2.next();
      CycFort a1=(CycFort)a.get(0);
      String a2=(String)a.get(1);
      if(a2.indexOf("\"")>=0)
        throw new RuntimeException("a double quote in the string: "+a2);
      w.write("("+binaryLexicalPredicate+" "+a1.cyclify()+" \""+a2+"\")\r\n");
      System.out.println((++i)+" of "+l.size()+": "+a1);
    }
  }
  public static boolean genls(CycConcept c1, CycConcept c2) throws CycApiException, IOException{
    return cyc.isGenlOf(c2.getCycFort(),c1.getCycFort(),everythingPSC);
  }
}
