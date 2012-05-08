package org.alpha.gui;
import org.eclipse.swt.widgets.*;

public interface ContactListWindow{
  Shell getShell();
  boolean isActive();
  boolean isForeground();
  void setVisible(boolean visible);
  boolean isVisible();
  void setForeground();
}
