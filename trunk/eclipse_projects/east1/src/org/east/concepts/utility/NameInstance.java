package org.east.concepts.utility;

import org.east.concepts.Name;
import org.east.concepts.Concept;
import org.east.concepts.TextualContext;

import java.io.Serializable;

public class NameInstance implements Serializable{
  private static final long serialVersionUID = 5606267142423187804L;
  private Name name;
  private Concept concept;
  private TextualContext namingContext;
  public void setName(Name name){
    this.name=name;
  }
  public void setConcept(Concept concept){
    this.concept=concept;
  }
  public void setNamingContext(TextualContext namingContext){
    this.namingContext=namingContext;
  }
  public Name getName(){
    return name;
  }
  public Concept getConcept(){
    return concept;
  }
  public TextualContext getNamingContext(){
    return namingContext;
  }
}
