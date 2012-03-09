package org.east.concepts.utility;

import org.east.gui.workbench.treesFrame.TreeFactory;
import org.east.text.parse.TextSectionContext;

import javax.swing.tree.DefaultMutableTreeNode;

public abstract class TextSection extends Titled{
  private static final long serialVersionUID = 7539172355953133440L;
  private String content;
  private Text parentText;
  private TextSectionContext textSectionContext;
  public TextSection(String title,String content,Text parentText){
    super(title);
    this.parentText=parentText;
    this.textSectionContext=new TextSectionContext(
            parentText==null?null:parentText.getTextContext(),
            this);
    this.content=content;
  }
  public Text getParentText(){
    return parentText;
  }
  public String getContent(){
    return content;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    DefaultMutableTreeNode superTree=super.toTree(tf);
    DefaultMutableTreeNode textSection=tf.named("text-section",
            tf.named("title",superTree));
    tf.addNamedChild(textSection,"content",getContent());
    Text parentText=getParentText();
    tf.addNamedChild(textSection,"parent-text",parentText==null?(Object)"null":parentText);
    return textSection;
  }
  public TextSectionContext getTextSectionContext(){
    return textSectionContext;
  }
}
