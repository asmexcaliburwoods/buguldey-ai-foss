package org.jcq2k.test;

import java.applet.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import org.jcq2k.util.joe.*;
import org.jcq2k.*;

/**
  The GUI AWT application that can be used to test
  functionality of the ICQ2KMessagingNetwork and
  ICQMessagingNetwork plugins.
  <p>
  @see org.jcq2k.icq2k.ICQ2KMessagingNetwork
*/

public class ICQMessagingTest_Applet extends Panel implements Runnable
{
  private final static org.log4j.Category CAT = org.log4j.Category.getInstance(ICQMessagingTest_Applet.class.getName());
  public static ICQMessagingTest cfg = new ICQMessagingTest();
  private Thread thread;
  private MessagingNetwork plugin;

  private Button userDetailsButton;

  private Button getUserDetailsButton()
  {
    if (userDetailsButton == null)
    {
      userDetailsButton = new Button("user info");
      userDetailsButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          try
          {
            fetchUserDetails();
          }
          catch (Throwable tr)
          {
            CAT.error("exception", tr);
          }
        }
      });
    }
    return userDetailsButton;
  }

  private Button sendContactsButton;

  private Button getSendContactsButton()
  {
    if (sendContactsButton == null)
    {
      sendContactsButton = new Button("send random contacts");
      sendContactsButton.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          sendContacts();
        }
      });
    }
    return sendContactsButton;
  }

  TextArea eventLog = new TextArea("### incoming events ###\n");
  {
    eventLog.setEditable(false);
  }
  Object eventLogLock = new Object();
  Button closeBtn = new Button("close");
  {
    closeBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        try
        {
          quit();
        }
        catch (Throwable tr)
        {
          CAT.error("exception", tr);
        }
      }
    });
  }
  Choice clientStatus = new Choice();
  {
    clientStatus.add("Online");
    clientStatus.add("Offline");
    clientStatus.add("Busy");
    clientStatus.select("Offline");
    clientStatus.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent e)
      {
        new Thread("icqtest/chooser control handler")
        {
          public void run()
          {
            try
            {
              if (clientStatus.getSelectedItem().equals("Online"))
              {
                if (plugin.getClientStatus(getMyLoginId()) == org.jcq2k.MessagingNetwork.STATUS_OFFLINE)
                  login();
                plugin.setClientStatus(getMyLoginId(), org.jcq2k.MessagingNetwork.STATUS_ONLINE);
              }
              else
                if (clientStatus.getSelectedItem().equals("Busy"))
                {
                  if (plugin.getClientStatus(getMyLoginId()) == org.jcq2k.MessagingNetwork.STATUS_OFFLINE)
                    login();
                  plugin.setClientStatus(getMyLoginId(), org.jcq2k.MessagingNetwork.STATUS_BUSY);
                }
                else
                  if (clientStatus.getSelectedItem().equals("Offline"))
                  {
                    if (plugin.getClientStatus(getMyLoginId()) != org.jcq2k.MessagingNetwork.STATUS_OFFLINE)
                      plugin.setClientStatus(getMyLoginId(), org.jcq2k.MessagingNetwork.STATUS_OFFLINE);
                  }
                  else
                  {
                    org.jcq2k.util.joe.Lang.ASSERT_FALSE("invalid clientStatus.getSelectedItem()");
                  }
            }
            catch (Throwable tr)
            {
              printException(tr);
            }
          }
        }
        .start();
      }
    });
  }
  TextField loginId = new TextField();
  TextField contactListEntry = new TextField("" + cfg.REQPARAM_CONTACT_LIST_ENTRY_LOGIN_ID);
  Button addToContactList = new Button("add");
  {
    addToContactList.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        try
        {
          String cl = contactList.getText();
          for (;;)
          {
            int pos = cl.indexOf(contactListEntry.getText());
            if (pos == -1)
              break;
            cl = cl.substring(0, pos) + cl.substring(pos + contactListEntry.getText().length());
          }
          cl += "\n" + contactListEntry.getText();
          for (;;)
          {
            int pos = cl.indexOf("\n\n");
            if (pos == -1)
              break;
            cl = cl.substring(0, pos) + cl.substring(pos + "\n".length());
          }
          if (cl.startsWith("\n"))
            cl = cl.substring("\n".length());
          if (cl.endsWith("\n"))
            cl = cl.substring(0, cl.length() - "\n".length());
          contactList.setText(cl);

          //
          plugin.addToContactList(getMyLoginId(), contactListEntry.getText());
        }
        catch (Throwable tr)
        {
          printException(tr);
        }
      }
    });
  }
  void fetchUserDetails()
  {
    try
    {
      UserDetails d = plugin.getUserDetails(getMyLoginId(), contactListEntry.getText());
      String s = getMyLoginId()+" reports: user details for "+contactListEntry.getText()+
        " are:\r\n  nick="+StringUtil.toPrintableString(d.getNick())+
        ",\r\n  real name="+StringUtil.toPrintableString(d.getRealName())+
        ",\r\n  email="+StringUtil.toPrintableString(d.getEmail())+".";
      CAT.info(s);
      log(s);
    }
    catch (Exception ex)
    {
      printException(ex);
    }
  }

  void sendContacts()
  {
    try
    {
      int n = (int) (7 * Math.random());
      if (n < 1) n = 2;

      String[] nicks = new String[n];
      String[] loginIds = new String[n];

      int i = 0;
      while (i < n)
      {
        nicks[i] = "random uin #" + i;
        loginIds[i]  = "" + (22222+(int) (10000000 * Math.random()));
        i++;
      }
      plugin.sendContacts(getMyLoginId(), contactListEntry.getText(), nicks, loginIds);
    }
    catch (Exception ex)
    {
      printException(ex);
    }
  }


  Button removeFromContactList = new Button("remove");
  {
    removeFromContactList.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        try
        {
          String cl = contactList.getText();
          for (;;)
          {
            int pos = cl.indexOf(contactListEntry.getText());
            if (pos == -1)
              break;
            cl = cl.substring(0, pos) + cl.substring(pos + contactListEntry.getText().length());
          }
          for (;;)
          {
            int pos = cl.indexOf("\n\n");
            if (pos == -1)
              break;
            cl = cl.substring(0, pos) + cl.substring(pos + "\n\n".length());
          }
          if (cl.startsWith("\n"))
            cl = cl.substring("\n".length());
          if (cl.endsWith("\n"))
            cl = cl.substring(0, cl.length() - "\n".length());
          contactList.setText(cl);

          //
          plugin.removeFromContactList(getMyLoginId(), contactListEntry.getText());
        }
        catch (Throwable tr)
        {
          printException(tr);
        }
      }
    });
  }
  TextField password = new TextField();
  {
    password.setEchoChar('*');
  }
  TextArea contactList = new TextArea();
  {
    StringBuffer sb = new StringBuffer();
    StringTokenizer st = new StringTokenizer(cfg.REQPARAM_CONTACT_LIST_LOGIN_IDS, ",");
    while (st.hasMoreTokens())
    {
      sb.append(st.nextToken());
      if (st.hasMoreTokens())
        sb.append("\n");
    }
    contactList.setText(sb.toString());
  }
  TextField dstLoginId = new TextField(""+cfg.REQPARAM_DST_LOGIN_ID);
  TextField sendMsg = new TextField("type msg and press enter");

    {
    ActionListener al = new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        if (!(StringUtil.isNullOrTrimmedEmpty(dstLoginId.getText())) && !(StringUtil.isNullOrTrimmedEmpty(sendMsg.getText())))
        {
          try
          {
            plugin.sendMessage(getMyLoginId(), dstLoginId.getText(), sendMsg.getText());
            sendMsg.setText("msg " + Math.random());
          }
          catch (Throwable tr)
          {
            printException(tr);
          }
        }
      }
    };
    sendMsg.selectAll();
    sendMsg.addActionListener(al);
    dstLoginId.addActionListener(al);
  }
  Button clearEventLogBtn = new Button("clear");
  {
    clearEventLogBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        try
        {
          clearEventLog();
        }
        catch (Throwable tr)
        {
          CAT.error("exception", tr);
        }
      }
    });
  }
  Button loginBtn = new Button("login");
  {
    loginBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        new Thread("icqtest/login button handler")
        {
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
        .start();
      }
    });
  }
  Button logoutBtn = new Button("logout");

    {
    logoutBtn.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent ev)
      {
        try
        {
          logout();
        }
        catch (Throwable tr)
        {
          CAT.error("exception", tr);
        }
      }
    });
  }
  public void clearEventLog()
  {
    synchronized (eventLogLock)
    {
      eventLog.setText("");
    }
  }
  void enableLoginUI()
  {
    try
    {
      logoutBtn.setEnabled(false);
      contactList.setEditable(true);
      loginBtn.setEnabled(true);
      loginId.setEnabled(true);
      password.setEnabled(true);
    }
    catch (Throwable tr)
    {
      CAT.error("exception", tr);
    }
  }
  String[] getContactList()
  {
    java.util.List cl = new java.util.LinkedList();
    StringTokenizer st = new StringTokenizer(contactList.getText());
    StringBuffer sb = new StringBuffer();
    StringBuffer dbg = new StringBuffer("test applet contactlist: ");
    while (st.hasMoreTokens())
    {
      String loginId = st.nextToken().trim();
      if (loginId.length() == 0)
        continue;
      dbg.append("'" + loginId + "' ");
      cl.add(loginId);
      sb.append(loginId).append('\n');
    }
    CAT.info(dbg.toString());
    contactList.setText(sb.toString());
    return (String[]) cl.toArray(new String[cl.size()]);
  }
  String getMyLoginId()
  {
    return loginId.getText();
  }
  public void init()
  {
    try
    {
      //data init
      loginId.setText(""+cfg.REQPARAM_SRC_LOGIN_ID);
      password.setText(""+cfg.REQPARAM_SRC_PASSWORD);

      //ui init
      setLayout(new GridLayout(2, 1));
      Panel inputArea = new Panel(new BorderLayout(2, 2));
      inputArea.add(contactList, "Center");
      Panel bottomR = new Panel(new FlowLayout(FlowLayout.RIGHT));
      bottomR.add(loginBtn);
      bottomR.add(logoutBtn);
      bottomR.add(closeBtn);
      Panel bottomL = new Panel(new FlowLayout());
      bottomL.add(new Label("status:"));
      bottomL.add(clientStatus);
      bottomL.add(new Label("  contact:"));
      bottomL.add(contactListEntry);
      bottomL.add(addToContactList);
      bottomL.add(removeFromContactList);
      bottomL.add(getUserDetailsButton());
      bottomL.add(getSendContactsButton());
      Panel bottom = new Panel(new BorderLayout());
      bottom.add("Center", bottomR);
      bottom.add("West", bottomL);
      inputArea.add(bottom, "South");
      Panel leftTop = new Panel(new GridLayout(10, 1));
      leftTop.add(new Label("login id:")); //1
      leftTop.add(loginId); //2
      leftTop.add(new Label("password:"));
      leftTop.add(password); //4
      leftTop.add(new Label(""));
      leftTop.add(new Label("")); //6

      leftTop.add(new Label("send msg"));
      leftTop.add(sendMsg); //8
      leftTop.add(new Label("to"));
      leftTop.add(dstLoginId); //10

      Panel left = new Panel(new FlowLayout());
      left.add(leftTop);
      inputArea.add(left, "West");
      Panel eventLogPanel = new Panel(new BorderLayout());
      eventLogPanel.add("Center", eventLog);
      Panel eventLogPanelButtons = new Panel(new FlowLayout(FlowLayout.RIGHT));
      eventLogPanelButtons.add(clearEventLogBtn);
      eventLogPanel.add("South", eventLogPanelButtons);
      add(inputArea);
      add(eventLogPanel);
      setBackground(SystemColor.control);
      doLayout();
      sendMsg.requestFocus();
    }
    catch (Throwable tr)
    {
      CAT.error("exception", tr);
      System.exit(1);
    }
  }
  public void log(String s)
  {
    synchronized (eventLogLock)
    {
      eventLog.append("[" + org.jcq2k.util.joe.Logger.formatCurrentDate() + "]\t" + s);
      eventLog.append("\n");
    }
  }
  void login()
  {
    try
    {
      logoutBtn.setEnabled(true);
      contactList.setEditable(false);
      loginBtn.setEnabled(false);
      loginId.setEnabled(false);
      password.setEnabled(false);

      //
      plugin.login(getMyLoginId(), password.getText(), getContactList(), MessagingNetwork.STATUS_ONLINE);
      logoutBtn.setEnabled(true);
    }
    catch (Throwable tr)
    {
      printException(tr);
      boolean loggedIn = false;
      try
      {
        loggedIn = plugin.getClientStatus(getMyLoginId()) != MessagingNetwork.STATUS_OFFLINE;
      }
      catch (Throwable tr2)
      {
        printException(tr2);
      }
      if (!loggedIn)
      {
        enableLoginUI();
      }
    }
  }
  void logout()
  {
    try
    {
      //
      plugin.logout(getMyLoginId());
    }
    catch (Throwable tr)
    {
      printException(tr);
    }
    enableLoginUI();
  }
