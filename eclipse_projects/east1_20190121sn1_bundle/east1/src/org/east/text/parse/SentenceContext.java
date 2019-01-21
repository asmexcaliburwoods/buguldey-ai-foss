package org.east.text.parse;

import org.east.concepts.utility.Context;
import org.east.concepts.utility.Sentence;
import org.east.gui.workbench.treesFrame.TreeFactory;

import javax.swing.tree.DefaultMutableTreeNode;

public class SentenceContext extends Context{
  private Sentence sentence;
  private TextSectionContext parentTextSectionContext;
  public SentenceContext(TextSectionContext parentTextSectionContext,Sentence s){
    super("SentenceContext","sentence-context");
    this.sentence=s;
    this.parentTextSectionContext=parentTextSectionContext;
  }
  public Sentence getSentence(){
    return sentence;
  }
  public TextSectionContext getParentTextSectionContext(){
    return parentTextSectionContext;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    DefaultMutableTreeNode superTree=super.toTree(tf);
    tf.addNamedChild(superTree,"sentence",sentence);
    tf.addChild(superTree,parentTextSectionContext.toTree(tf));
    return superTree;
  }
}
