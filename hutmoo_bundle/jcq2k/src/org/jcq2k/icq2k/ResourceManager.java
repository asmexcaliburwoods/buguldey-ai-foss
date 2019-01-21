package org.jcq2k.icq2k;

import java.util.*;
import org.jcq2k.*;
import org.jcq2k.util.joe.*;
import org.jcq2k.util.*;

/**
  Manages sessions and threads that belong to each given
  ICQ2KMessagingNetwork instance.
*/
class ResourceManager
{
  private final static org.log4j.Category CAT = org.log4j.Category.getInstance(ResourceManager.class.getName());
  private final Hashtable loginId2session = new java.util.Hashtable(25, 25);
  private ServeSessionsThread sessionThread;
  private PluginContext context;

  ResourceManager(PluginContext ctx)
  {
    this.context = ctx;
  }

  final Session createSession(String srcLoginId) throws MessagingNetworkException
  {
    Session session = getSession(srcLoginId);
    MLang.EXPECT(session == null, "'" + srcLoginId + "' is either logging in or logged in", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    synchronized (loginId2session)
    {
      session = makeSessionInstance(srcLoginId);
      loginId2session.put(srcLoginId, session);
      notifyThread();
      return session;
    }
  }

  /** Session factory */
  protected Session makeSessionInstance(String srcLoginId) throws MessagingNetworkException
  {
    return new Session(srcLoginId);
  }


  Aim_conn_t createTCPConnection(Session session, int connType, java.net.InetAddress ia, int port, PluginContext ctx) throws java.io.IOException, MessagingNetworkException
  {
    return new SimpleTCPConnection(session, connType, ia, port, ctx);
  }

  Session getSession(String loginId)
  {
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(loginId, "loginId");
    return (Session) loginId2session.get(loginId);
  }


  Session getSessionNotNull(String srcLoginId) throws MessagingNetworkException
  {
    Session session = getSession(srcLoginId);
    MLang.EXPECT_NOT_NULL(session, "Illegal state: cannot perform this operation while " + srcLoginId + " is logged out. (Session is null).", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    return session;
  }


  Enumeration getSessions()
  {
    synchronized (loginId2session)
    {
      return ((Hashtable) loginId2session.clone()).elements();
    }
  }


  private void notifyThread()
  {
    if (loginId2session.size() == 0)
    {
      if (sessionThread != null)
      {
        loginId2session.notify();
        sessionThread = null;
      }
    }
    else
    {
      if (sessionThread == null)
      {
        sessionThread = new ServeSessionsThread(context);
        sessionThread.start();
      }
      else
      {
        loginId2session.notify();
      }
    }
  }


  void notifyToHandleData()
  {
    synchronized (loginId2session)
    {
      notifyThread();
    }
  }


  void removeSession(Session session)
  {
    Lang.ASSERT_EQUAL(session.getStatus_Oscar(), StatusUtil.OSCAR_STATUS_OFFLINE, "status for '" + session.getLoginId() + "'", "StatusUtil.OSCAR_STATUS_OFFLINE");
    synchronized (loginId2session)
    {
      loginId2session.remove(session.getLoginId());
      notifyThread();
    }
  }


  void waitForSessionListChange(long timeoutMillis) throws InterruptedException
  {
    synchronized (loginId2session)
    {
      loginId2session.wait(timeoutMillis);
    }
  }


  public void init()
  {
    synchronized (loginId2session)
    {
      if (loginId2session.size() != 0)
        CAT.error("BUGGGGG", new AssertException("loginId2session must be empty after deinit()"));
      loginId2session.clear(); //to be more safe.
    }
  }
  public void deinit()
  {
    synchronized (loginId2session)
    {
      Enumeration e = loginId2session.elements();
      while (e.hasMoreElements())
      {
        Session ses = (Session) e.nextElement();
        try
        {
          ses.setLastError(new MessagingNetworkException("plugin stops or restarts", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED));
          ses.logout(context,
            MessagingNetworkException.CATEGORY_NOT_CATEGORIZED,
            "plugin stops or restarts");
        }
        catch (Exception ex)
        {
          CAT.error("ex during icq2k.deinit(), ignored", ex);
        }
      }
      if (loginId2session.size() != 0)
        CAT.error("BUGGGGG", new AssertException("loginId2session must be empty after deinit()"));
      loginId2session.clear(); //to be more safe.

      Thread t = sessionThread;
      if (t != null)
      {
        CAT.debug("waiting 1 sec. for the session thread to die...");
        try
        {
          t.join(1000);
        }
        catch (InterruptedException ex)
        {
          CAT.debug("interrupted ex", ex);
        }
        CAT.debug("waiting for session thread finished.");
        if (t.isAlive())
          CAT.error("thread "+t+" is still alive!", new Exception("still alive!"));
      }
    }
  }
}
