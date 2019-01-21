package org.east.pos;

public final class Gender{
  private Gender(){}
  public static final int MASCULINE=1;
  public static final int FEMININE=2;
  public static final int NEUTRAL=3;
  public static String toString(int gender){
    switch(gender){
      case MASCULINE: return "masculine";
      case FEMININE: return "feminine";
      case NEUTRAL: return "gender-neutral";
      default: throw new RuntimeException();
    }
  }
  public static String toShortString(int gender){
    switch(gender){
      case MASCULINE: return "m";
      case FEMININE: return "f";
      case NEUTRAL: return "";
      default: throw new RuntimeException();
    }
  }
}
