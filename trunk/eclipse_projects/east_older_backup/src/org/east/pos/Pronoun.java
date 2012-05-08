package org.east.pos;

import org.opencyc.cycobject.CycFort;

public class Pronoun extends Denoting{
  public static final int TYPE_OBJECT_PRONOUN = 1;
  public static final int TYPE_POSSESSIVE_PRONOUN_PRE = 2;
  public static final int TYPE_POSSESSIVE_PRONOUN_POST = 3;
  private String spelling;
  private int person;
  private boolean plural;
  private int gender;
  private int type;
  public Pronoun(CycFort denotat,
                 String spelling,
                 int personInt, boolean pluralBool, int genderInt, int pronounType){
    super(denotat);
    this.spelling=spelling;
    this.person=personInt;
    this.plural=pluralBool;
    this.gender=genderInt;
    this.type=pronounType;
  }
  /** 3 for third person */
  public int getPerson(){
    return person;
  }
  public boolean isPlural(){
    return plural;
  }
  public int getGender(){
    return gender;
  }
  public int getType(){
    return type;
  }
  public String toString(){
    return spelling+" "+getDenotat().cyclify();
  }
}
