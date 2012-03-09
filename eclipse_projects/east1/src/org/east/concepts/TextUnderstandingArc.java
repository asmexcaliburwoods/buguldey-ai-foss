package org.east.concepts;

import org.east.concepts.utility.Text;
import org.east.modeling.Model;

import java.io.Serializable;

public class TextUnderstandingArc implements Serializable{
  private static final long serialVersionUID = 2577046613553944843L;
  private FileConcept fileConcept;
  public TextUnderstandingArc(FileConcept fileConcept){
    this.fileConcept=fileConcept;
  }
  private long lastUpdated;
  public long getLastUpdated(){
    return lastUpdated;
  }
  public void setLastUpdated(long lastUpdated){
    this.lastUpdated=lastUpdated;
    save();
  }
  public Text getText(){
    return text;
  }
  private Text text;
  public void setText(Text text){
    this.text=text;
    save();
  }
  private Model model=new Model(this);
  public Model getModel(){
    return model;
  }
  public void save(){
    fileConcept.save();
  }
  public FileConcept getFileConcept(){
    return fileConcept;
  }
}
