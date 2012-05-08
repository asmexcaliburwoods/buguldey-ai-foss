package org.alpha;
import org.eclipse.swt.graphics.*;

public interface Transport{
  void setTrayIconVisible(boolean visible);
  String getTransportNameAsDisplayString();
  String getTransportOnlineStatusAsDisplayString();
  Image getTransportOnlineStatusAsIconImage();
  void updateTrayIcon();
}
