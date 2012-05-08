package org.alpha.gui;
import org.eclipse.swt.widgets.*;

/** @noinspection PublicMethodNotExposedInInterface*/
public class Global{
  private static Display display=new Display();
  private static boolean quit;
  private static ContactListWindow contactListWindow;
  private Global(){}
  public static Display getDisplay(){
    return display;
  }
  public static void exit(){
    quit=true;
    display.wake();
  }
  public static void messageLoop(){
    Display display=Global.display;
    while(!quit){
      if(display.readAndDispatch())continue;
      display.sleep();
    }
  }
  public static void setContactListWindow(ContactListWindow contactListWindow){
    Global.contactListWindow=contactListWindow;
  }
  public static ContactListWindow getContactListWindow(){
    return contactListWindow;
  }
//  private static ImageLoader imageLoader=new ImageLoader();
//  public static ImageLoader getImageLoader(){
//    return imageLoader;
//  }
}
