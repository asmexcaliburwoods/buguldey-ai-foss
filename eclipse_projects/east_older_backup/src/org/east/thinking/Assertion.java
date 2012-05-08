package org.east.thinking;

import org.east.gui.workbench.treesFrame.DisplayableNode;

import java.io.Serializable;

public interface Assertion extends Serializable, DisplayableNode{
  void doAssert() throws Exception;
  void doRetract(String reason) throws Exception;
}
