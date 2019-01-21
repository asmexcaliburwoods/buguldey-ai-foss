package org.east.pos;

import org.opencyc.cycobject.CycFort;

public class Noun extends Denoting{
  private final int gender;
  private String toString;
  public int getGender(){
    return gender;
  }
  public boolean isPlural(){
    return plural;
  }
  private final boolean plural;
  public boolean isSingular(){
    return singular;
  }
  private final boolean singular;
  public Noun(CycFort denotat,boolean plural,int gender){
    this(denotat,plural,!plural,gender);
  }
  private Noun(CycFort denotat,boolean plural,boolean singular,int gender){
    super(denotat);
    this.gender=gender;
    this.plural=plural;
    this.singular=singular;
  }
  public Noun(CycFort denotat,boolean plural){
    this(denotat,plural,Gender.NEUTRAL);
  }
  public Noun(CycFort denotat){
    this(denotat,false,false,Gender.NEUTRAL);
  }
  public synchronized String toString(){
    if(toString==null)toString=getDenotat().cyclify()+(plural||singular?"("+
            (gender==Gender.NEUTRAL?"":Gender.toString(gender)+",")+
            (plural?"plural":"")+(plural&&singular?"&":"")+(singular?"singular":"")+")":"");
    return toString;
  }
}
