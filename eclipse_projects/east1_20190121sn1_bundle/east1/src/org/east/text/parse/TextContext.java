package org.east.text.parse;

import org.east.concepts.utility.Context;
import org.east.concepts.utility.Text;
import org.east.gui.workbench.treesFrame.TreeFactory;

import javax.swing.tree.DefaultMutableTreeNode;

public class TextContext extends Context{
  private Text text;
  public TextContext(Text text){
    super("TextContext", "text-context");
    this.text=text;
  }
  public Text getText(){
    return text;
  }
  public DefaultMutableTreeNode toTree(TreeFactory tf){
    DefaultMutableTreeNode superTree=super.toTree(tf);
    tf.addChild(superTree,tf.named("text",text.toTree(tf)));
    return superTree;
  }
}
