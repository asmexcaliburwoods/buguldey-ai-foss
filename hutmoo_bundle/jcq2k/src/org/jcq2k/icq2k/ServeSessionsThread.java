package org.jcq2k.icq2k;

import java.util.*;
import org.jcq2k.*;
import org.jcq2k.util.*;
import org.jcq2k.util.joe.*;


/**
  Provides handling of ICQ2KMessagingNetwork's incoming data
  stream.
  <p>
  <b>Multiplicity.</b>
  <p>
  At any given moment, there are zero or one instances
  of ServeSessionsThread per single instance of
  ICQ2KMessagingNetwork.  When there is at least one Session,
  the thread is running.  When there are no Sessions, no
  thread is running.
  <p>
  <b>Dispatch mainloop.</b>
  <p>
  The thread calls Session.tick(PluginContext) for each running
  Session.
  <ul>
  <li>If Session needs to be called once again as soon as possible,
          tick() returns true.
  <li>If Session doesn't need to be called again,
            tick() returns false.
  </ul>
  <p>
  When tick() methods are called on all Sessions,
  the following occurs:
  <ul>
  <li>If all sessions returned false, the thread waits approx. 100
                  milliseconds, and then repeats the loop.
  <li>If some session returned true, the thread repeats
                  the loop without waiting.
  <li>If some session is logged out (removed from the list)
                  while the thread was waiting,
                  then the thread stops waiting, and then either dies or
                  repeats the loop.
  </ul>
  <p>
  Sessions can disallow calling their tick() methods using the
  isRunning() method.  If the Session is not running,
  the tick() is never called.  If the Session is running,
  the tick() can be called.
  <p>
  Session is not running:
  <ul>
  <li>while the login sequence (since while the login sequence,
                  events are dispatched by the thread that called login()), and
  <li>after the Session shutdown(), when it is not yet removed from
      the list of Sessions.
  </ul>
  <p>
  Session is running after successful login(), until the
  Session shutdown().

  @see ICQ2KMessagingNetwork
  @see Session
  @see Session#tick(PluginContext)
  @see Session#isRunning()
*/

public final class ServeSessionsThread extends Thread
{
  private final static org.log4j.Category CAT = org.log4j.Category.getInstance(ServeSessionsThread.class.getName());
  private PluginContext context;
  private static int count = 1;

public ServeSessionsThread(PluginContext ctx)
{
  super("icq2k/st"+(count++));
  this.context = ctx;
}
  public void run()
  {
    try
    {
        CAT.debug("icq2k sess thread started");
        for (;;)
        {
          boolean dataIsWaiting = false;
          Enumeration e = context.getICQ2KMessagingNetwork().getResourceManager().getSessions();
          if (!e.hasMoreElements())
                  break;
          while (e.hasMoreElements())
          {
            Session sess = (Session) e.nextElement();
            try
            {
              if (sess.isRunning())
                dataIsWaiting |= sess.tick(context);
            }
            catch (Exception ex)
            {
              CAT.warn("exception while sess.tick()", ex);
            }
          }
          if (!dataIsWaiting)
          {
            context.getICQ2KMessagingNetwork().getResourceManager().waitForSessionListChange(20);
          }
        }
    }
    catch (Throwable tr)
    {
      CAT.error("Exception while icq2k session loop", tr);
    }
    finally
    {
      CAT.debug("icq2k sess thread finished");
    }
  }
}