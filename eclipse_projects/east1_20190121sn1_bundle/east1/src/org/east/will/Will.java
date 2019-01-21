package org.east.will;

import org.east.brain.Brain;
import org.east.desires.Desire;
import org.east.util.ExceptionUtil;
import org.east.East;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.Serializable;

public class Will implements Serializable{
  private Brain brain;
  private boolean suspended=true;
  public Will(Brain brain){
    this.brain=brain;
  }
  private void spawnThread(){
    new Thread(new Runnable(){
      public void run(){
        Will.this.suspended=false;
        try{
          List iteration=new ArrayList();
//          List active=new ArrayList();
          while(true){
            if(East.isApplicationTerminating())break;
            synchronized(Will.this){
              if(desires.isEmpty())continue;
              iteration.clear();
              iteration.addAll(desires);
            }
            boolean shouldContinue=false;
            Iterator it=iteration.iterator();
            while(it.hasNext()){
              Desire desire=(Desire)it.next();
              if(desire.isFulfilled()){
                removeDesire(desire);
                continue;
              }
              if(desire.isEstimatedAsUnreachable()){
                removeDesire(desire);
                continue;
              }
              if(East.isApplicationTerminating())break;
              if(desire.isEstimatedAsReachable()&&desire.shouldNowRunStepToCompletion()){
                desire.stepToCompletion();
                if(desire.isFulfilled()){
                  removeDesire(desire);
                  continue;
                }
                if(desire.isEstimatedAsUnreachable()){
                  removeDesire(desire);
                  continue;
                }
                Thread.sleep(10);
                if(!shouldContinue)shouldContinue=desire.shouldNowRunStepToCompletion();
              }
              if(East.isApplicationTerminating())break;
            }
            synchronized(Will.this){
              if(!shouldContinue&&!East.isApplicationTerminating())
                Will.this.wait();
            }
          }
        }catch(Throwable tr){
          ExceptionUtil.handleException(tr);
        }
        Will.this.suspended=true;
      }
    },"Will").start();
  }
  private List desires=new LinkedList();
  public synchronized void addDesire(Desire desire){
    desires.add(desire);
    notifyAll();
  }
  public synchronized void removeDesire(Desire desire){
    desires.remove(desire);
  }
  public Brain getBrain(){
    return brain;
  }
  public synchronized boolean hasDesires(){
    return !desires.isEmpty();
  }
  public boolean isSuspended(){
    return suspended;
  }
  public synchronized void setSuspended(boolean suspended){
    if(suspended)notifyAll();
    else spawnThread();
  }
}
