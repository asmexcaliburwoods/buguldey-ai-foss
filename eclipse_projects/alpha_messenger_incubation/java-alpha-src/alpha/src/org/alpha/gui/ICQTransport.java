package org.alpha.gui;
import org.eclipse.swt.graphics.*;

public class ICQTransport extends AbstractTransport{
  private Image transportOnlineStatusAsIconImage_Offline=
          new Image(Global.getDisplay(),"offline.png");
  public String getTransportNameAsDisplayString(){
    return "ICQ";
  }
  public String getTransportOnlineStatusAsDisplayString(){
    return "Offline";
  }
  public Image getTransportOnlineStatusAsIconImage(){
    return transportOnlineStatusAsIconImage_Offline;
  }
}
