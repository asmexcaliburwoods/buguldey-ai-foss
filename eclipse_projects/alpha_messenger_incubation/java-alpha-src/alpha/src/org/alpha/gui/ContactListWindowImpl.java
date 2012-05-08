package org.alpha.gui;

import org.alpha.registry.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.win32.*;
import org.eclipse.swt.widgets.*;
import java.util.logging.*;

public class ContactListWindowImpl implements ContactListWindow{
  private static final Logger log=Logger.getLogger(ContactListWindowImpl.class.getName());
  private boolean visible;
  private static final String CONTACTLIST_BOUNDS_REGISTRY_KEY="contactListBounds";
  private static final int DISTANCE_BETWEEN_LABELS=2;
  private Label visibilityLabel;
  static{
    System.loadLibrary("ContactListWindowImpl");
  }
  //private static native int createWindow_getHWND();
  private static native int getActiveWindowHWND();
  private static native int getForegroundWindowHWND();
  //private static native void hideWindow(int hwnd);
  //private static native void showWindow(int hwnd);
  private static native void setForeground0(int hwnd);
  private final Shell shell;
  private Label menuAlphaLabel;
  private Point easyMoveOldEventPoint;
  private Label statusLabel;
  //private int hwnd;
  {
    //hwnd=createWindow_getHWND();
    //if(hwnd!=0)
//      shell=Shell.win32_new(Global.getDisplay(),hwnd);
//    else
//      shell=null;
    shell=new Shell(Global.getDisplay());
    OS.SetWindowLong(shell.handle,OS.GWL_EXSTYLE,OS.WS_EX_TOOLWINDOW);
    OS.SetWindowLong(shell.handle,OS.GWL_STYLE,
            OS.GetWindowLong(shell.handle,OS.GWL_STYLE)&
                    ~(OS.WS_CAPTION | OS.WS_SYSMENU | OS.WS_MINIMIZEBOX));
    Rectangle defaultBounds=new Rectangle(0,0,200,300);
    Rectangle bounds=(Rectangle)Preferences.userNode.getObject(
            CONTACTLIST_BOUNDS_REGISTRY_KEY,defaultBounds);
    shell.setBounds(bounds);
    {
      menuAlphaLabel=new Label(shell, SWT.LEFT);
      Image menuImage=new Image(Global.getDisplay(), "alpha.png");
      menuAlphaLabel.setImage(menuImage);
      Rectangle menuImageBounds=menuImage.getBounds();
      menuAlphaLabel.setSize(menuImageBounds.width,menuImageBounds.height);//b.computeSize(SWT.DEFAULT,SWT.DEFAULT));
      moveMenuAlphaLabel();
    }
    {
      statusLabel=new Label(shell, SWT.LEFT);
      Image statusImage=TransportsImpl.getOfflineStatusIconImage();
      statusLabel.setImage(statusImage);
      Rectangle statusImageBounds=statusImage.getBounds();
      statusLabel.setSize(statusImageBounds.width,statusImageBounds.height);//b.computeSize(SWT.DEFAULT,SWT.DEFAULT));
      moveStatusLabel();
    }
    {
      visibilityLabel=new Label(shell, SWT.LEFT);
      Image image=TransportsImpl.getVisibilityIconImage(TransportsImpl.getDefaultVisibility());
      statusLabel.setImage(image);
      Rectangle statusImageBounds=image.getBounds();
      statusLabel.setSize(statusImageBounds.width,statusImageBounds.height);//b.computeSize(SWT.DEFAULT,SWT.DEFAULT));
      moveStatusLabel();
    }

    shell.addControlListener(new ControlListener(){
      public void controlMoved(ControlEvent e){
        storeBounds();
      }
      public void controlResized(ControlEvent e){
        moveMenuAlphaLabel();
        moveStatusLabel();
        storeBounds();
      }
    });
//    shell.addDisposeListener(new DisposeListener(){
//      public void widgetDisposed(DisposeEvent e){
//        Global.exit();
//      }
//    });
    final Color red = new Color(Global.getDisplay(), 0xFF, 0, 0);
    shell.addPaintListener(new PaintListener () {
      public void paintControl(PaintEvent event){
        GC gc = event.gc;
        gc.setForeground(red);
        Rectangle rect = shell.getClientArea();
        gc.drawRectangle(rect.x + 10, rect.y + 10, rect.width - 20, rect.height - 20);
      }
    });
    shell.addMouseMoveListener(new MouseMoveListener(){
      public void mouseMove(MouseEvent e){
        Point easyMoveOldEventPoint=
                ContactListWindowImpl.this.easyMoveOldEventPoint;
        if(easyMoveOldEventPoint!=null){
          Shell shell=ContactListWindowImpl.this.shell;
          Point old=shell.getLocation();
          Point eScreen=shell.toDisplay(e.x,e.y);
          shell.setLocation(
                  old.x+eScreen.x-easyMoveOldEventPoint.x,
                  old.y+eScreen.y-easyMoveOldEventPoint.y);
          ContactListWindowImpl.this.easyMoveOldEventPoint=eScreen;
        }
      }
    });
    shell.addMouseListener(new MouseListener(){
      public void mouseDoubleClick(MouseEvent e){
      }
      public void mouseDown(MouseEvent e){
        easyMoveOldEventPoint=shell.toDisplay(e.x,e.y);
        shell.setCapture(true);
      }
      public void mouseUp(MouseEvent e){
        if(easyMoveOldEventPoint!=null){
          easyMoveOldEventPoint=null;
          shell.setCapture(false);
        }
      }
    });
  }
  private void moveStatusLabel(){
    Point location=menuAlphaLabel.getLocation();
    statusLabel.setLocation(location.x+DISTANCE_BETWEEN_LABELS,location.y);
  }
  private void moveVisibilityLabel(){
    Point location=statusLabel.getLocation();
    visibilityLabel.setLocation(location.x+DISTANCE_BETWEEN_LABELS,location.y);
  }
  private void storeBounds(){
    try{
      Preferences.userNode.putObject(CONTACTLIST_BOUNDS_REGISTRY_KEY,
              shell.getBounds());
    } catch(Exception e1){
      log.throwing(ContactListWindowImpl.class.getName(),"",e1);
    }
  }
  private void moveMenuAlphaLabel(){
    Rectangle clientRect=shell.getClientArea();
    menuAlphaLabel.setLocation(clientRect.x,clientRect.y+clientRect.height-menuAlphaLabel.getBounds().height);
  }
  public Shell getShell(){
    return shell;
  }
  public void setVisible(boolean visible){
    this.visible=visible;
//    if(visible)showWindow(shell.handle);
//    else hideWindow(shell.handle);
    shell.setVisible(visible);
  }
  public void setForeground(){
    setForeground0(shell.handle);
  }
  public boolean isActive(){
    int activeWindowHWND=getActiveWindowHWND();
    return activeWindowHWND==shell.handle;
  }
  public boolean isForeground(){
    int foregroundWindowHWND=getForegroundWindowHWND();
    return foregroundWindowHWND==shell.handle;
  }
  public boolean isVisible(){
    return visible;
  }
}
