package org.jcq2k.icq2k;

import java.io.*;
import java.util.*;

/**
Represents a single TCP/IP socket connection
with some functionality for dispatching
incoming OSCAR events.
*/
public final class SimpleTCPConnection extends Aim_conn_t
{
  private final static org.log4j.Category CAT = org.log4j.Category.getInstance(SimpleTCPConnection.class.getName());
  private java.net.Socket fd;
  private final Object lock2 = new Object();
/**
 Creates a new connection to the specified
 host of specified type.
 <br>
 <br>type: Type of connection to create
 <br>destination: Host to connect to (in "host:port" or "host" syntax)
 */
public SimpleTCPConnection(Session session, int connType, java.net.InetAddress host, int port, PluginContext ctx) throws org.jcq2k.MessagingNetworkException, java.io.IOException
{
  super(session, connType, host, port, ctx);
  java.net.Socket sok = proxyconnect(host, port, (int) ctx.getICQ2KMessagingNetwork().getSocketTimeoutMillis());
  synchronized (lock2)
  {
    fd = sok;
  }
  inputStream = sok.getInputStream();
  outputStream = new BufferedOutputStream(sok.getOutputStream(), 1024);
}
/**
Closes the connection.
<p>
Never throws Exceptions.
*/
public void closeSocket()
{
  java.net.Socket fd;
  synchronized (lock2)
  {
    fd = this.fd;
    this.fd = null;
  }
  if (fd == null)
    return;
  log("closing socket "+fd);
  try
  {
    fd.close();
  }
  catch (Exception ex)
  {
  CAT.debug("exc in java.net.Socket.close()", ex);
  }
}
public boolean isClosed()
{
  synchronized (lock2)
  {
    return fd == null;
  }
}
private void log(String s)
{
  CAT.debug("icq2k: " + s);
}
/**
 * Attempts to connect to the specified host via the configured
 * proxy settings, if present.  If no proxy is configured for
 * this session, the connection is done directly.
 */
private java.net.Socket proxyconnect(java.net.InetAddress host, int port, int timeout) throws org.jcq2k.MessagingNetworkException, java.io.IOException
{
  if (ICQ2KMessagingNetwork.socksProxyInetAddress == null)
  {
    log("connect: creating TCP connection to " + host + ":" + port + ", timeout: " + (timeout/1000) + " sec.");
    java.net.Socket sok = new java.net.Socket(host, port);
    sok.setSoTimeout(timeout);
    log("connect: socket created  [" + host + ":" + port + "], timeout: " + (timeout/1000) + " sec.");
    return sok;
  }
  else
  {
    log("socks5 connect: creating TCP connection to " + host + ":" + port + ", timeout: " + (timeout/1000) + " sec.");
    java.net.Socket sok = org.jcq2k.proxy.socks5.Socks5Util.proxyConnect(host.getHostAddress(), port, ICQ2KMessagingNetwork.socksProxyInetAddress, ICQ2KMessagingNetwork.socksProxyPort, ICQ2KMessagingNetwork.socksProxyUserName, ICQ2KMessagingNetwork.socksProxyPassword, timeout);
    log("socks5 connect: connection created  [" + host + ":" + port + "], timeout: " + (timeout/1000) + " sec.");
    return sok;
  }
}
}