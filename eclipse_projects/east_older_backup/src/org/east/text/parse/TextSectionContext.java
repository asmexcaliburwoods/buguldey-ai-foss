package org.east.text.parse;

import org.east.concepts.utility.Context;
import org.east.concepts.utility.TextSection;
import org.east.gui.workbench.treesFrame.TreeFactory;

import javax.swing.tree.DefaultMutableTreeNode;

public class TextSectionContext extends Context{
  private TextSection textSection;
  private TextContext textContext;
  public TextSectionContext(TextContext textContext,TextSection textSection){
    super("TextSectionContext", "text-section-context");
    this.textSection=textSection;
    this.textContext=textContext;
  }
  public TextSection getTextSection(){
    return textSection;
  }
  public TextContext getTextContext(){
    return textContext;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    DefaultMutableTreeNode superTree=super.toTree(tf);
    if(textSection==null)
      tf.addNamedChild(superTree,"text-section","null");
    else
      tf.addNamedChild(superTree,"text-section",textSection.toTree(tf));
    if(textContext==null)
      tf.addNamedChild(superTree,"text-context","null");
    else
      tf.addNamedChild(superTree,"text-context",textContext.toTree(tf));
    return superTree;
  }
}
