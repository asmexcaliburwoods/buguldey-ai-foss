package org.alpha.gui;
import org.alpha.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

public abstract class AbstractTransport implements Transport, SelectionListener{
  private TrayItem trayIcon;
  public final synchronized void setTrayIconVisible(boolean visible){
    TrayItem trayIcon=this.trayIcon;
    if(trayIcon==null){
      trayIcon=new TrayItem(Global.getDisplay().getSystemTray(),0);
      this.trayIcon=trayIcon;
      updateTrayIcon();
      trayIcon.addSelectionListener(this);
    }
    trayIcon.setVisible(visible);
  }
  public void updateTrayIcon(){
    trayIcon.setToolTipText(
          getTransportNameAsDisplayString()+": "+getTransportOnlineStatusAsDisplayString());
    trayIcon.setImage(getTransportOnlineStatusAsIconImage());
  }

  /** single click */
  public void widgetSelected(SelectionEvent e){
    ContactListWindow contactListWindow=Global.getContactListWindow();
    if(contactListWindow.isVisible())contactListWindow.setVisible(false);
    else{
      contactListWindow.setVisible(true);
      contactListWindow.setForeground();
    }
  }
  /** double click
   * @noinspection NoopMethodInAbstractClass*/
  public void widgetDefaultSelected(SelectionEvent e){
  }
}
