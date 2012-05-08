package org.alpha.gui;
import org.eclipse.swt.widgets.*;
import java.util.logging.*;

public class Alpha{
  private static final Logger log=Logger.getLogger(Alpha.class.getName());
  public static void main(String[] s){
    try{
      ContactListWindow contactListWindow=new ContactListWindowImpl();
      Global.setContactListWindow(contactListWindow);
      Shell shell=contactListWindow.getShell();
      shell.setVisible(true);
      TransportsImpl.addTransport(new ICQTransport());
      Global.messageLoop();
      TransportsImpl.setTransportIconsVisible(false);
      contactListWindow.getShell().dispose();
      Global.getDisplay().dispose();
    }catch(Throwable tr){
      log.throwing(Alpha.class.getName(),"main",tr);
    }
  }
}