public static void main(java.lang.String[] args)
{
  String className = null;
  try
  {
    System.err.println("logging is done using log4j.");
    final ICQMessagingTest_Applet applet = new ICQMessagingTest_Applet();
    className = cfg.REQPARAM_MESSAGING_NETWORK_IMPL_CLASS_NAME.trim();
    CAT.info("Instantiating class \"" + className + "\"...");
    try
    {
      applet.plugin = (MessagingNetwork) Class.forName(className).newInstance();
      applet.plugin.init();
    }
    catch (Throwable tr)
    {
      CAT.error("ex in main", tr);
      System.exit(1);
    }
    java.awt.Frame frame = new java.awt.Frame("MessagingTest");
    frame.addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent e)
      {
        applet.quit();
      }
    });
    frame.add("Center", applet);
    frame.setSize(800, 650);
    frame.setLocation(150, 100);
    applet.init();
    frame.show();
    frame.invalidate();
    frame.validate();
    applet.start();
  }
  catch (Throwable tr)
  {
    CAT.error("exception", tr);
    System.exit(1);
  }
}
  void printException(Throwable tr)
  {
    CAT.error("exception", tr);
    //StringWriter sw = new StringWriter();
    //PrintWriter pw = new PrintWriter(sw);
  //tr.p rintStackTrace(pw);
    //pw.flush();
    //sw.flush();
    log(getMyLoginId() + " reports exception:\r\n  " + tr.getClass().getName() + "\r\n  " + tr.getMessage()); //sw.toString());
  }
  public void quit()
  {
    System.exit(0);
  }
  public void run()
  {
    try
    {
      //start logging in
      plugin.addMessagingNetworkListener(new MessagingNetworkListener()
      {
        public void messageReceived(byte networkId, String from, String to, String text)
        {
          CAT.info("incoming message from " + from + " to " + to + " (len: " + text.length() + "):\r\n\"" + text + "\"");
          log("incoming message from " + from + " to " + to + (text == null ? ": null (BUGGG!!)" : " (" + text.length() + " chars):\r\n\"" + text + "\""));
        }

        public void contactsReceived(byte networkId, String from, String to, String[] contactsUins, String[] contactsNicks)
        {
          StringBuffer sb = new StringBuffer("incoming contacts from " + from + " to " + to + ", number of contacts="+contactsNicks.length+":\r\n");
          int i = 0;
          while (i < contactsNicks.length)
          {
            sb.append(
              "  nick="+StringUtil.toPrintableString(contactsNicks[i])+"\r\n"+
              "  uin ="+StringUtil.toPrintableString(contactsUins[i])+"\r\n");
            i++;
          }
          String s = sb.toString();
          CAT.info(s);
          log(s);
        }

        public void statusChanged(byte networkId, String srcLoginId, String dstLoginId, int status, int reasonCategory, String reasonMessage)
        {
          String status_s = "invalid: " + status + " (BUGGG!)";
          switch (status)
          {
            case MessagingNetwork.STATUS_OFFLINE :
              status_s = "offline";
              break;
            case MessagingNetwork.STATUS_ONLINE :
              status_s = "online";
              break;
            case MessagingNetwork.STATUS_BUSY :
              status_s = "busy";
              break;
          }
          if (srcLoginId.equals(dstLoginId))
            log(srcLoginId + " changed its client status to " + status_s + " [" + (reasonMessage == null ? "no reason given" : "reason: " + reasonMessage) + "]");
          else
            log(srcLoginId + " reports: " + dstLoginId + " changed status to " + status_s);
          //java.awt.Toolkit.getDefaultToolkit().beep();
          if (getMyLoginId().equals(srcLoginId) && srcLoginId.equals(dstLoginId))
          {
            switch (status)
            {
              case MessagingNetwork.STATUS_OFFLINE :
                clientStatus.select("Offline");
                enableLoginUI();
                break;
              case MessagingNetwork.STATUS_ONLINE :
                clientStatus.select("Online");
                break;
              case MessagingNetwork.STATUS_BUSY :
                clientStatus.select("Busy");
                break;
            }
          }
        }
      });
      login();
      thread = null;
    }
    catch (Throwable tr)
    {
      printException(tr);
    }
  }
  public void start()
  {
    if (thread == null)
    {
      thread = new Thread(this);
      thread.start();
    }
  }
  public void stop()
  {
    try
    {
      logout();
    }
    catch (Throwable tr)
    {
      printException(tr);
    }
    thread = null;
  }
}