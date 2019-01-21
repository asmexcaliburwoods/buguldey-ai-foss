package org.jcq2k.proxy.socks5;

import java.io.*;
import java.net.*;
import java.util.*;
import org.jcq2k.util.Acme.Utils;

/**
Utility class that allows to create TCP/IP connections
using the external Socks5 proxy server.
*/
public final class Socks5Util
{
  public final static int SOCKS5_PROXY_DEFAULT_PORT = 1080;
/**
 * Attempts to connect to the specified host using
 * the specified socks5ProxyHostName settings.
 */
public final static Socket proxyConnect(String peerHostName, int peerPort, String socks5ProxyHostName, int socks5ProxyPort, int timeout)
throws UnknownHostException, IOException
{
  return proxyConnect(peerHostName, peerPort, InetAddress.getByName(socks5ProxyHostName), socks5ProxyPort, null, null, timeout);
}
/**
 * Attempts to connect to the specified host using
 * the specified socks5ProxyHostName settings.
 */
public final static Socket proxyConnect(String peerHostName, int peerPort, String socks5ProxyHostName, int socks5ProxyPort, String socks5ProxyUserName, String socks5ProxyUserPassword, int timeout)
throws UnknownHostException, IOException
{
  return proxyConnect(peerHostName, peerPort, InetAddress.getByName(socks5ProxyHostName), socks5ProxyPort, socks5ProxyUserName, socks5ProxyUserPassword, timeout);
}
/**
 * Attempts to connect to the specified host using
 * the specified socks5ProxyHostName settings.
 */
public final static Socket proxyConnect(String peerHostName, int peerPort, InetAddress socks5ProxyHost, int socks5ProxyPort, int timeout)
throws IOException
{
  return proxyConnect(peerHostName, peerPort, socks5ProxyHost, socks5ProxyPort, null, null, timeout);
}
/**
 * Attempts to connect to the specified host using
 * the specified socks5 settings.
 */
public final static Socket proxyConnect(String peerHostName, int peerPort, InetAddress socks5ProxyHost, int socks5ProxyPort, String socks5ProxyUserName, String socks5ProxyUserPassword, int timeout)
throws IOException
{
  if (socks5ProxyHost == null)
    throw new NullPointerException("socks5ProxyHost is null");
  if (peerHostName == null)
    throw new NullPointerException("peerHostName is null");
  byte[] peerHost = peerHostName.getBytes();
  if (peerHost.length > 255)
    throw new RuntimeException("peerHostName is too long (length must be <= 255).");
  byte[] userName = null;
  byte[] password = null;
  if (socks5ProxyUserName != null)
  {
    if (socks5ProxyUserPassword == null)
      throw new NullPointerException("socks5ProxyUserPassword is null.  It must be non-null when socks5ProxyUserName is non-null.");
    userName = socks5ProxyUserName.getBytes();
    if (userName.length > 255)
      throw new RuntimeException("socks5ProxyUserName is too long (length must be <= 255).");
    password = socks5ProxyUserPassword.getBytes();
    if (password.length > 255)
      throw new RuntimeException("socks5ProxyUserPassword is too long (length must be <= 255).");
  }

  //
  final byte[] buf = new byte[16];
  Socket socksSocket = new Socket(socks5ProxyHost, socks5ProxyPort);
  socksSocket.setSoTimeout(timeout);
  final ByteArrayOutputStream bas = new ByteArrayOutputStream(Math.max((userName == null ? 0 : 3 + userName.length + password.length), 7 + peerHost.length));
  int ofs;
  buf[0] = 0x05; //SOCKS version 5
  if (socks5ProxyUserName != null)
  {
    buf[1] = 0x02; //two methods
    buf[2] = 0x00; //method1: no authentication
    buf[3] = 0x02; //method2: username/password authentication
    ofs = 4;
  }
  else
  {
    buf[1] = 0x01;
    buf[2] = 0x00;
    ofs = 3;
  }
  try
  {
    OutputStream os = socksSocket.getOutputStream();
    os.write(buf, 0, ofs);
    InputStream is = socksSocket.getInputStream();
    Utils.readFullyWithTimeout(is, buf, 0, 2, timeout);
    if (buf[0] != 0x05)
      throw new IOException("socks proxy returned incorrect version (" + buf[0] + "), must be 5.");
    if (buf[1] == (byte) 0xff)
      throw new IOException("socks5 proxy returned error (username/password must be specified?).");
    if (buf[1] == 0x02)
    {
      //username authentication
      if (socks5ProxyUserName == null)
        throw new IOException("socks5 proxy server requires username/password auth (invalid proxy server?)");
      bas.write(1); //version 1
      bas.write((byte) userName.length);
      bas.write(userName);
      userName = null;
      bas.write((byte) password.length);
      bas.write(password);
      password = null;
      os.write(bas.toByteArray());
      Utils.readFullyWithTimeout(is, buf, 0, 2, timeout);
      if ((buf[0] != 0x01) || (buf[1] != 0x00))
        throw new IOException("socks5 proxy returned error (incorrect username/password?).");
      bas.reset();
    }
    bas.write(0x05);
    /* CONNECT */
    bas.write(0x01);
    /* reserved */
    bas.write(0x00);
    /* address type: host name */
    bas.write(0x03);
    bas.write((byte) peerHost.length);
    bas.write(peerHost);
    bas.write((byte) ((peerPort >> 8) & 0xff));
    bas.write((byte) (peerPort & 0xff));
    os.write(bas.toByteArray());
    Utils.readFullyWithTimeout(is, buf, 0, 10, timeout);
    if (buf[0] != 0x05)
      throw new IOException("socks proxy returned incorrect version (" + buf[0] + "), must be 5.");
    if (buf[1] != 0x00)
      throw new IOException("socks5 proxy returned error (cannot connect to peer host?).");
    return socksSocket;
  }
  catch (RuntimeException ex1)
  {
    try
    {
      socksSocket.close();
    }
    catch (Exception ex2)
    {
    }
    throw ex1;
  }
  catch (IOException ex)
  {
    try
    {
      socksSocket.close();
    }
    catch (Exception ex2)
    {
    }
    throw ex;
  }
}
}