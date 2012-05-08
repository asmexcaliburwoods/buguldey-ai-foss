package org.alpha.gui;
import org.alpha.*;
import org.eclipse.swt.graphics.*;
import java.util.*;

public class TransportsImpl{
  private static List transports=new LinkedList();
  private static final Image OFFLINE_IMAGE=new Image(Global.getDisplay(),"offline.png");
  public static void setTransportIconsVisible(boolean visible){
    Transport[] transports=getTransports();
    int length=transports.length;
    for(int i=0;i<length;i++){
      Transport transport=transports[i];
      transport.setTrayIconVisible(visible);
    }
  }
  public static Transport[] getTransports(){
    return (Transport[])transports.toArray(new Transport[0]);
  }
  public static void addTransport(Transport transport){
    transports.add(transport);
    transport.setTrayIconVisible(true);
  }
  /** icons for the menu */
  public static Image getOfflineStatusIconImage(){
    return OFFLINE_IMAGE;
  }
  private static int defaultVisibility=Constants.VISIBILITY_VISIBLE_NORMAL;
  public static int getDefaultVisibility(){
    return defaultVisibility;
  }
  public static Image getVisibilityIconImage(int visibility){
    return;
  }
}
