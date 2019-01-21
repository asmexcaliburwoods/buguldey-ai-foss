package org.jcq2k.test;

import java.applet.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import org.jcq2k.util.joe.*;
import org.jcq2k.*;

/**
The console application that can be used to test
functionality of the ICQ2KMessagingNetwork and
ICQMessagingNetwork plugins.
<p>
@see org.jcq2k.icq2k.ICQ2KMessagingNetwork
*/
public class ICQMessagingTest_Console
{
  private final static org.log4j.Category CAT = org.log4j.Category.getInstance(ICQMessagingTest_Console.class.getName());
  public static ICQMessagingTest cfg = new ICQMessagingTest();
  private MessagingNetwork plugin;
  String loginId;
  String password;
  {
    try
    {
      String className = cfg.REQPARAM_MESSAGING_NETWORK_IMPL_CLASS_NAME.trim();
      CAT.info("Instantiating class \"" + className + "\"...");
      plugin = (MessagingNetwork) Class.forName(className).newInstance();
      plugin.init();
      loginId = ""+cfg.REQPARAM_SRC_LOGIN_ID;
      password = cfg.REQPARAM_SRC_PASSWORD;
    }
    catch (Throwable tr)
    {
    CAT.error("exception", tr);
      System.exit(1);
    }
  }
  String contactList = null;
  {
    StringBuffer sb = new StringBuffer();
    StringTokenizer st = new StringTokenizer(cfg.REQPARAM_CONTACT_LIST_LOGIN_IDS, ",");
    while (st.hasMoreTokens())
    {
      sb.append(st.nextToken());
      if (st.hasMoreTokens())
        sb.append("\n");
    }
    contactList = sb.toString();
  }
  String[] getContactList()
  {
    java.util.List cl = new java.util.LinkedList();
    StringTokenizer st = new StringTokenizer(contactList);
    StringBuffer dbg = new StringBuffer("test app contactlist: ");
    while (st.hasMoreTokens())
    {
      String loginId = st.nextToken().trim();
      if (loginId.length() == 0)
        continue;
      dbg.append("'" + loginId + "' ");
      cl.add(loginId);
    }
    CAT.info(dbg.toString());
    return (String[]) cl.toArray(new String[cl.size()]);
  }
  //
  String getMyLoginId()
  {
    return loginId;
  }
  void login()
  {
    try
    {
      plugin.login(getMyLoginId(), password, getContactList(), MessagingNetwork.STATUS_ONLINE);
    }
    catch (Throwable tr)
    {
    CAT.error("exception", tr);
    }
  }
  void logout()
  {
      try
    {
      plugin.logout(getMyLoginId());
        }
      catch (Throwable tr)
      {
    CAT.error("exception", tr);
        }
    }
  public static void main(java.lang.String[] args)
  {
    try
    {
      System.err.println("logging is done using log4j.");
      new ICQMessagingTest_Console().run();
    }
    catch (Throwable tr)
    {
    CAT.error("exception", tr);
      System.exit(1);
    }
  }
  public void run()
  {
    try
    {
      login();
    }
    catch (Throwable tr)
    {
    CAT.error("exception", tr);
    }
  }
}