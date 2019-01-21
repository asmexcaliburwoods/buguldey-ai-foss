package org.jcq2k.icq2k;

import java.io.*;
import java.net.*;
import java.util.*;
import org.jcq2k.*;
import org.jcq2k.util.*;
import org.jcq2k.util.joe.*;

/**
Represents a SNAC header (SNAC family, subtype,
request id, and flags) and subsequent SNAC data for
a packet that can be transmitted over the FLAP channel 2.

@see #send(Aim_conn_t).
*/
public final class SNAC
{
  public final int family;
  public final int subtype;
  public final long requestId;
  public final byte flag1;
  public final byte flag2;
  private final ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
/**
 * SNAC constructor comment.
 */
public SNAC(int family, int subtype) throws IOException
{
  this(family, subtype, 0, 0);
}
/**
 * SNAC constructor comment.
 */
public SNAC(int family, int subtype, int flag1, int flag2) throws IOException
{
  this(family, subtype, 0, 0, 0);
}
/**
 * SNAC constructor comment.
 */
public SNAC(int family, int subtype, int flag1, int flag2, long requestId) throws IOException
{
  this.family = family;
  this.subtype = subtype;
  this.flag1 = (byte) (flag1 & 0xff);
  this.flag2 = (byte) (flag2 & 0xff);
  this.requestId = requestId;
  addWord(family);
  addWord(subtype);
  addByte(flag1);
  addByte(flag2);
  addDWord(requestId);
}
/**
 * SNAC constructor comment.
 */
public void addByte(int byt) throws IOException
{
  byteArray.write((byte) ((byt) & 0xff));
}
/**
 * SNAC constructor comment.
 */
public void addByteArray(byte[] b) throws IOException
{
  if (b.length > 0)
    byteArray.write(b);
}
/**
 * SNAC constructor comment.
 */
public void addDWord(long x) throws IOException
{
  addWord(((int) ((x >> 16) & 0xffff)));
  addWord(((int) ((x) & 0xffff)));
}
/**
 * SNAC constructor comment.
 */
public void addStringPrependedWithByteLength(String s) throws IOException
{
  byte[] ba = Session.string2byteArray(s);
  addByte(ba.length);
  addByteArray(ba);
}
/**
 * SNAC constructor comment.
 */
public void addStringRaw(String s) throws IOException
{
  byte[] ba = Session.string2byteArray(s);
  addByteArray(ba);
}
/**
 * SNAC constructor comment.
 */
public void addTlv(int type, byte[] value) throws IOException
{
  addWord(type);
  addWord(value.length);
  addByteArray(value);
}
/**
 * SNAC constructor comment.
 */
public void addTlv(int type, String value) throws IOException
{
  addTlv(type, Session.string2byteArray(value));
}
/**
 * SNAC constructor comment.
 */
public void addTlvByte(int type, int byt) throws IOException
{
  addWord(type);
  addWord(1);
  addWord(byt);
}
/**
 * SNAC constructor comment.
 */
public void addTlvDWord(int type, long dword) throws IOException
{
  addWord(type);
  addWord(4);
  addDWord(dword);
}
/**
 * SNAC constructor comment.
 */
public void addTlvWord(int type, int word) throws IOException
{
  addWord(type);
  addWord(2);
  addWord(word);
}
/**
 * SNAC constructor comment.
 */
public void addWord(int x) throws IOException
{
  byteArray.write((byte) ((x >> 8) & 0xff));
  byteArray.write((byte) ((x) & 0xff));
}
public void addIcqUin(long uin) throws IOException
{
  addByteArray(new byte[] {
        (byte) (uin & 0xff), //
        (byte) ((uin >> 8) & 0xff), //
        (byte) ((uin >> 16) & 0xff), //
        (byte) ((uin >> 24) & 0xff)});
}
/**
Creates a channel 2 FLAP packet with this SNAC packet inside,
and sends it immediately using a given connection conn.
*/
public void send(Aim_conn_t conn) throws MessagingNetworkException, IOException
{
  byteArray.flush();
  byte[] flapData = byteArray.toByteArray();
  byteArray.close();
  byte[] flapHeader = new FLAPHeader(2, conn.getNextOutputStreamSeqnum(), flapData.length).byteArray;
  ByteArrayOutputStream bas  = new ByteArrayOutputStream(flapData.length+flapHeader.length);
  bas.write(flapHeader);
  bas.write(flapData);
  bas.flush();

  conn.write(bas.toByteArray());
  bas.close();
  conn.flush();
}
}