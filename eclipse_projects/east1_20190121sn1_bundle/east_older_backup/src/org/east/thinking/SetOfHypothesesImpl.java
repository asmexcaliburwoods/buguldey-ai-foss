package org.east.thinking;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Iterator;

public class SetOfHypothesesImpl implements SetOfHypotheses{
  private List hypotheses=new LinkedList();
  public synchronized void pickNextAlternative() throws Exception{
    Iterator it=hypotheses.iterator();
    while(it.hasNext()){
      Hypothesis h=(Hypothesis)it.next();
      if(h.isInvalid())continue;
      h.doAssert();
      return;
    }
  }
  public List getHypotheses(){
    return Collections.unmodifiableList(hypotheses);
  }
  public synchronized void addHypothesis(Hypothesis h){
    hypotheses.add(h);
  }
}
