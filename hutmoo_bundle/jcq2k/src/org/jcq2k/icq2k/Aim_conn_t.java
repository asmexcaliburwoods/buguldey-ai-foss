package org.jcq2k.icq2k;

import java.io.*;
// import java.awt.*;
// import java.awt.event.*;
import java.util.*;
import org.jcq2k.*;

/**
  Represents a base class for socket connections.
  Has with some functionality for dispatching incoming OSCAR events.
*/
public abstract class Aim_conn_t
{
  private final static org.log4j.Category CAT = org.log4j.Category.getInstance(Aim_conn_t.class.getName());
  private final Session session;
  protected InputStream inputStream;
  protected OutputStream outputStream;
  public int type;
  public int subtype;
  public int seqnum;
  public Object seqnum_lock = new Object();
  private final java.util.Vector rxhandlers = new java.util.Vector(10); //of RxTriggerStruct
  private long lastPacketSendTimeMillis = System.currentTimeMillis() - 2 * ICQ2KMessagingNetwork.REQPARAM_ADVANCED_RATECONTROL_SENDPACKET_MILLIS;
  private boolean rateControlOn = false;
  /*
  private final Frame traktor;
  private static boolean traktorConnectionDown = false;
  */

  public class RxTriggerStruct
  {
    int family = -1;
    int subtype = -1;
    RxHandler rxHandler;
  }
  /**
    Creates a new connection to the specified
    host of specified type.
    <br>
    <br>type: Type of connection to create
    <br>destination: Host to connect to (in "host:port" or "host" syntax)
  */
  public Aim_conn_t(Session session, int connType, java.net.InetAddress host, int port, PluginContext ctx) throws org.jcq2k.MessagingNetworkException, java.io.IOException
  {
    this.session = session;
    subtype = -1;
    type = connType;
    seqnum = 0;

    /*
    if (traktorConnectionDown) throw new IOException("network is down");

    traktor = new Frame(""+host+":"+port+" - Traktor");
    final Checkbox c = new Checkbox("break this & ALL future connections");
    c.setState(traktorConnectionDown);
    c.addItemListener(
      new ItemListener()
      {
        public void itemStateChanged(ItemEvent e)
        {
          traktorConnectionDown = c.getState();
          try
          {
            if (traktorConnectionDown) closeSocket();
          }
          catch (Exception ex)
          {
          }
        }
      });
    traktor.add(c);
    traktor.setSize(100,50);
    traktor.setLocation(200+type*30,450);
    traktor.show();
    */
  }
/**
 * AIMConnection constructor comment.
 */
public final void addHandler(RxHandler rxHander, int snacFamily, int snacSubtype)
{
    RxTriggerStruct s = new RxTriggerStruct();
    s.family = snacFamily;
    s.subtype = snacSubtype;
    s.rxHandler = rxHander;
    rxhandlers.add(s);
}
public final int available() throws IOException
{
  if (Thread.currentThread().isInterrupted())
    throw new InterruptedIOException();
  return getInputStream().available();
}
/**
  Closes the connection.
  <p>
  Never throws Exceptions.
*/
public abstract void closeSocket();

public synchronized void setRateControlOn(boolean on)
{
  this.rateControlOn = on;
}
public synchronized final void flush() throws IOException, MessagingNetworkException
{
  if (rateControlOn)
  {
    long pauseMillis = lastPacketSendTimeMillis + ICQ2KMessagingNetwork.REQPARAM_ADVANCED_RATECONTROL_SENDPACKET_MILLIS - System.currentTimeMillis();
    if (pauseMillis > 0)
    {
      session.sleep("sendpacket ratecontrol", pauseMillis);
    }
    getOutputStream().flush();
    lastPacketSendTimeMillis = System.currentTimeMillis();
  }
  else
    getOutputStream().flush();
}
protected final java.io.InputStream getInputStream() throws IOException
{
    if (isClosed())
        throw new IOException("connection closed");
    return inputStream;
}

public final int getNextOutputStreamSeqnum()
{
    synchronized (seqnum_lock)
    {
        int next = seqnum++;
        return next;
    }
}
protected final java.io.OutputStream getOutputStream() throws IOException
{
    if (isClosed())
        throw new IOException("connection closed");
    return outputStream;
}

  public final boolean handle(Session sess, Command_rx_struct rxframe, final SNAC snac) throws org.jcq2k.MessagingNetworkException, java.io.IOException
  {
    synchronized (rxhandlers)
    {
        for (int i = 0; i < rxhandlers.size(); i++)
        {
            RxTriggerStruct trigger = (RxTriggerStruct) rxhandlers.elementAt(i);
            if (trigger.family == snac.family && trigger.subtype == snac.subtype)
            {
                trigger.rxHandler.triggered(sess, rxframe, snac);
                return true;
            }
        }
    }
    return false;
  }

public abstract boolean isClosed();
private void log(String s)
{
  CAT.debug("icq2k: " + s);
}
public final int read(byte[] b) throws IOException
{
    return read(b, 0, b.length);
}
public final int read(byte[] b, int ofs, int len) throws IOException
{
  org.jcq2k.util.Acme.Utils.readFullyWithTimeout(
    getInputStream(), b, ofs, len, ICQ2KMessagingNetwork.socketTimeoutMillis);
  return len;
}
public final void removeHandler(int snacFamily, int snacSubtype)
{
    synchronized (rxhandlers)
    {
        for (int i = 0; i < rxhandlers.size(); i++)
        {
            RxTriggerStruct trigger = (RxTriggerStruct) rxhandlers.elementAt(i);
            if (trigger.family == snacFamily && trigger.subtype == snacFamily)
            {
                rxhandlers.remove(trigger);
                return;
            }
        }
    }
}
public final void write(byte[] b) throws IOException
{
  org.jcq2k.util.joe.Log4jUtil.dump(CAT, "--- sending data ---------", b, Session.DBG_DUMP_PREFIX);
  getOutputStream().write(b);
}
}