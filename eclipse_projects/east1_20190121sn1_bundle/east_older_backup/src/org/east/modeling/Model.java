package org.east.modeling;

import org.east.concepts.utility.Node;
import org.east.concepts.TextUnderstandingArc;

import java.io.Serializable;

public class Model implements Serializable{
  private TextUnderstandingArc arc;
  public Model(TextUnderstandingArc arc){
    this.arc=arc;
  }
  private Node assertionsForVerbs;
  public Node getAssertionsForVerbs(){
    return assertionsForVerbs;
  }
  public void setAssertionsForVerbs(Node assertionsForVerbs){
    this.assertionsForVerbs=assertionsForVerbs;
    save();
  }
  public void save(){
    arc.save();
  }
}
