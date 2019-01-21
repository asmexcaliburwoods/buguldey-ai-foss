package org.jcq2k.icq2k;

import java.io.*;
import java.net.*;
import java.util.*;
import org.jcq2k.util.joe.*;
import org.jcq2k.util.joe.jsync.*;
import org.jcq2k.util.*;
import org.jcq2k.*;
import org.log4j.Category;

/**
  Represents a ICQ2KMessagingNetwork session.
  <p>
  <b>Lifecycle.</b>
  <ul>
  <li><b>Creation.</b>
    Session is created while login().
  <li><b>Destruction.</b> Session is destroyed in these cases:
    <ul>
    <li>while logout(),
    <li>while setClientStatus(STATUS_OFFLINE),
    <li>when the IO error occured in the user's TCP/IP connection, or
    <li>when the user is disconnected by the server (socket closed).
    </ul>
  </ul>
  <p>
  Historically, the class contains 95% of the
  plugin functionality.
  <p>
  @see ServeSessionsThread
  @see tick(PluginContext)
  @see ICQ2KMessagingNetwork
*/

public class Session
implements  SNACFamilies,
            AIMConstants
{
  private final static org.log4j.Category CAT = org.log4j.Category.getInstance(Session.class.getName());

  private MessagingNetworkException lastError = null;

  /**
    When running, Session.tick() method is called periodically
    by the dispatch thread.
    When not running, Session.tick() method is never called.
    <p>
    @see #isRunning()
    */
  private boolean running = false;
  private boolean shuttingDown = false;
  private final Object shuttingDownLock = new Object();

  /**
    Session's login id (icq number).
    */
  private final String loginId;

  /**
    ICQ number.
    */
  private final int uin;

  /**
    Session's contact list items and their current status.
  */
  private Hashtable contactListUinInt2cli;

  /**
    Should never return null for contact list entries.
    Returning non-null means that the ContactListItem is in the contact list
    (possibly, with the offline status).
    Returning null means that the ContactListItem is not in the contact list.
  */
  public ContactListItem getContactListItem(String dstLoginId)
  {
    return getContactListItem(new Integer(dstLoginId));
  }

  private ContactListItem getContactListItem(Integer dstLoginId)
  {
    return (ContactListItem) contactListUinInt2cli.get(dstLoginId);
  }

  private int getContactListItemStatus(Integer dstLoginId)
  {
    ContactListItem cli = getContactListItem(dstLoginId);
    if (cli == null) return StatusUtil.OSCAR_STATUS_OFFLINE;
    else return cli.getStatusOscar();
  }

  public Enumeration getContactListItems()
  {
    return contactListUinInt2cli.elements();
  }

  /**
    Session's current status.
  */
  private int status_Oscar = StatusUtil.OSCAR_STATUS_OFFLINE;

  /**
    Connection to BOS (Basic OSCAR service) server.
    Is null when connection closed.
    */
  private Aim_conn_t bosconn;
  /**
    Connection to authorization/login server.
    Is null when the auth connection is closed.
    */
  private Aim_conn_t authconn;

  /**
    Lock object that is used to synchronize login and logout operations.
    */
  private final Object logoutLock = new Object();

  /**
    Next snac request id.
    */
  private long snac_nextid = 1; //C unsigned long

  /**
    Last time the keepAlive packet sent.  Initialized at login(...).
    Is not used if ICQ2KMessagingNetwork.isKeepAlivesUsed() is false.
    */
  private long lastKeepaliveMillis;

  ///**
  //Not used. Was used for for sendMessage0().
  //*/
  //final static byte[] IM_HTML_UNICODE_ENTITY_PREFIX = new byte[] {(byte) '&', (byte) '#'};

  ///**
  //Not used. Was used for for sendMessage0().
  //*/
  //final static byte[] IM_HTML_PREFIX = "<html>".getBytes();


  ///**
  //Not used. Was used for for sendMessage0().
  //*/
  //final static byte[] IM_HTML_POSTFIX = "</html>".getBytes();

  /**
    Prefix for debugging packet dumps.
    */
  public static final String DBG_DUMP_PREFIX = "icq: ";

  /**
  Delivery time of last RATE2_MAXIMUM_MSGCOUNT msgs, in milliseconds.
  Time is kept as several Long values.
  */
  private final Wheel rate2msgsSendTimeQueue = new Wheel();

  /**
    Creates new instance.
    <p>
    Properties config file is loaded in the static initializer.
  */
  public Session(String loginId) throws MessagingNetworkException
  {
    super();
    log("new Session");
    MLang.EXPECT_NOT_NULL_NOR_EMPTY(loginId, "loginId", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    this.loginId = loginId;
    //if (ICQ2KMessagingNetwork.ENFORCE_ICQ_VALIDATION)
    uin = MLang.parseInt(loginId, "loginId", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
  }

  /**
    Adds.  Throws MessagingNetworkException if not connected.
  */
  public void addToContactList(String dstLoginId, PluginContext ctx) throws MessagingNetworkException
  {
    log("addToContactList()");
    try
    {
      ASSERT_LOGGED_IN();

      int dstUin = MLang.parseInt_NonNegative(dstLoginId, "dstLoginId", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      Integer dstUin_ = new Integer(dstUin);
      if (getContactListItem(dstUin_) != null)
      {
        log(dstLoginId + " already in a contact list, ignored");
        return;
      }
      contactListUinInt2cli.put(dstUin_, makeContactListItem(dstLoginId));
      send_addSingleUserToContactList(dstLoginId);
    }
    catch (Exception ex)
    {
      handleException(ex, "addToContactList", ctx);
    }
  }

  private ContactListItem makeContactListItem(String dstLoginId)
  {
    return new ContactListItem(this, dstLoginId);
  }

  /**
    Grab a single command sequence off the socket, and enqueue
    it in the rx event queue in a seperate struct.
  */
  private void aim_get_command(Aim_conn_t conn, PluginContext ctx) throws MessagingNetworkException, java.io.IOException
  {
    Command_rx_struct newrx = null;
    Lang.ASSERT_NOT_NULL(conn, "conn");
    MLang.EXPECT(!conn.isClosed(), "connection must be open", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);

    // Rendezvous (client-client) connections do not speak
    // flap.
    if (conn.type == AIMConstants.AIM_CONN_TYPE_RENDEZVOUS)
    {
      log("aim_get_command(): RENDEZVOUS conn not supported, closing it.");
      conn.closeSocket();
      MLang.EXPECT_FALSE("RENDEZVOUS conn not supported", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      //return aim_get_command_rendezvous(sess, conn);
    }
    if (conn.type == AIMConstants.AIM_CONN_TYPE_RENDEZVOUS_OUT)
    {
      log("incoming data on RENDEZVOUS_OUT connection, ignored");
      return;
    }

    //let's read flap header.  Six bytes:
    //0 char  - Always 0x2a
    //1 char  - Channel ID.  Usually 2 -- 1 and 4 are used during login.
    //2 short - Seqnum
    //4 short - Number of data bytes that follow.
    byte[] flapHeader = new byte[6];
    int len = conn.read(flapHeader, 0, 6);
    if (len != 6)
    {
      log("aim_get_command(): closing conn: invalid FLAP header: length must be 6, but it is " + len);
      conn.closeSocket();
      replaceThrowMessagingNetworkException("invalid FLAP header: length must be 6, but it is " + len, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
    }
    try
    {
      MLang.EXPECT_EQUAL(flapHeader[0], 0x2a, "flapHeader[0]", "0x2a ('*')", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
    }
    catch (MessagingNetworkException ex)
    {
      log("aim_get_command(): closing conn: invalid FLAP header: must start with '*' (0x2a), but it starts with " + HexUtil.toHexString0x(flapHeader[0], 0xff, 2));
      conn.closeSocket();
      throw ex;
    }

    /* allocate a new rxstruct */
    newrx = new Command_rx_struct();
    newrx.hdrtype = AIMConstants.AIM_FRAMETYPE_OSCAR;
    newrx.hdr_oscar_type = flapHeader[1]; //channel id
    newrx.hdr_oscar_seqnum = aimutil_get16(flapHeader, 2); //seqnum
    int dataFieldLength = aimutil_get16(flapHeader, 4); //dataFieldLength
    //newrx.nofree = 0; //free by default //C refcounting stuff
    newrx.data = AllocUtil.createByteArray(this, dataFieldLength);

    /* read the data portion of the packet */
    try
    {
      if (conn.read(newrx.data, 0, dataFieldLength) < dataFieldLength)
        throw new java.io.IOException("unexpected end of stream while reading FLAP data field.");
    }
    catch (java.io.IOException ex)
    {
      CAT.error("aim_get_command(): closing conn: I/O exception while reading FLAP data field", ex);
      closeConnectionIfNotNull(conn);
      replaceThrowMessagingNetworkException("i/o error: " + ex, MessagingNetworkException.CATEGORY_LOGGED_OFF_DUE_TO_NETWORK_ERROR);
    }
    org.jcq2k.util.joe.Log4jUtil.dump(CAT, "recv rxframe: chan " + newrx.hdr_oscar_type + " seq " + HexUtil.toHexString0x(newrx.hdr_oscar_seqnum), newrx.data, DBG_DUMP_PREFIX);
    //
    Lang.ASSERT_NOT_NULL(newrx, "newrx");
    newrx.conn = conn;
    //
    handleRx(newrx, ctx);
  }
  /**
    Creates a new connection to the specified
    host of specified type.

    type: Type of connection to create
    destination: Host to connect to (in "host:port" or "host" syntax)
  */
  private Aim_conn_t aim_newconn(final int type, final String destination, PluginContext ctx) throws MessagingNetworkException, java.io.IOException, UnknownHostException
  {
    int port = AIMConstants.CFG_AIM_LOGIN_PORT_DEFAULT;
    String host = null;
    Lang.ASSERT_NOT_NULL_NOR_TRIMMED_EMPTY(destination, "destination");

    int colon = destination.lastIndexOf(':');
    host = destination;
    if (colon > -1)
    {
      try
      {
        port = Integer.parseInt(destination.substring(colon + 1));
        host = destination.substring(0, colon);
      }
      catch (NumberFormatException ex)
      {
        replaceThrowMessagingNetworkException("invalid port value, must be integer: \"" + destination + "\"", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      }
    }
    return aim_newconn(type, java.net.InetAddress.getByName(host), port, ctx);
  }
  /**
    Creates a new connection to the specified host of specified type.

    type: Type of connection to create
    destination: Host to connect to (in "host:port" or "host" syntax)
  */
  private Aim_conn_t aim_newconn(int type, java.net.InetAddress host, int port, PluginContext ctx) throws MessagingNetworkException, java.io.IOException
  {
    return ctx.getICQ2KMessagingNetwork().getResourceManager().createTCPConnection(this, type, host, port, ctx);
  }
  private int aim_putsnac(byte[] buf, int family, int subtype, int flags, long snacid)
  {
    int offset = 0; //0
    offset += aimutil_put16(buf, offset, family & 0xffff); //2
    offset += aimutil_put16(buf, offset, subtype & 0xffff); //4
    offset += aimutil_put16(buf, offset, flags & 0xffff); //6
    offset += aimutil_put32(buf, offset, snacid); //10
    return offset; //10
  }
  /**
    Writes a TLV with a two-byte integer value portion.
    buf: Destination buffer
    t: TLV type
    v: Value
  */
  private int aim_puttlv_16(byte[] buf, int offset, final int t, final int v)
  {
    int delta = 0;
    delta += aimutil_put16(buf, offset + delta, t);
    delta += aimutil_put16(buf, offset + delta, 2);
    delta += aimutil_put16(buf, offset + delta, v);
    return delta;
  }
  /**
    Writes a TLV with a two-byte integer value portion.
    buf: Destination buffer
    t: TLV type
    v: Value
  */
  private int aim_puttlv_32(byte[] buf, int offset, final int t, final long v)
  {
    int delta = 0;
    delta += aimutil_put16(buf, offset + delta, t);
    delta += aimutil_put16(buf, offset + delta, 2);
    delta += aimutil_put32(buf, offset + delta, v);
    return delta;
  }
  /**
    aim_puttlv_str - Write a string TLV.
    buf: Destination buffer
    tlv_type: TLV type
    l: Length of string
    s: String to write

    Writes a TLV with a string value portion.  (Only the first @l
    bytes of the passed string will be written, which should not
    include the terminating null.)
  */
  private int aim_puttlv_str_(byte buf[], final int offset, int tlv_type, byte[] stringAsByteArray)
  {
    int delta = 0;
    delta += aimutil_put16(buf, offset + delta, tlv_type);
    delta += aimutil_put16(buf, offset + delta, stringAsByteArray.length);
    System.arraycopy(stringAsByteArray, 0, buf, offset + delta, stringAsByteArray.length);
    delta += stringAsByteArray.length;
    return delta;
  }
  /**
    Reads and parses a series of TLV patterns from a data buffer.
    data: Input buffer
  */
  private Aim_tlvlist_t aim_readtlvchain(byte[] data, int ofs, int len) throws MessagingNetworkException
  {
    return new Aim_tlvlist_t(this, data, ofs, len);
  }
  private void aim_sendconnack(Aim_conn_t conn) throws MessagingNetworkException
  {
    Command_tx_struct newpacket = aim_tx_new(conn, AIMConstants.AIM_FRAMETYPE_OSCAR, 0x0001, 4);
    int cpos = 0;
    cpos += aimutil_put32(newpacket.data, cpos, 0x00000001);
    aim_tx_sendframe(newpacket);
  }
  private Command_tx_struct aim_tx_new(Aim_conn_t conn, short /* C unsigned char */ frameType, //
  int chan, int datalen) throws MessagingNetworkException
  {
    Lang.ASSERT_NOT_NULL(conn, "conn");
    if (conn.type == AIMConstants.AIM_CONN_TYPE_RENDEZVOUS || conn.type == AIMConstants.AIM_CONN_TYPE_RENDEZVOUS_OUT)
    {
      if (frameType != AIMConstants.AIM_FRAMETYPE_OFT)
        Lang.ASSERT_FALSE("aim_tx_new(): attempted to allocate inappropriate frame type\r\nfor rendezvous connection, frameType=" + frameType);
    }
    else
    {
      if (frameType != AIMConstants.AIM_FRAMETYPE_OSCAR)
        Lang.ASSERT_FALSE("aim_tx_new(): attempted to allocate inappropriate frame type\r\nfor flap connection, frameType=" + frameType);
    }
    Command_tx_struct newtx = new Command_tx_struct();
    newtx.conn = conn;
    newtx.data = AllocUtil.createByteArray(this, datalen);
    newtx.hdrtype = frameType;
    if (newtx.hdrtype == AIMConstants.AIM_FRAMETYPE_OSCAR)
    {
      newtx.hdr_oscar_type = (short) chan;
    }
    else
    {
      if (newtx.hdrtype == AIMConstants.AIM_FRAMETYPE_OFT)
      {
        newtx.hdr_oft_type = (short) chan;
        //newtx.hdr_oft_hdr2len = 0; //this will get setup by caller */
      }
      else
      {
        Lang.ASSERT_FALSE("aim_tx_new(): invalid frame type: " + frameType);
      }
    }
    return newtx;
  }
  private void aim_tx_sendframe(final Command_tx_struct txframe) throws MessagingNetworkException
  {
    int pak_length = 0;
    Lang.ASSERT_NOT_NULL(txframe, "txframe");
    StringBuffer dbgMsg = new StringBuffer("send txframe: ");
    Lang.ASSERT_NOT_NULL(txframe.conn, "txframe.conn");
    if (txframe.hdrtype == AIMConstants.AIM_FRAMETYPE_OSCAR)
    {
      txframe.hdr_oscar_seqnum = txframe.conn.getNextOutputStreamSeqnum();
    }
    final int hdrtype = txframe.hdrtype;

    //calculate packet length
    switch (hdrtype)
    {
      case AIMConstants.AIM_FRAMETYPE_OSCAR :
        pak_length = txframe.data.length + 6;
        dbgMsg.append(//
        "\r\ntype=oscar chan=" + txframe.hdr_oscar_type + //
        " seq=" + HexUtil.toHexString0x(txframe.hdr_oscar_seqnum) + //
        " snac_data_len=" + txframe.data.length + //
        " pak_length=" + pak_length);
        org.jcq2k.util.joe.Log4jUtil.dump(CAT, dbgMsg.toString(), txframe.data, DBG_DUMP_PREFIX + "tx ");
        break;
      case AIMConstants.AIM_FRAMETYPE_OFT :
        dbgMsg.append("type=oft   ");
        byte[] hdr_oft_hdr2 = txframe.hdr_oft_hdr2;
        Lang.ASSERT_NOT_NULL(hdr_oft_hdr2, "hdr_oft_hdr2 of txframe");
        pak_length = hdr_oft_hdr2.length + 8;
        dbgMsg.append("pak_length=" + pak_length);
        log(dbgMsg.toString());
        break;
      default :
        Lang.ASSERT_FALSE("aim_tx_sendframe(): invalid hdrtype: " + HexUtil.toHexString0x(hdrtype));
    }

    //allocate the raw packet
    Lang.ASSERT_NON_NEGATIVE(pak_length, "pak_length");
    byte[] pak = AllocUtil.createByteArray(this, pak_length);

    //feed it
    switch (hdrtype)
    {
      case AIMConstants.AIM_FRAMETYPE_OSCAR :
        /* byte 0: command byte */
        aimutil_put8(pak, 0, 0x2a);

        /* byte 1: type/family byte */
        aimutil_put8(pak, 1, txframe.hdr_oscar_type);

        /* bytes 2+3: word: flap seqnum */
        aimutil_put16(pak, 2, txframe.hdr_oscar_seqnum);

        /* bytes 4+5: word: snac length */
        aimutil_put16(pak, 4, txframe.data.length);

        /* bytes >= 6: raw snac data */
        System.arraycopy(txframe.data, 0, pak, 6, txframe.data.length);
        break;
      case AIMConstants.AIM_FRAMETYPE_OFT :
        int offset = 0;
        Lang.ASSERT_NOT_NULL(txframe.hdr_oft_magic, "txframe.hdr_oft_magic");
        offset += aimutil_put8(pak, offset, txframe.hdr_oft_magic[0]);
        offset += aimutil_put8(pak, offset, txframe.hdr_oft_magic[1]);
        offset += aimutil_put8(pak, offset, txframe.hdr_oft_magic[2]);
        offset += aimutil_put8(pak, offset, txframe.hdr_oft_magic[3]);
        offset += aimutil_put16(pak, offset, txframe.hdr_oft_hdr2.length + 8);
        offset += aimutil_put16(pak, offset, txframe.hdr_oft_type);
        System.arraycopy(txframe.hdr_oft_hdr2, 0, pak, offset, txframe.hdr_oft_hdr2.length);
        break;
      default :
        Lang.ASSERT_FALSE("aim_tx_sendframe(): invalid hdrtype: " + HexUtil.toHexString0x(hdrtype));
    }

    //* For OSCAR, a full raw packet is now in pak.
    //* For OFT, just the bloated header is in pak,
    //* since OFT allows us to do the data in a different write (yay!).
    if (hdrtype != AIMConstants.AIM_FRAMETYPE_OSCAR)
      org.jcq2k.util.joe.Log4jUtil.dump(CAT, "sending data", pak, DBG_DUMP_PREFIX);
    try
    {
      txframe.conn.write(pak);
      txframe.conn.flush();
    }
    catch (java.io.IOException ex)
    {
      CAT.error("aim_tx_sendframe(): closing conn: I/O exception while sending", ex);
      closeConnectionIfNotNull(txframe.conn);
      return; //bail out
    }
    if (txframe.hdrtype == AIMConstants.AIM_FRAMETYPE_OFT && txframe.data.length != 0)
    {
      //for (int cpos = 0; cpos < txframe.commandlen; cpos++)
      //faimdprintf(sess, 0, "%02x ", txframe.data[cpos]);
      try
      {
        Lang.ASSERT_NOT_NULL(txframe.data, "txframe.data");
        org.jcq2k.util.joe.Log4jUtil.dump(CAT, "sending data portion of the packet", txframe.data, DBG_DUMP_PREFIX);
        txframe.conn.write(txframe.data);
        txframe.conn.flush();
      }
      catch (java.io.IOException ex)
      {
        log("aim_tx_sendframe(): closing conn: I/O exception while sending");
        closeConnectionIfNotNull(txframe.conn);
        return;
      }
    }
  }
  public static int aimutil_get16(byte[] buf, int offset)
  {
    int val;
    val = (buf[offset] << 8) & 0xff00;
    val |= (buf[++offset]) & 0xff;
    return val;
  }
  private long aimutil_get32(byte[] buf, int offset)
  {
    long val;
    val = (buf[  offset] << 24) & 0xff000000;
    val|= (buf[++offset] << 16) & 0x00ff0000;
    val|= (buf[++offset] <<  8) & 0x0000ff00;
    val|= (buf[++offset]      ) & 0x000000ff;
    return val;
  }

  private long aimutil_getIcqUin(byte[] buf, int offset)
  {
    //be 5c 94 01  //uin  //"BE 5C 94 01" aka 0x01945CBE == 26500286
    long val;
    val = (buf[  offset]      ) & 0x000000ff;
    val|= (buf[++offset] <<  8) & 0x0000ff00;
    val|= (buf[++offset] << 16) & 0x00ff0000;
    val|= (buf[++offset] << 24) & 0xff000000;
    return val;
  }


  public static int aimutil_get8(byte[] buf, int offset)
  {
    return ((int)(buf[offset])) & 0xff;
  }
  public byte[] aimutil_getByteArray(byte[] buf, int offset) throws MessagingNetworkException
  {
    return aimutil_getByteArray(buf, offset+1, aimutil_get8(buf, offset));
  }
  public byte[] aimutil_getByteArray(byte[] buf, int offset, int length) throws MessagingNetworkException
  {
    byte[] b = AllocUtil.createByteArray(this, length);
    System.arraycopy(buf, offset, b, 0, b.length);
    return b;
  }
  public String aimutil_getString(byte[] buf, int offset) throws MessagingNetworkException
  {
    byte[] b = aimutil_getByteArray(buf, offset);
    return byteArray2string(b);
  }
  //i += aimutil_put16(newrx.data, i, 0x01);
  private int aimutil_put16(byte[] buf, int offset, int a)
  {
    buf[offset] = (byte) ((a >> 8) & 0xff);
    buf[++offset] = (byte) (a & 0xff);
    return 2;
  }
  //i += aimutil_put16(newrx.data, i, 0x01);
  private int aimutil_put32(byte[] buf, int offset, long a)
  {
    buf[offset] = (byte) ((a >> 24) & 0xff);
    buf[++offset] = (byte) ((a >> 16) & 0xff);
    buf[++offset] = (byte) ((a >> 8) & 0xff);
    buf[++offset] = (byte) (a & 0xff);
    return 4;
  }
  //i += aimutil_put8(newrx.data, i, 0x01);
  private int aimutil_put8(byte[] buf, int offset, int a)
  {
    buf[offset] = (byte) (a & 0xff);
    return 1;
  }
  //i += aimutil_putstr(newrx.data, i, "0", 1);
  private int aimutil_putstr(byte[] data, int offset, String s)
  {
    Lang.ASSERT_NOT_NULL(s, "s");
    return aimutil_putstr(data, offset, s, s.length());
  }
  //i += aimutil_putstr(newrx.data, i, "0", 1);
  private int aimutil_putstr(byte[] data, int offset, String s, int length)
  {
    Lang.ASSERT_NOT_NULL(s, "s");
    byte[] src = string2byteArray(s);
    System.arraycopy(src, 0, data, offset, length);
    return length;
  }


  private void ASSERT_LOGGED_IN() throws MessagingNetworkException
  {
    MLang.EXPECT(status_Oscar != StatusUtil.OSCAR_STATUS_OFFLINE, "status cannot be offline to perform this operation", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
  }


  /*
    Converts a byte array into string.
  */
  public static String byteArray2string(byte[] ba)
  {
    return byteArray2string(ba, 0, ba.length);
  }

  private static String fileEncoding = System.getProperty("file.encoding");

  /*
    Converts portion of a byte array into string.
  */
  public static String byteArray2string(byte[] ba, int ofs, int len)
  {
    if (len == 0) return "";

    //changed by Antich to test encoding problems - TEMPORARY SOLUTION!!
    try
    {
      CAT.debug("Constructing message using encoding " + fileEncoding);
      return new String(ba, ofs, len, fileEncoding);
    }
    catch (Exception e)
    {
      CAT.debug("Constructing message, reverted to default enc", e);
      return new String(ba, ofs, len);
    }
  }

  /*
    Converts byte array into string using some rules.
    Also, replaces 0xFE string delimiters to CRLFs.
  */
  public static String byteArray2stringConvertXfes(byte[] ba)
  {
    StringBuffer sb = new StringBuffer(ba.length + 10);
    int chunkStart = 0;
    int chunkLen = 0;
    for (int i = 0; i < ba.length; i++)
    {
      byte b = ba[i];
      if (b != (byte) 0xFE)
      {
        chunkLen++;
        continue;
      }
      else
      {
        sb.append(byteArray2string(ba, chunkStart, chunkLen));
        i++;
        for (; i < ba.length; i++)
        {
          if (ba[i] != 0xFE)
          {
            i--;
            break;
          }
        }
        chunkStart = i + 1;
        chunkLen = 0;
        if (i >= ba.length)
          break;
        sb.append("\r\n");
      }
    }
    if (chunkLen > 0)
      sb.append(byteArray2string(ba, chunkStart, chunkLen));
    return sb.toString();
  }

  /*
  Closes a connection if it is non-null, just returns otherwise.
  Never throws Exceptions.
  */
  private static void closeConnectionIfNotNull(Aim_conn_t conn)
  {
    if (conn != null)
      conn.closeSocket();
  }
  private boolean consumesnac(Command_rx_struct rx) throws MessagingNetworkException, java.io.IOException
  {
    int family = -2;
    int subtype = -2;
    int flag1 = -2;
    int flag2 = -2;
    long snacid = -2;

    if (rx.data.length >= 2)
      family = aimutil_get16(rx.data, 0);
    if (rx.data.length >= 4)
      subtype = aimutil_get16(rx.data, 2);
    if (rx.data.length >= 5)
      flag1 = aimutil_get8(rx.data, 4);
    if (rx.data.length >= 6)
      flag2 = aimutil_get8(rx.data, 5);
    if (rx.data.length >= 10)
      snacid = aimutil_get32(rx.data, 6);
    //
    return rx.conn.handle(this, rx, new SNAC(family, subtype, flag1, flag2, snacid));
  }
  protected void handleException(Throwable tr, String processName, PluginContext ctx) throws MessagingNetworkException
  {
    CAT.error("error while "+processName, tr);
    MessagingNetworkException ex;

    if (tr instanceof MessagingNetworkException)
    {
      ex = (MessagingNetworkException) tr;
    }
    else
    if (tr instanceof UnknownHostException)
      ex = new MessagingNetworkException("DNS error resolving "+tr.getMessage(), MessagingNetworkException.CATEGORY_LOGGED_OFF_DUE_TO_NETWORK_ERROR);
    else
    if (tr instanceof java.io.IOException)
      ex = new MessagingNetworkException("I/O error: "+tr.getMessage(), MessagingNetworkException.CATEGORY_LOGGED_OFF_DUE_TO_NETWORK_ERROR);
    else
      ex = new MessagingNetworkException("unknown error: "+tr.getMessage(), MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);

    if (ex.getCategory() != MessagingNetworkException.CATEGORY_STILL_CONNECTED)
    {
      setLastError(ex);
      shutdown(ctx, lastError.getCategory(), lastError.getMessage());
    }

    throw new MessagingNetworkException("Error while " + processName + ": " + ex.getMessage(), ex.getCategory());
  }

  private final Object lastErrorLock = new Object();

  void replaceThrowMessagingNetworkException(String reason, int cat) throws MessagingNetworkException
  {
    replaceThrowMessagingNetworkException(new MessagingNetworkException(reason, cat));
  }

  void replaceThrowMessagingNetworkException(MessagingNetworkException newEx) throws MessagingNetworkException
  {
    synchronized (lastErrorLock)
    {
      if (lastError != null)
      {
        CAT.debug("exception replaced", newEx);
        throw lastError;
      }
      else
      {
        lastError = newEx;
        throw newEx;
      }
    }
  }


  void setLastError(MessagingNetworkException newEx)
  {
    synchronized (lastErrorLock)
    {
      if (lastError == null)
        lastError = newEx;
    }
  }
  /*
   * Converts text into ASCII7 HTML.
   *
   * Also converts any non-7bit chars to UNICODE HTML entities of the form "&#2026;".
   */
  private static byte[] encodeAsAscii7Html(final String text)
  {
    //try
    //{
      //if (text.length() == 0)
        //return AllocUtil.createByteArray(this, ] {};
      //java.io.ByteArrayOutputStream bas = new java.io.ByteArrayOutputStream(text.length());
      ////bas.write(IM_HTML_PREFIX);
      //char[] chars = text.toCharArray();
      //for (int i = 0; i < chars.length; i++)
      //{
        //final int unicodeChar = chars[i];
        //if ((unicodeChar & 0xff80) == 0)
        //{
          //bas.write((byte) unicodeChar);
          //continue;
        //}
        //bas.write(IM_HTML_UNICODE_ENTITY_PREFIX); //&#
        //bas.write(HexUtil.HEX_DIGITS_BYTES[ (unicodeChar) & 15]);
        //bas.write(HexUtil.HEX_DIGITS_BYTES[ (unicodeChar << 8) & 15]);
        //bas.write(HexUtil.HEX_DIGITS_BYTES[ (unicodeChar << 16) & 15]);
        //bas.write(HexUtil.HEX_DIGITS_BYTES[ (unicodeChar << 24) & 15]);
        //bas.write((byte) ';');
      //}
      ////bas.write(IM_HTML_POSTFIX);
      //return bas.toByteArray();
    //}
    //catch (java.io.IOException ex)
    //{
    //CAT.error(ex.getMessage(), ex);
      //return text.getBytes();
    //}
    return text.getBytes();
  }
  private void fireErrorMessage(String errorMessage, PluginContext context)
  {
    log("fireErrorMessage: " + errorMessage);
    try
    {
      context.getICQ2KMessagingNetwork().fireMessageReceived("0", loginId, errorMessage);
    }
    catch (Exception ex)
    {
      CAT.error("error while firing errorMessage to m n listeners", ex);
    }
  }
  private Aim_conn_t getAuthConnNotNull() throws MessagingNetworkException
  {
    if (Thread.currentThread().isInterrupted())
      replaceThrowMessagingNetworkException("thread interrupted", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    Aim_conn_t conn;
    synchronized (logoutLock)
    {
      conn = authconn;
    }
    if (conn == null)
      replaceThrowMessagingNetworkException("connection closed", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    return conn;
  }
  private Aim_conn_t getBosConnNotNull() throws MessagingNetworkException
  {
    if (Thread.currentThread().isInterrupted())
      replaceThrowMessagingNetworkException("thread interrupted", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    Aim_conn_t conn;
    synchronized (logoutLock)
    {
      conn = bosconn;
    }
    if (conn == null)
      replaceThrowMessagingNetworkException("connection closed", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    return conn;
  }
  public int getContactStatus_Oscar(String dstLoginId, PluginContext ctx) throws MessagingNetworkException
  {
    log("getContactStatus_Oscar");
    try
    {
      int dstUin = MLang.parseInt_NonNegative(dstLoginId, "dstLoginId", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      Integer dstUin_ = new Integer(dstUin);
      return getContactListItemStatus(dstUin_);
    }
    catch (Exception ex)
    {
      handleException(ex, "getContactStatus", ctx);
      return StatusUtil.OSCAR_STATUS_OFFLINE;
    }
  }
    /**
     * Insert the method's description here.
     * Creation date: (29.03.01 14:14:05)
     * @return java.lang.String
     */
    public final java.lang.String getLoginId() {
      return loginId;
    }
  /**
     * Insert the method's description here.
     * Creation date: (29.03.01 14:14:05)
     * @return int
     */
  public int getStatus_Oscar()
  {
    return status_Oscar;
  }
  private byte[] getStringAsByteArr(byte[] buf, int offset) throws MessagingNetworkException
  {
    int len = aimutil_get16(buf, offset);
    MLang.EXPECT(len < 32*1024, "len must be less than 32*1024, but it is "+len, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
    byte[] str = AllocUtil.createByteArray(this, len);
    System.arraycopy(buf, offset+2, str, 0, len);
    return buf;
  }
  private void handleAuthorizationError_alwaysThrowEx(byte[] flapDataField, byte flapChannel, PluginContext ctx) throws MessagingNetworkException
  {
    if (flapChannel == 4)
    {
      int errCode = -1;
      String errUrl = null;
      String screenName = null;
      Aim_tlvlist_t tlvlist = new Aim_tlvlist_t(this, flapDataField);
      screenName = tlvlist.getNthTlvOfTypeAsString(1, 1);
      if (tlvlist.getNthTlvOfType(1, 8) != null)
        errCode = tlvlist.getNthTlvOfTypeAs16Bit(1, 8);
      errUrl = tlvlist.getNthTlvOfTypeAsString(1, 4);
      log("rx_handleAuthorizationError: channel 4: shutting down, errUrl=\"" + errUrl + "\", errCode=" + errCode);
      String reason = "Unknown authorization error";
      switch (errCode)
      {
        case 0x0001 :
          reason = "Invalid or not registered ICQ number.";
          break;
        case 0x0005 :
          reason = "Invalid password or ICQ number.";
          break;
        case 0x0018 :
          reason = "ICQ server reports: connect rate exceeded.  You are reconnecting too often.  Try to connect again 10 or 20 minutes later.";
          break;
        default :
          reason += ": channel=4, errCode=" + HexUtil.toHexString0x(errCode);
          if (errUrl != null)
            reason += ", " + errUrl;
          break;
      }
      String errmsg = "Cannot login: " + reason;
      shutdown(ctx, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR, errmsg);
      replaceThrowMessagingNetworkException(errmsg, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
    }
    else
      if (flapChannel == 2)
      {
        int snacFamily = -1;
        int snacSubtype = -1;
        if (flapDataField.length >= 4)
        {
          snacFamily = aimutil_get16(flapDataField, 0);
          snacSubtype = aimutil_get16(flapDataField, 2);
        }
        log("rx_handleAuthorizationError: channel 2: shutting down, unknown auth response (snac: " + HexUtil.toHexString0x(snacFamily) + "/" + HexUtil.toHexString0x(snacSubtype) + ")");
        shutdown(ctx, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR, "icq protocol violation");
        replaceThrowMessagingNetworkException("Cannot login: unknown auth response on a channel 2.  Possibly, ICQ number too long.", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      }
      else
      {
        log("rx_handleAuthorizationError: shutting down, unknown auth response on unknown channel=" + flapChannel);
        shutdown(ctx, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR, "icq protocol violation");
        replaceThrowMessagingNetworkException("Cannot login: unknown auth response on flap channel " + flapChannel + ".", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      }
  }
  private void handleIncomingAuthRequest(String senderLoginId, final PluginContext ctx) throws MessagingNetworkException, java.io.IOException
  {
    log("incoming authrequest");
    sendAuthResponsePositive(senderLoginId, ctx);
  }

  /**
    Called by tick() when some connection had incoming data.
  */
  private void handleIncomingData(Aim_conn_t conn, PluginContext ctx) throws MessagingNetworkException, java.io.IOException
  {
    try
    {
    if (conn.type == AIMConstants.AIM_CONN_TYPE_RENDEZVOUS_OUT)
    {
      log("handleIncomingData(): closing conn: RENDEZVOUS_OUT not supported.");
      closeConnectionIfNotNull(conn);
      replaceThrowMessagingNetworkException("RENDEZVOUS_OUT not supported", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
    }
    else
    {
      try
      {
        aim_get_command(conn, ctx);
      }
      catch (MessagingNetworkException ex12)
      {
        CAT.error("handleIncomingData() exception, shutting down", ex12);
        shutdown(ctx, ex12.getCategory(), ex12.getCategoryMessage());
        throw ex12;
      }
      //aim_rxdispatch();
    }
    }
    catch (MessagingNetworkException ex)
    {
      Aim_conn_t bosconn = this.bosconn;
      if (bosconn == null || bosconn.isClosed())
      {
        CAT.error("BOS connection does not exist anymore, shutting down. [1, ex]", ex);
        shutdown(ctx, ex.getCategory(), ex.getCategoryMessage());
        log("session logged out. [1, ex]");
      }
      throw ex;
    }
    Aim_conn_t bosconn = this.bosconn;
    if (bosconn == null || bosconn.isClosed())
    {
      log("BOS connection does not exist anymore, shutting down. [2, normal]");
      shutdown(ctx, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED, null);
      log("session logged out. [2, normal]");
    }
  }
  private void handleRx(Command_rx_struct rxframe, PluginContext ctx)
  {
    try
    {
      Lang.ASSERT_NOT_NULL(rxframe.data, "rxframe.data");
      log("handleRx(): " + //
        (rxframe.data.length >= 4 ? //
          "snac f/s=" + HexUtil.toHexString0x(aimutil_get16(rxframe.data, 0)) + "/" + HexUtil.toHexString0x(aimutil_get16(rxframe.data, 2)) + ", "
          : ""
        ) +
        "seq=" + HexUtil.toHexString0x(rxframe.hdr_oscar_seqnum) + ", " +
        "flapdata len=" + HexUtil.toHexString0x(rxframe.data.length)
        );

      if (((rxframe.hdrtype == AIMConstants.AIM_FRAMETYPE_OFT) && (rxframe.conn.type != AIMConstants.AIM_CONN_TYPE_RENDEZVOUS)) //
        || ((rxframe.hdrtype == AIMConstants.AIM_FRAMETYPE_OSCAR) && (rxframe.conn.type == AIMConstants.AIM_CONN_TYPE_RENDEZVOUS)))
      {
        log("handleRx(): incompatible rxframe type " + rxframe.hdrtype + " on conn type " + HexUtil.toHexString0x(rxframe.conn.type) + ", rxframe ignored");
        return;
      }

      if (rxframe.conn.type == AIMConstants.AIM_CONN_TYPE_RENDEZVOUS)
      {
        // make sure that we only get OFT frames on these connections
        if (rxframe.hdrtype != AIMConstants.AIM_FRAMETYPE_OFT)
        {
          log("handleRx(): non-OFT frames (of type " + HexUtil.toHexString0x(rxframe.hdrtype) + ") on OFT connection, rxframe ignored");
        }
        else
        {
          log("handleRx(): OFT rxframe, rxframe ignored");
        }
        return;
      }

      //
      if (rxframe.conn.type == AIMConstants.AIM_CONN_TYPE_RENDEZVOUS_OUT)
      {
        log("handleRx(): RENDEZVOUS_OUT connection, rxframe ignored");
        return;
      }
      //if ((rxframe.data.length == 4) && (aimutil_get32(rxframe.data, 0) == 0x00000001))
      //{
      //try
      //{
      //rx_handleFlapVersion(rxframe);
      //}
      //catch (MessagingNetworkException ex)
      //{
      //CAT.error(ex.getMessage(), ex);
      //}
      //
      //return;
      //}
      if (rxframe.hdr_oscar_type == 0x04)
      {
        rx_handle_negchan_middle(rxframe, ctx);
        return;
      }
      if (consumesnac(rxframe))
        return;
      if (rxdispatch_BIG_SWITCH(rxframe, ctx))
        return;
      log("handleRx(): unhandled packet, rxframe ignored");
    }
    catch (Exception ex)
    {
      CAT.debug("ex in handleRx", ex);
    }
  }
  /**
  This read-only attribute indicates if this Session's
  tick(PluginContext) method should be called by
  ServeSessionsThread's incoming data dispatch loop.
  <p>
  If and only if the Session is running, tick(PluginContext)
  will be called.
  <p>
  This method returns false while login sequence, and
  after session death, and returns true between these
  Session lifetime events.

  @see #tick(PluginContext)
  @see ServeSessionsThread
  */
  public boolean isRunning()
  {
    synchronized (logoutLock)
    {
      return running;
    }
  }
  protected void log(String s)
  {
    CAT.debug("icq2k [" + loginId + "]: " + s);
  }
  public void login_Oscar(final String password, String[] contactList, int status_Oscar, PluginContext ctx) throws MessagingNetworkException
  {
    log("login() start");
    final String ERRMSG_PREFIX = "Error logging in: ";
    try
    {
      synchronized (shuttingDownLock)
      {
        shuttingDown = false;
      }
      synchronized (lastErrorLock)
      {
        lastError = null;
      }
      MLang.EXPECT_NOT_NULL_NOR_EMPTY(password, "password", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      MLang.EXPECT(password.length() < 256, "password.length() must be < 256, but it is "+password.length(), MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      Lang.ASSERT(status_Oscar != StatusUtil.OSCAR_STATUS_OFFLINE, "StatusUtil.OSCAR_STATUS_OFFLINE should never happen as a login_Oscar() argument.");
      //MLang.ASSERT_IS_STATUS(status, "status");

      if (contactList == null)
      {
        contactList = new String[] {};
      }

      //
      if (contactListUinInt2cli == null)
      {
        contactListUinInt2cli = new Hashtable(contactList.length);
        for (int i = 0; i < contactList.length; i++)
        {
          String dstLoginId = contactList[i];
          MLang.EXPECT(dstLoginId.length() < 256, "dstLoginId.length() must be < 256, but it is "+dstLoginId.length(), MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
          if (dstLoginId == null)
          {
            continue;
          }
          int dstUin = MLang.parseInt_NonNegative(dstLoginId, "contactList[" + i + "]", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
          Integer dstUin_ = new Integer(dstUin);
          if (contactListUinInt2cli.put(dstUin_, makeContactListItem(dstLoginId)) != null)
          {
            CAT.debug("" + dstLoginId + " already in a contact list, ignored");
            continue;
          }
        }
      }
      synchronized (logoutLock)
      {
        Lang.ASSERT_NOT_NULL(loginId, "loginId");
        Lang.ASSERT_NOT_NULL(password, "password");
        snac_nextid = 1;
      }

      {
        Aim_conn_t conn = aim_newconn(AIMConstants.AIM_CONN_TYPE_AUTH, ICQ2KMessagingNetwork.getLoginServerInetAddress(), ICQ2KMessagingNetwork.getLoginServerPort(), ctx);
        Aim_conn_t oldConn;
        synchronized (logoutLock)
        {
          oldConn = authconn;
          authconn = conn;
        }
        if (oldConn != null)
        {
          oldConn.closeSocket();
        }
      }

      //
      byte[] flap_header = new byte[6];
      int len = getAuthConnNotNull().read(flap_header);
      log("recv flap_header, len: " + len);
      MLang.EXPECT_EQUAL(len, 6, "len", "FLAP header length", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      FLAPHeader flap = new FLAPHeader(flap_header);

      //
      byte[] flap_data_field = AllocUtil.createByteArray(this, flap.data_field_length);
      len = getAuthConnNotNull().read(flap_data_field);
      org.jcq2k.util.joe.Log4jUtil.dump(CAT, "recv flap_data_field, len: " + HexUtil.toHexString0x(len), flap_data_field, DBG_DUMP_PREFIX);
      MLang.EXPECT_EQUAL(len, flap_data_field.length, "len", "flap_data_field.length", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);

      MLang.EXPECT(flap_data_field.length >= 4, "hello length must be >= 4, but it is " + flap_data_field.length, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      long hello = aimutil_get32(flap_data_field, 0);
      MLang.EXPECT_EQUAL(hello, 1, "flap version", "00 00 00 01", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      log("server hello received, sending login request");
      loginSequence_10sendLogin(contactList, loginId, password, ctx);
      loginSequence_20waitForServerReady(ctx);
      loginSequence_40sendContactList(contactList, ctx);

      if (status_Oscar == StatusUtil.OSCAR_STATUS_ONLINE)
        setStatus_Oscar_Internal(status_Oscar, false, ctx, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED, null);
      else
        setStatus_Oscar_Internal(status_Oscar, true, ctx, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED, null);

      if (ctx.getICQ2KMessagingNetwork().isKeepAlivesUsed())
        lastKeepaliveMillis = System.currentTimeMillis();

      //loginSequence_50waitForWwwAolComPacket(ctx);
      getBosConnNotNull().setRateControlOn(true);
      setRunning(true);
      log("login() finished (success)");
    }
    catch (Exception ex)
    {
      log("login() finished (failed)");
      handleException(ex, "login", ctx);
    }
  }

  private void logInfo(String s)
  {
    CAT.info("icq2k [" + loginId + "]: " + s);
  }

  /** This is the initial login request packet */
  private void loginSequence_10sendLogin(final String[] contactList, String loginId, String password, PluginContext ctx) throws MessagingNetworkException, java.io.IOException
  {
    log("entered loginSequence_10sendLogin(), password="+StringUtil.toPrintableString(password));
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(password, "icq password");
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(loginId, "loginId");
    final byte[] password_b = string2byteArray(password);
    final byte[] xor_table = {//
      (byte) 0xf3, (byte) 0x26, (byte) 0x81, (byte) 0xc4, //
      (byte) 0x39, (byte) 0x86, (byte) 0xdb, (byte) 0x92 //
    };
    int xorindex = 0;
    for (int i = 0; i < password_b.length; i++)
    {
      password_b[i] ^= xor_table[xorindex++];
      if (xorindex >= xor_table.length)
        xorindex = 0;
    }
    final byte[] sn = string2byteArray(loginId);

    log("login, stage 10: encoded password length = " + password_b.length);

    byte[] pak = new byte[6 + 0x7e + password_b.length - 1 + sn.length - 8];
    int ofs = 0;
    //0000: 2a 01
    ofs += aimutil_put8(pak, ofs, 0x2a);
    ofs += aimutil_put8(pak, ofs, 1);
    int seq = getAuthConnNotNull().getNextOutputStreamSeqnum();
    //00 01
    ofs += aimutil_put16(pak, ofs, seq);
    //00 7d
    ofs += aimutil_put16(pak, ofs, 0x7e + password_b.length - 1 + sn.length - 8);
    //00 00 - 00 01
    ofs += aimutil_put32(pak, ofs, 0x00000001);
    //00 01 / 00 08 / "12345678"
    ofs += aim_puttlv_str_(pak, ofs, 1, sn);
    //00 02 / 00 01 / 0xxx
    ofs += aim_puttlv_str_(pak, ofs, 2, password_b);
    //00  03 / 00 32 / 41 "A"
    //0002: 4f 4c 20 49  6e 73 74 61 - 6e 74 20 4d  65 73 73 65 "OL I nsta nt M esse"
    //0003: 6e 67 65 72  20 28 53 4d - 29 2c 20 76  65 72 73 69 "nger  (SM ), v ersi"
    //0004: 6f 6e 20 33  2e 35 2e 31 - 36 37 30 2f  57 49 4e 33 "on 3 .5.1 670/ WIN3"
    //0005: 32 "2"
    ofs += aim_puttlv_str_(pak, ofs, 3, //
    string2byteArray("ICQ Inc. - Product of ICQ (TM).2000b.4.63.1.3279.85"));
    final byte[] trail = {(byte) 0x00, (byte) 0x16, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x0A, (byte) 0x00, (byte) 0x17, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x18, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x3F, (byte) 0x00, (byte) 0x19, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x1A, (byte) 0x00, (byte) 0x02, (byte) 0x0C, (byte) 0xCF, (byte) 0x00, (byte) 0x14, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x55, (byte) 0x00, (byte) 0x0F, (byte) 0x00, (byte) 0x02, (byte) 0x65, (byte) 0x6E, (byte) 0x00, (byte) 0x0E, (byte) 0x00, (byte) 0x02, (byte) 0x75, (byte) 0x73};
    Lang.ASSERT_EQUAL(ofs + trail.length, pak.length, "ofs + trail.length", "pak.length");
    System.arraycopy(trail, 0, pak, ofs, trail.length);

    log("login, stage 10: sending login packet, seq=" + HexUtil.toHexString0x(seq));
    //CAT.dump("", pak, DBG_DUMP_PREFIX);
    getAuthConnNotNull().write(pak);
    getAuthConnNotNull().flush();

    byte[] flap_header = new byte[6];
    int len = getAuthConnNotNull().read(flap_header);
    MLang.EXPECT_EQUAL(len, 6, "len", "FLAP header length", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
    final FLAPHeader flap = new FLAPHeader(flap_header);
    org.jcq2k.util.joe.Log4jUtil.dump(CAT, "recv flap_header, len: " + HexUtil.toHexString0x(len), flap_header, DBG_DUMP_PREFIX);

    //
    String bos_hostport;
    byte[] cookie;

    //
    byte[] auth_response = new byte[flap.data_field_length];
    len = getAuthConnNotNull().read(auth_response);
    org.jcq2k.util.joe.Log4jUtil.dump(CAT, "recv flap_data, len: " + len, auth_response, DBG_DUMP_PREFIX);
    MLang.EXPECT_EQUAL(len, auth_response.length, "len", "auth_response.length", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);

    //flap channel
    if (flap_header[1] != 4)
    {
      handleAuthorizationError_alwaysThrowEx(auth_response, flap_header[1], ctx);
      //unreachable code
      //handleAuthorizationError_alwaysThrowEx does always throw a MessagingNetworkException
      //this return is to fool the VisualAge.
      return;
    }

    //
    Aim_tlvlist_t resp = new Aim_tlvlist_t(this, auth_response);
    //String loginId = resp.getNthTlvOfTypeAsString(1, 1);
    bos_hostport = resp.getNthTlvOfTypeAsString(1, 5);
    cookie = resp.getNthTlvOfTypeAsByteArray(1, 6);

    //
    if (cookie == null || bos_hostport == null)
    {
      handleAuthorizationError_alwaysThrowEx(auth_response, flap_header[1], ctx);
    }

    //
    log("aim_send_login(): cookie received, closing auth connection...");

    {
      Aim_conn_t oldconn;
      synchronized (logoutLock)
      {
        oldconn = authconn;
        authconn = null;
      }
      closeConnectionIfNotNull(oldconn);
    }
    synchronized (shuttingDownLock)
    {
      if (shuttingDown)
        replaceThrowMessagingNetworkException("logged out", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    }
    log("aim_send_login(): auth connection closed, connecting to bos...");

    {
      Aim_conn_t conn = aim_newconn(AIMConstants.AIM_CONN_TYPE_BOS, bos_hostport, ctx);
      Aim_conn_t oldconn;
      synchronized (logoutLock)
      {
        oldconn = bosconn;
        bosconn = conn;
      }
      closeConnectionIfNotNull(oldconn);
    }

    synchronized (shuttingDownLock)
    {
      if (shuttingDown)
        replaceThrowMessagingNetworkException("logged out", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    }

    byte[] chipsa_flapHeader = new FLAPHeader(1, getBosConnNotNull().getNextOutputStreamSeqnum(), 4 + 4 + cookie.length).byteArray;
    byte[] xxx = new byte[8];
    int offset = 0;
    offset += aimutil_put16(xxx, offset, 0x0000);
    offset += aimutil_put16(xxx, offset, 0x0001);
    offset += aimutil_put16(xxx, offset, 0x0006);
    offset += aimutil_put16(xxx, offset, cookie.length);
    getBosConnNotNull().write(chipsa_flapHeader);
    getBosConnNotNull().write(xxx);
    getBosConnNotNull().write(cookie);
    getBosConnNotNull().flush();

    // now, response of SNAC fam/subtype 0001/0003 means "authorized".
    // we send a contact list
    //
    //
    //
    //final byte[] password_b = string2byteArray(password);
    //final byte[] sn = string2byteArray(loginId);
    //Command_tx_struct txframe = aim_tx_new(getBosConnNotNull(), AIMConstants.AIM_FRAMETYPE_OSCAR, 0x0002, 1152);
    //txframe.lock = 1;
    //int offset;
    //txframe.hdr_oscar_type = (short) (((this.flags & AIMConstants.AIM_SESS_FLAGS_SNACLOGIN) != 0) ? 0x02 : 0x01);
    //if ((this.flags & AIMConstants.AIM_SESS_FLAGS_SNACLOGIN) != 0)
    //offset = aim_putsnac(txframe.data, 0x0017, 0x0002, 0x0000, 0x00010000);
    //else
    //{
    //offset = aimutil_put16(txframe.data, 0, 0x0000);
    //offset += aimutil_put16(txframe.data, offset, 0x0001);
    //}
    //offset += aim_puttlv_str_(txframe.data, offset, 0x0001, sn);
    //if ((this.flags & AIMConstants.AIM_SESS_FLAGS_SNACLOGIN) != 0)
    //{
    //byte[] digest = new byte[16];
    //aim_encode_password_md5(password_b, key, digest);
    //offset += aim_puttlv_str_(txframe.data, offset, 0x0025, digest);
    //}
    //else
    //{
    //byte[] password_encoded = new byte[password_b.length];
    //aim_encode_password(password_b, password_encoded);
    //offset += aim_puttlv_str_(txframe.data, offset, 0x0002, password_encoded);
    //}
    //offset += aim_puttlv_str_(txframe.data, offset, 0x0003, clientinfo.clientstring);
    //if ((this.flags & AIMConstants.AIM_SESS_FLAGS_SNACLOGIN) != 0)
    //{
    //offset += aim_puttlv_16(txframe.data, offset, 0x0016, clientinfo.major2);
    //offset += aim_puttlv_16(txframe.data, offset, 0x0017, clientinfo.major);
    //offset += aim_puttlv_16(txframe.data, offset, 0x0018, clientinfo.minor);
    //offset += aim_puttlv_16(txframe.data, offset, 0x0019, clientinfo.minor2);
    //offset += aim_puttlv_16(txframe.data, offset, 0x001a, clientinfo.build);
    //}
    //else
    //{
    ///* Use very specific version numbers, to further indicate the hack. */
    //offset += aim_puttlv_16(txframe.data, offset, 0x0016, 0x010a);
    //offset += aim_puttlv_16(txframe.data, offset, 0x0017, 0x0004);
    //offset += aim_puttlv_16(txframe.data, offset, 0x0018, 0x003c);
    //offset += aim_puttlv_16(txframe.data, offset, 0x0019, 0x0001);
    //offset += aim_puttlv_16(txframe.data, offset, 0x001a, 0x0cce);
    //offset += aim_puttlv_32(txframe.data, offset, 0x0014, 0x00000055);
    //}
    //offset += aim_puttlv_str_(txframe.data, offset, 0x000e, clientinfo.country);
    //offset += aim_puttlv_str_(txframe.data, offset, 0x000f, clientinfo.lang);
    //if ((this.flags & AIMConstants.AIM_SESS_FLAGS_SNACLOGIN) != 0)
    //{
    //offset += aim_puttlv_32(txframe.data, offset, 0x0014, clientinfo.unknown);
    //offset += aim_puttlv_16(txframe.data, offset, 0x0009, 0x0015);
    //}
    ////txframe.commandlen = offset;
    //txframe.lock = 0;
    //aim_tx_enqueue(txframe);
    log("exited loginSequence_10sendLogin()");
  }
  private void loginSequence_20waitForServerReady(PluginContext ctx) throws java.io.IOException, MessagingNetworkException
  {
    log("entered loginSequence_20waitForServerReady()");
    Aim_conn_t bosconn = this.bosconn;
    Lang.ASSERT_NOT_NULL(bosconn, "bosconn");
    long stopTime = System.currentTimeMillis() + ctx.getICQ2KMessagingNetwork().getServerResponseTimeoutMillis();

    for (;;)
    {
      if (Thread.currentThread().isInterrupted())
        throw new InterruptedIOException();
      shutdownAt(stopTime, "waitForServerReady", ctx);

      byte[] flap_header = new byte[6];
      int len = bosconn.read(flap_header);
      log("recv flap_header, len: " + len);
      MLang.EXPECT_EQUAL(len, 6, "len", "FLAP header length", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      FLAPHeader flap = new FLAPHeader(flap_header);

      //
      byte[] flap_data_field = AllocUtil.createByteArray(this, flap.data_field_length);
      len = bosconn.read(flap_data_field);
      log("recv flap_data_field, len: " + len);
      MLang.EXPECT_EQUAL(len, flap_data_field.length, "len", "flap_data_field.length", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      if (flap.channel == 4)
      {
        rx_handleConnectionError(flap_data_field, false, ctx);
        continue;
      }
      if (flap.channel != 2 && flap_data_field.length < 4)
        continue;
      int family = aimutil_get16(flap_data_field, 0);
      int subtype = aimutil_get16(flap_data_field, 2);
      if ((family != 0 && subtype == 1) || (family == 9 && subtype == 9))
      {
        rx_handleGenericServiceError(flap_data_field, ctx);
        continue;
      }
      if (family != AIM_CB_FAM_GEN /*1*/|| subtype != AIM_CB_GEN_SERVERREADY /*3*/)
        continue;
      log("exited loginSequence_20waitForServerReady()");
      return;
    }
  }
  private void loginSequence_40sendContactList(final String[] contactList, final PluginContext ctx) throws java.io.IOException, MessagingNetworkException
  {
    SNAC p = null;
    log("entered loginSequence_40sendContactList()");
    getBosConnNotNull().removeHandler(0001, 0003);
    getBosConnNotNull().addHandler(new RxHandler()
    {
      public void triggered(Session s, Command_rx_struct rxframe, SNAC snac) throws java.io.IOException, MessagingNetworkException
      {
        log("loginSequence_40sendContactList(): memrequest 1/0x1f received, sending memrequest reply, 1/0x20");
        getBosConnNotNull().removeHandler(1, 0x1f);
        //
        //memrequest
        //data at 0x00001000 (0 bytes) of requested
        //aim_callhandler: calling for 0001/001f
        //Fri Jun  8 19:24:41 NOVST 2001  faimtest: memrequest: unable to use AIM binary ("(
        //null)/(null)"), sending defaults...
        //--- sending data --------------
        //0000: 2a 02 00 10  00 1c 00 01 - 00 20 00 00  00 00 00 04 "*... .... . .. ...."
        //0001: 00 10 d4 1d  8c d9 8f 00 - b2 04 e9 80  09 98 ec f8 ".... .... .... ...."
        //0002: 42 7e                 "B~"
        //
        SNAC p1 = new SNAC(1, 0x20, 0, 0, snac_nextid++);
        p1.addByteArray(new byte[] {(byte) 0x00, (byte) 0x10, (byte) 0xd4, (byte) 0x1d, (byte) 0x8c, (byte) 0xd9, (byte) 0x8f, (byte) 0x00, (byte) 0xb2, (byte) 0x04, (byte) 0xe9, (byte) 0x80, (byte) 0x09, (byte) 0x98, (byte) 0xec, (byte) 0xf8, (byte) 0x42, (byte) 0x7e});
        p1.send(getBosConnNotNull());
      }
    }, 1, 0x1f); //memrequest

    /*
      getBosConnNotNull().addHandler(new RxHandler()
      {
      public void triggered(Session s, Command_rx_struct rxframe, SNAC snac) throws java.io.IOException, MessagingNetworkException
      {
      if (snac.flag1 == 0 && snac.flag2 == 1)
      {
      //offline msg
      log("loginSequence_40sendContactList(): offline msg received");
      parseOfflineMessage(rxframe, ctx);
      }
      else
      {
      if (snac.flag1 == 0 && snac.flag2 == 0 && snac.requestId == 0x00010002)
      {
      //offline msgs done
      log("loginSequence_40sendContactList(): offline msgs done, sending ack offline msgs");
      getBosConnNotNull().removeHandler(0x15, 3);
      //2A 02 10 B8 00 19 //srv snds
      //00 15 00 03 //offline msgs done?
      //00 00
      //00 01 00 02 //reqid
      //00 01 00 0B
      //  09
      //    00
      //    BE 5C 94 01  42 00 02 00 00  .........._\".B.

      //
      //2A 02 0A BC 00 18  //clt snds
      //00 15 00 02 //ack offline msgs?
      //00 00
      //00 05 00 02 //reqid
      //00 01 00 0A
      //  08
      //    00
      //    BE 5C 94 01  3E 00 06 00  ............_\".>...
      Aim_tlvlist_t tlvlist = new Aim_tlvlist_t(this, rxframe.data, 10, rxframe.data.length - 10);
      byte[] offline_msg_cookie_block = tlvlist.getNthTlvOfTypeAsByteArray(1, 1);

      MLang.EXPECT_NOT_NULL(offline_msg_cookie_block, "offline_msg_cookie_block", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);

      MLang.EXPECT(offline_msg_cookie_block.length > 2, "offline_msg_cookie_block > 2", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      int length = 4; //aimutil_get8(offline_msg_cookie_block, 0);
      //MLang.EXPECT_POSITIVE(length, "offline_msg_cookie_block.length", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      //MLang.EXPECT(length < 32, "offline_msg_cookie_block.length < 32", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      MLang.EXPECT(2 + length <= offline_msg_cookie_block.length, "2+length <= offline_msg_cookie_block.length", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      long offline_msg_cookie = aimutil_get32(offline_msg_cookie_block, 2);
      SNAC ack = new SNAC(0x15, 2, 0, 0, 0x00050002);
      ack.addByteArray(new byte[] {00, 01, 00, 0x0A, 8, 00});
      ack.addDWord(offline_msg_cookie);
      ack.addByteArray(new byte[] {0x3E, 00, 06, 00});
      ack.send(getBosConnNotNull());
      ack = null;
      }
      else
      {
        log("got unknown 0x15/3 packet, ignored");
      }
        }
      }
      }, 0x15, 3); //offline msg?/offline msgs done?
    */

    log("loginSequence_40sendContactList(): sending 1/0x17, generic service controls/unknown");
    p = new SNAC(0x0001, 0x0017, 0, 0, 0x17);
    p.addByteArray(new byte[] {00, 01, 00, 03, 00, 02, 00, 01, 00, 03, 00, 01, 00, (byte) 0x15, //
      00, 01, 00, 04, 00, 01, 00, 06, 00, 01, 00, 0x09, 00, 01, 00, (byte) 0x0A, 00, 01
    });
    p.send(getBosConnNotNull());
    p = null;

    /*
      //getBosConnNotNull().addHandler(new RxHandler()
      //{
      //public void triggered(Session s, Command_rx_struct rxframe, SNAC snac) throws java.io.IOException, MessagingNetworkException
      //{
      //getBosConnNotNull().removeHandler(1, 7); //rate info response
      //log("loginSequence_40sendContactList(): got rate info response, sending 1/8, rate info ack");
      //SNAC p = new SNAC(1, 8, 0, 0, 8);
      //p.addByteArray(new byte[] {00, 01, 00, 02, 00, 03, 00, 04, 00, 05});
      //p.send(getBosConnNotNull());
      //}
      //}
      //, 1, 7); //rate info response
    */

    log("loginSequence_40sendContactList(): (skipping) 1/6, rate info request");

    /*
      //log("loginSequence_40sendContactList(): sending 1/6, rate info request");
      //p = new SNAC(0x0001, 0x0006, 0, 0, 6);
      //p.send(getBosConnNotNull());
      //p = null;
    */

    log("loginSequence_40sendContactList(): sending 1/0xe, generic service controls/Request information on my screen name");
    p = new SNAC(0x0001, 0x000e, 0, 0, 0xe);
    p.send(getBosConnNotNull());

    log("loginSequence_40sendContactList(): sending 2/2, location/Request rights information");
    p = new SNAC(0x0002, 0x0002, 0, 0, 2);
    p.send(getBosConnNotNull());

    log("loginSequence_40sendContactList(): sending 3/2, buddylist/Request rights information");
    p = new SNAC(0x0003, 0x0002, 0, 0, 2);
    p.send(getBosConnNotNull());

    log("loginSequence_40sendContactList(): sending 4/4, messaging/Request parameter information");
    p = new SNAC(0x0004, 0x0004, 0, 0, 0x000004);
    p.send(getBosConnNotNull());

    log("loginSequence_40sendContactList(): sending 9/2, bos-specific/Request BOS Rights");
    p = new SNAC(0x0009, 0x0002, 0, 0, 2);
    p.send(getBosConnNotNull());

    log("loginSequence_40sendContactList(): sending 4/2, Messaging/Add ICBM parameter");
    p = new SNAC(0x0004, 0x0002, 0, 0, 2);
    p.addByteArray(new byte[] {00, 00, 00, 00, 00, 03, 0x1F, 0x40, 0x03, (byte) 0xE7, 0x03, //
    (byte) 0xE7, 0x00, 0x00, 0x00, 0x00});
    p.send(getBosConnNotNull());

    log("loginSequence_40sendContactList(): sending 2/4, Location Services/Set user information");
    p = new SNAC(0x0002, 0x0004, 0, 0, 4);
    p.addByteArray(new byte[] {(byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x20, (byte) 0x09, //
    (byte) 0x46, (byte) 0x13, (byte) 0x49, (byte) 0x4C, (byte) 0x7F, (byte) 0x11, (byte) 0xD1, //
    (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45, (byte) 0x53, (byte) 0x54, (byte) 0x00, //
    (byte) 0x00, (byte) 0x09, (byte) 0x46, (byte) 0x13, (byte) 0x44, (byte) 0x4C, (byte) 0x7F, //
    (byte) 0x11, (byte) 0xD1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45, (byte) 0x53, //
    (byte) 0x54, (byte) 0x00, (byte) 0x00}); //0x01 94 5C BE == 26500286
    p.send(getBosConnNotNull());
    p = null;

    /*
      getBosConnNotNull().addHandler(new RxHandler()
      {
      public void triggered(Session s, Command_rx_struct rxframe, Aim_modsnac_t snac) throws java.io.IOException, MessagingNetworkException
      {
      getBosConnNotNull().removeHandler(9, 3);
      //faimtest_bosrights
      //aim_callhandler: calling for 0009/0003
      //Fri Jun  8 19:24:41 NOVST 2001  faimtest: faimtest: BOS rights: Max permit = 160 /
      //Max deny = 160
      //Fri Jun  8 19:24:41 NOVST 2001  faimtest: faimtest: officially connected to BOS.
      //
      //aim_bos_clientready(sess, command->getBosConnNotNull());
      //--- sending data --------------
      //0000: 2a 02 00 0f  00 52 00 01 - 00 02 00 00  00 00 00 03 "*... .R.. .... ...."
      //0001: 00 01 00 03  00 04 06 86 - 00 02 00 01  00 04 00 01 ".... .... .... ...."
      //0002: 00 03 00 01  00 04 00 01 - 00 04 00 01  00 04 00 01 ".... .... .... ...."
      //0003: 00 06 00 01  00 04 00 01 - 00 08 00 01  00 04 00 01 ".... .... .... ...."
      //0004: 00 09 00 01  00 04 00 01 - 00 0a 00 01  00 04 00 01 ".... .... .... ...."
      //0005: 00 0b 00 01  00 04 00 01 -        ".... ...."
      snac_nextid = 3;
      SNAC p = new SNAC(0x0001, 0x0002, 0, 0, snac_nextid++);
      p.addByteArray(new byte[] {(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x06, (byte) 0x86, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0b, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01});
      p.send(getBosConnNotNull());
      p = null;
      connected = true;
      }
      }
      , 9, 3);
      getBosConnNotNull().addHandler(new RxHandler()
      {
      public void triggered(Session s, Command_rx_struct rxframe, Aim_modsnac_t snac) throws java.io.IOException, MessagingNetworkException
      {
      getBosConnNotNull().removeHandler(1, 7);
      //aim_callhandler: calling for 0001/0007
      //Rate Information Response Acknowledge
      //aim_bos_ackrateresp(sess, command->getBosConnNotNull());  /* ack rate info response
      //--- sending data --------------
      //0000: 2a 02 00 04  00 14 00 01 - 00 08 00 00  00 00 00 00 "*... .... .... ...."
      //0001: 00 01 00 02  00 03 00 04 - 00 05        ".... .... .."
      //
      SNAC p = new SNAC(0x0001, 0x0008);
      p.addByteArray(new byte[] {(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x05});
      p.send(getBosConnNotNull());
      p = null;
      //
      //aim_bos_reqpersonalinfo(sess, command->getBosConnNotNull()); //aim_genericreq_n(sess, getBosConnNotNull(), 0x0001, 0x000e);
      //--- sending data --------------
      //0000: 2a 02 00 05  00 0a 00 01 - 00 0e 00 00  00 00 00 00 "*... .... .... ...."
      p = new SNAC(0x0001, 0x000e);
      p.send(getBosConnNotNull());
      //aim_bos_reqlocaterights(sess, command->getBosConnNotNull());
      //--- sending data --------------
      //0000: 2a 02 00 06  00 0a 00 02 - 00 02 00 00  00 00 00 00 "*... .... .... ...."
      p = new SNAC(0x0002, 0x0002);
      p.send(getBosConnNotNull());
      p = null;
      //
      //aim_bos_setprofile(sess, command->getBosConnNotNull(), profile, NULL, AIM_CAPS_BUDDYICON | AIM_CAPS_CHAT | AIM_CAPS_GETFILE | AIM_CAPS_SENDFILE | AIM_CAPS_IMIMAGE /*| AIM_CAPS_GAMES | AIM_CAPS_SAVESTOCKS);
      //--- sending data --------------
      //0000: 2a 02 00 07  01 35 00 02 - 00 04 00 00  00 00 00 02 "*... .5.. .... ...."
      //0001: 00 01 00 1f  74 65 78 74 - 2f 78 2d 61  6f 6c 72 74 ".... text /x-a olrt"
      //0002: 66 3b 20 63  68 61 72 73 - 65 74 3d 22  75 73 2d 61 "f; c hars et=" us-a"
      //0003: 73 63 69 00  02 00 89 48 - 65 6c 6c 6f  2e 3c 62 72 "sci. ...H ello .<br"
      //0004: 3e 4d 79 20  63 61 70 74 - 61 69 6e 20  69 73 20 28 ">My  capt ain  is ("
      //0005: 6e 75 6c 6c  29 2e 20 20 - 54 68 65 79  20 77 65 72 "null ).   They  wer"
      //0006: 65 20 64 75  6d 62 20 65 - 6e 6f 75 67  68 20 74 6f "e du mb e noug h to"
      //0007: 20 6c 65 61  76 65 20 74 - 68 69 73 20  6d 65 73 73 " lea ve t his  mess"
      //0008: 61 67 65 20  69 6e 20 74 - 68 65 69 72  20 63 6c 69 "age  in t heir  cli"
      //0009: 65 6e 74 2c  20 6f 72 20 - 74 68 65 79  20 61 72 65 "ent,  or  they  are"
      //000a: 20 75 73 69  6e 67 20 66 - 61 69 6d 74  65 73 74 2e " usi ng f aimt est."
      //000b: 20 20 53 68  61 6d 65 20 - 6f 6e 20 74  68 65 6d 2e "  Sh ame  on t hem."
      //000c: 00 03 00 1f  74 65 78 74 - 2f 78 2d 61  6f 6c 72 74 ".... text /x-a olrt"
      //000d: 66 3b 20 63  68 61 72 73 - 65 74 3d 22  75 73 2d 61 "f; c hars et=" us-a"
      //000e: 73 63 69 00  04 00 00 00 - 05 00 50 09  46 13 46 4c "sci. .... ..P. F.FL"
      //000f: 7f 11 d1 82  22 44 45 53 - 54 00 00 09  46 13 45 4c "... "DES T... F.EL"
      //0010: 7f 11 d1 82  22 44 45 53 - 54 00 00 74  8f 24 20 62 "... "DES T..t .$ b"
      //0011: 87 11 d1 82  22 44 45 53 - 54 00 00 09  46 13 48 4c ".... "DES T... F.HL"
      //0012: 7f 11 d1 82  22 44 45 53 - 54 00 00 09  46 13 43 4c "... "DES T... F.CL"
      //0013: 7f 11 d1 82  22 44 45 53 - 54 00 00     "... "DES T.."
      //
      p = new SNAC(0x0002, 0x0004, 0, 0, 2);
      p.addTlv(1, "text/x-aolrtf; charset=\"us-ascii\"");
      p.addTlv(2, "Profile");
      p.addTlv(3, "text/x-aolrtf; charset=\"us-ascii\"");
      p.addTlv(4, ""); //away message
      //Capability information
      //info.c::aim_putcap(...)
      p.addTlv(5, new byte[] {(byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x50, (byte) 0x09, (byte) 0x46, (byte) 0x13, (byte) 0x46, (byte) 0x4c, (byte) 0x7f, (byte) 0x11, (byte) 0xd1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45, (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x46, (byte) 0x13, (byte) 0x45, (byte) 0x4c, (byte) 0x7f, (byte) 0x11, (byte) 0xd1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45, (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x74, (byte) 0x8f, (byte) 0x24, (byte) 0x20, (byte) 0x62, (byte) 0x87, (byte) 0x11, (byte) 0xd1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45, (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x46, (byte) 0x13, (byte) 0x48, (byte) 0x4c, (byte) 0x7f, (byte) 0x11, (byte) 0xd1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45, (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x46, (byte) 0x13, (byte) 0x43, (byte) 0x4c, (byte) 0x7f, (byte) 0x11, (byte) 0xd1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45, (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00});
      p.send(getBosConnNotNull());
      p = null;

      //aim_bos_reqbuddyrights(sess, command->getBosConnNotNull());
      //--- sending data --------------
      //0000: 2a 02 00 08  00 0a 00 03 - 00 02 00 00  00 00 00 00 "*... .... .... ...."
      p = new SNAC(0x0003, 0x0002);
      p.send(getBosConnNotNull());
      p = null;

      ///* send the buddy list and profile (required, even if empty)
      //aim_bos_setbuddylist(sess, command->getBosConnNotNull(), buddies);
      //
      //Add buddy [Source: Client]
      //Adds a number of buddies to your buddy list, causing AIM to send
      //us on/off events for the given users. Len/buddy combinations can be
      //repeated as many times as you have buddies to add.
      //
      //SNAC Information:
      //
      //Family 0x0003
      //SubType 0x0004
      //Flags {0x00, 0x00}
      //
      //--------------------------------------------------------------------------------
      //
      //RAW / Buddy name length (byte)
      //RAW / Buddy name
      java.io.ByteArrayOutputStream ba = new java.io.ByteArrayOutputStream();
      for (int i = 0; i < contactList.length; i++)
      {
      byte[] buddyName = string2byteArray(contactList[i]);
      MLang.EXPECT(buddyName.length > 0 && buddyName.length < 256, "buddy name is too long or too short.\r\nDetails: length must be 0..255, but it is " + buddyName.length + ". Buddy name: '" + contactList[i] + "'.", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      ba.write((byte) buddyName.length);
      ba.write(buddyName);
      }
      ba.flush();
      byte[] baa = ba.toByteArray();
      ba.close();
      log("loginSequence_40sendContactList(): sending contact list");
      p = new SNAC(0x0003, 0x0004);
      p.addByteArray(baa);
      p.send(getBosConnNotNull());
      p = null;
      ba = null;
      baa = null;

      ///* dont really know what this does
      //aim_addicbmparam(sess, command->getBosConnNotNull());
      //--- sending data --------------
      //0000: 2a 02 00 0a  00 1a 00 04 - 00 02 00 00  00 00 00 00 "*... .... .... ...."
      //0001: 00 00 00 00  00 03 1f 40 - 03 e7 03 e7  00 00 00 00 ".... ...@ .... ...."
      p = new SNAC(0x0004, 0x0002);
      p.addByteArray(new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x1f, (byte) 0x40, (byte) 0x03, (byte) 0xe7, (byte) 0x03, (byte) 0xe7, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
      p.send(getBosConnNotNull());
      p = null;

      //aim_bos_reqicbmparaminfo(sess, command->getBosConnNotNull());
      //--- sending data --------------
      //0000: 2a 02 00 0b  00 0a 00 04 - 00 04 00 00  00 00 00 00 "*... .... .... ...."
      p = new SNAC(0x0004, 0x0004);
      p.send(getBosConnNotNull());
      p = null;

      //aim_bos_reqrights(sess, command->getBosConnNotNull());
      //--- sending data --------------
      //0000: 2a 02 00 0c  00 0a 00 09 - 00 02 00 00  00 00 00 00 "*... .... .... ...."
      p = new SNAC(0x0009, 0x0002);
      p.send(getBosConnNotNull());
      p = null;

      ///* set group permissions -- all user classes
      //aim_bos_setgroupperm(sess, command->getBosConnNotNull(), AIM_FLAG_ALLUSERS);
      //--- sending data --------------
      //0000: 2a 02 00 0d  00 0e 00 09 - 00 04 00 00  00 00 00 00 "*... .... .... ...."
      //0001: 00 00 00 1f           "...."
      p = new SNAC(0x0009, 0x0004);
      p.addDWord(0x1f);
      p.send(getBosConnNotNull());
      p = null;

      //aim_bos_setprivacyflags(sess, command->getBosConnNotNull(), AIM_PRIVFLAGS_ALLOWIDLE);
      //--- sending data --------------
      //0000: 2a 02 00 0e  00 0e 00 01 - 00 14 00 00  00 00 00 00 "*... .... .... ...."
      //0001: 00 00 00 01           "...."
      p = new SNAC(0x0001, 0x0014);
      p.addDWord(0x1);
      p.send(getBosConnNotNull());
      p = null;
      }
      }
      , 1, 7);
      //
      //aim_callhandler: calling for 0001/0003
      //Fri Jun  8 19:24:38 NOVST 2001  faimtest: faimtest: SNAC families supported by thi
      //s host (type 2): 0x0001 0x0002 0x0003 0x0004 0x0006 0x0008 0x0009 0x000a 0x000b 0x
      //000c 0x0013 0x0015
      //Fri Jun  8 19:24:38 NOVST 2001  faimtest: faimtest: done with BOS ServerReady
      //aim_setversions(sess, command->getBosConnNotNull());
      //--- sending data --------------
      //0000: 2a 02 00 02  00 3a 00 01 - 00 17 00 00  00 00 00 01 "*... .:.. .... ...."
      //0001: 00 01 00 03  00 02 00 01 - 00 03 00 01  00 04 00 01 ".... .... .... ...."
      //0002: 00 06 00 01  00 08 00 01 - 00 09 00 01  00 0a 00 01 ".... .... .... ...."
      //0003: 00 0b 00 02  00 0c 00 01 - 00 13 00 01  00 15 00 01 ".... .... .... ...."
      //
      SNAC p = new SNAC(0x0001, 0x0017, 0, 0, 1);
      p.addByteArray(new byte[] {(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0b, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x0c, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x13, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x15, (byte) 0x00, (byte) 0x01});
      p.send(getBosConnNotNull());
      p = null;
      //
      //aim_bos_reqrate(sess, command->getBosConnNotNull()); /* request rate info
      //--- sending data --------------
      //0000: 2a 02 00 03  00 0a 00 01 - 00 06 00 00  00 00 00 00 "*... .... .... ...."
      //
      p = new SNAC(0x0001, 0x0006);
      p.send(getBosConnNotNull());
      p = null;
    */
    /*
      // send the buddy list and profile (required, even if empty)
      //aim_bos_setbuddylist(sess, command->getBosConnNotNull(), buddies);
      //
      //Add buddy [Source: Client]
      //Adds a number of buddies to your buddy list, causing AIM to send
      //us on/off events for the given users. Len/buddy combinations can be
      //repeated as many times as you have buddies to add.
      //
      //SNAC Information:
      //
      //Family 0x0003
      //SubType 0x0004
      //Flags {0x00, 0x00}
      //
      //--------------------------------------------------------------------------------
      //
      //RAW / Buddy name length (byte)
      //RAW / Buddy name
    */
    java.io.ByteArrayOutputStream ba = new java.io.ByteArrayOutputStream();
    for (int i = 0; i < contactList.length; i++)
    {
      byte[] buddyName = string2byteArray(contactList[i]);
      MLang.EXPECT(buddyName.length > 0 && buddyName.length < 256, "buddy name is too long or too short.\r\nDetails: length must be 0..255, but it is " + buddyName.length + ". Buddy name: '" + contactList[i] + "'.", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      ba.write((byte) buddyName.length);
      ba.write(buddyName);
    }
    ba.flush();
    byte[] baa = ba.toByteArray();
    ba.close();

    log("loginSequence_40sendContactList(): sending 3/4, send contact list");
    snac_nextid = 4;
    p = new SNAC(0x0003, 0x0004, 0, 0, snac_nextid++);
    p.addByteArray(baa);
    p.send(getBosConnNotNull());
    ba = null;
    baa = null;

    log("loginSequence_40sendContactList(): sending 9/7, BOS-specific/Add deny list entries");
    snac_nextid = 7;
    p = new SNAC(0x0009, 0x0007, 0, 0, snac_nextid++);
    p.send(getBosConnNotNull());

    log("loginSequence_40sendContactList(): sending sci fi 2 (1/0x1e), generic service controls/unknown");
    snac_nextid = 0x1e;
    p = new SNAC(0x0001, 0x001e, 0, 0, snac_nextid++);
    p.addByteArray(new byte[] {(byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x25, (byte) 0xC0, (byte) 0xA8, (byte) 0x04, (byte) 0x6B, (byte) 0x00, (byte) 0x00, (byte) 0x73, (byte) 0x74, (byte) 0x04, (byte) 0x00, (byte) 0x07, (byte) 0x1D, (byte) 0xAE, (byte) 0xF0, (byte) 0x9E, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x50, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x3B, (byte) 0x17, (byte) 0x6B, (byte) 0x57, (byte) 0x3B, (byte) 0x18, (byte) 0xAC, (byte) 0xE9, (byte) 0x3B, (byte) 0x17, (byte) 0x6B, (byte) 0x4E, (byte) 0x00, (byte) 0x00});
    p.send(getBosConnNotNull());

    log("loginSequence_40sendContactList(): sending sci fi 3 (1/0x11), generic service controls/unknown");
    snac_nextid = 0x11;
    p = new SNAC(0x0001, 0x0011, 0, 0, snac_nextid++);
    p.addByteArray(new byte[] {0, 0, 0, 0});
    p.send(getBosConnNotNull());

    log("loginSequence_40sendContactList(): sending sci fi 4, (1/2), generic service controls/Client is now online and ready for normal function");
    p = new SNAC(0x0001, 0x0002, 0, 0, 2);
    p.addByteArray(new byte[] {(byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x04, (byte) 0x06, (byte) 0x86, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0b, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01});
    p.send(getBosConnNotNull());

    //2A 02 0A B8 00 18 //clt snds
    //00 15 00 02 //sf 5, unknown/unknown
    //00 00
    //00 01 00 02 //snac reqid
    //00 01 00 0A
    //  08
    //    00 BE 5C 94 01 3C 00 02 00
    //  ..............._\".<...

    //2a 02 5da1 0018  0015 0002 0000 00010002  0001 000a 0800 1b374d07 3c 00 02 00
    /*
      log("loginSequence_40sendContactList(): sending 0x15/2, reqid 1/2, get offline messages 1");
      p = new SNAC(0x0015, 0x0002, 0, 0, 0x00010002);
      //"BE 5C 94 01"  aka  0x01 94 5C BE == 26500286
      p.addByteArray(new byte[] {//
        0x00, 0x01, 0x00,       0x0a, 0x08, 0x00, //
        (byte) (uin & 0xff), //
        (byte) ((uin >> 8)& 0xff), //
        (byte) ((uin >> 16)& 0xff), //
        (byte) ((uin >> 24)& 0xff), //
        (byte) 0x3C, (byte) 0x00, (byte) 0x02, (byte) 0x00});
      p.send(getBosConnNotNull());
      p = null;

      //2a 02 5da2 0033  0015 0002 0000 00020002  0001 0025 2300 1b374d07 d0 07 03 00 98 08 17 00 3c 6b 65 79 3e 44 61 74 61 46 69 6c 65 73 49 50 3c 2f 6b 65 79 3e 00
      log("loginSequence_40sendContactList(): sending 0x15/2, reqid 2/2, get offline messages 2");
      p = new SNAC(0x0015, 0x0002, 0, 0, 0x00020002);
      //"BE 5C 94 01"  aka  0x01 94 5C BE == 26500286
      p.addByteArray(new byte[] {//
        0x00, 0x01, 0x00,       0x25, 0x23, 0x00, //
        (byte) (uin & 0xff), //
        (byte) ((uin >> 8)& 0xff), //
        (byte) ((uin >> 16)& 0xff), //
        (byte) ((uin >> 24)& 0xff), //
        (byte) 0xd0, (byte) 0x07, (byte) 0x03, (byte) 0x00, (byte) 0x98, (byte) 0x08, (byte) 0x17, (byte) 0x00, (byte) 0x3c, (byte) 0x6b, (byte) 0x65, (byte) 0x79, (byte) 0x3e, (byte) 0x44, (byte) 0x61, (byte) 0x74, (byte) 0x61, (byte) 0x46, (byte) 0x69, (byte) 0x6c, (byte) 0x65, (byte) 0x73, (byte) 0x49, (byte) 0x50, (byte) 0x3c, (byte) 0x2f, (byte) 0x6b, (byte) 0x65, (byte) 0x79, (byte) 0x3e, (byte) 0x00});
      p.send(getBosConnNotNull());
      p = null;

      //2a 02 5da3 0031  0015 0002 0000 00030002  0001 0023 2100 1b374d07 d0 07 04 00 98 08 15 00 3c 6b 65 79 3e 42 61 6e 6e 65 72 73 49 50 3c 2f 6b 65 79 3e 00
      log("loginSequence_40sendContactList(): sending 0x15/2, reqid 3/2, get offline messages 3");
      p = new SNAC(0x0015, 0x0002, 0, 0, 0x00030002);
      //"BE 5C 94 01"  aka  0x01 94 5C BE == 26500286
      p.addByteArray(new byte[] {//
        0x00, 0x01, 0x00,       0x23, 0x21, 0x00, //
        (byte) (uin & 0xff), //
        (byte) ((uin >> 8)& 0xff), //
        (byte) ((uin >> 16)& 0xff), //
        (byte) ((uin >> 24)& 0xff), //
        (byte) 0xd0, (byte) 0x07, (byte) 0x04, (byte) 0x00, (byte) 0x98, (byte) 0x08, (byte) 0x15, (byte) 0x00, (byte) 0x3c, (byte) 0x6b, (byte) 0x65, (byte) 0x79, (byte) 0x3e, (byte) 0x42, (byte) 0x61, (byte) 0x6e, (byte) 0x6e, (byte) 0x65, (byte) 0x72, (byte) 0x73, (byte) 0x49, (byte) 0x50, (byte) 0x3c, (byte) 0x2f, (byte) 0x6b, (byte) 0x65, (byte) 0x79, (byte) 0x3e, (byte) 0x00});
      p.send(getBosConnNotNull());
      p = null;

      //2a 02 5da4 0032  0015 0002 0000 00040002  0001 0024 2200 1b374d07 d0 07 05 00 98 08 16 00 3c 6b 65 79 3e 43 68 61 6e 6e 65 6c 73 49 50 3c 2f 6b 65 79 3e 00
      log("loginSequence_40sendContactList(): sending 0x15/2, reqid 4/2, get offline messages 4");
      p = new SNAC(0x0015, 0x0002, 0, 0, 0x00040002);
      //"BE 5C 94 01"  aka  0x01 94 5C BE == 26500286
      p.addByteArray(new byte[] {//
        0x00, 0x01, 0x00,       0x24, 0x22, 0x00, //
        (byte) (uin & 0xff), //
        (byte) ((uin >> 8)& 0xff), //
        (byte) ((uin >> 16)& 0xff), //
        (byte) ((uin >> 24)& 0xff), //
        (byte) 0xd0, (byte) 0x07, (byte) 0x05, (byte) 0x00, (byte) 0x98, (byte) 0x08, (byte) 0x16, (byte) 0x00, (byte) 0x3c, (byte) 0x6b, (byte) 0x65, (byte) 0x79, (byte) 0x3e, (byte) 0x43, (byte) 0x68, (byte) 0x61, (byte) 0x6e, (byte) 0x6e, (byte) 0x65, (byte) 0x6c, (byte) 0x73, (byte) 0x49, (byte) 0x50, (byte) 0x3c, (byte) 0x2f, (byte) 0x6b, (byte) 0x65, (byte) 0x79, (byte) 0x3e, (byte) 0x00});
      p.send(getBosConnNotNull());
      p = null;
    */
    //"offl msg ack" start
    ////2a 02 5da5 0018  0015 0002 0000 00050002  0001 000a 0800 1b374d07 3e 00 06 00
    //log("loginSequence_40sendContactList(): sending 0x15/2, reqid 5/2, get offline messages 5");
    //p = new SNAC(0x0015, 0x0002, 0, 0, 0x00050002);
    ////"BE 5C 94 01"  aka  0x01 94 5C BE == 26500286
    //p.addByteArray(new byte[] {//
    //0x00, 0x01, 0x00,     0x0a, 0x08, 0x00, //
    //(byte) (uin & 0xff), //
    //(byte) ((uin >> 8)& 0xff), //
    //(byte) ((uin >> 16)& 0xff), //
    //(byte) ((uin >> 24)& 0xff), //
    //(byte) 0x3e, (byte) 0x00, (byte) 0x06, (byte) 0x00});
    //p.send(getBosConnNotNull());
    //p = null;
    //"offl msg ack" end

    ////AIM/Oscar Protocol Specification:
    ////Section 3: BOS Signon: Set Initial ICBM Parameter
    ////--------------------------------------------------------------------------------
    ////Set ICBM Paramter[Source: Client]
    ////Not sure what this one does, but it can't hurt to send it.
    ////SNAC Information:
    ////Family 0x0004
    ////SubType 0x0002
    ////Flags {0x00, 0x00}
    ////--------------------------------------------------------------------------------
    ////RAW SNAC Header
    ////RAW 0x0000
    ////RAW 0x00000003
    ////RAW 0x1f40
    ////RAW 0x03e7
    ////RAW 0x03e7
    ////RAW 0x0000
    ////RAW 0x0000
    //
    //SNAC p = new SNAC(4, 2);
    //p.addWord(0);
    //p.addDWord(3);
    //p.addWord(0x1f40);
    //p.addWord(0x03e7);
    //p.addWord(0x03e7);
    //p.addWord(0);
    //p.addWord(0);
    //p.send(getBosConnNotNull());

    log("exited loginSequence_40sendContactList()");
  }

  private boolean wwwAolComPacketReceived;

  private void loginSequence_50waitForWwwAolComPacket(PluginContext ctx) throws MessagingNetworkException
  {
    log("entered loginSequence_50waitForWwwAolComPacket");
    this.wwwAolComPacketReceived = false;
    long stopTime = System.currentTimeMillis() + ctx.getICQ2KMessagingNetwork().getServerResponseTimeoutMillis();

    getBosConnNotNull().addHandler(new RxHandler()
    {
      public void triggered(Session s, Command_rx_struct rxframe, SNAC snac) throws java.io.IOException, MessagingNetworkException
      {
        log("loginSequence_50waitForWwwAolComPacket(): wwwAolComPacket received, exiting login");
        Session.this.wwwAolComPacketReceived = true;
        getBosConnNotNull().removeHandler(1, 0x13);
      }
    }, 1, 0x13);

    while (!wwwAolComPacketReceived)
    {
      if (Thread.currentThread().isInterrupted())
        replaceThrowMessagingNetworkException("thread interrupted", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      shutdownAt(stopTime, "loginSequence_50waitForWwwAolComPacket", ctx);

      if (getBosConnNotNull().isClosed())
        throw new MessagingNetworkException("icq server connection closed",
          MessagingNetworkException.CATEGORY_LOGGED_OFF_DUE_TO_NETWORK_ERROR);

      tick(ctx);

      try
      {
        Thread.currentThread().sleep(10);
      }
      catch (InterruptedException ex)
      {
        replaceThrowMessagingNetworkException("thread interrupted", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      }
    }
    log("exited loginSequence_50waitForWwwAolComPacket");
  }

  void logout(PluginContext ctx, int reasonCategory, String reasonMessage)
  throws MessagingNetworkException
  {
    log("logout: "+reasonMessage);
    //ASSERT_LOGGED_IN();
    shutdown(ctx, reasonCategory, reasonMessage);
  }
  /**
   * Logs off, closes all connections and
   * deallocates a session
   * sess: Session to kill
   * See also: libfaim::conn.c::aim_session_kill
   * See also: libfaim::conn.c::aim_logoff
   */
  private void logout0(PluginContext ctx, int reasonCategory, String reasonMessage) throws MessagingNetworkException
  {
    setRunning(false);
    log("closing all connections");
    Aim_conn_t conn1;
    Aim_conn_t conn2;
    synchronized (logoutLock)
    {
      logoutLock.notifyAll();
      conn1 = authconn;
      authconn = null;
      conn2 = bosconn;
      bosconn = null;
    }
    closeConnectionIfNotNull(conn1);
    closeConnectionIfNotNull(conn2);
    unlockSendMsgWait();
    log("closing all done.");
    setStatus_Oscar_Internal(StatusUtil.OSCAR_STATUS_OFFLINE, false, ctx, reasonCategory, reasonMessage);
  }
  /*
    Parses incoming message (ICBM) packet.
  */
  private void parseMessage(Command_rx_struct rxframe, PluginContext ctx)
  throws MessagingNetworkException, java.io.IOException
  {
    log("parseMessage() start");
    final byte[] data = rxframe.data;
    final int datalen = data.length;
    int ofs = 10;
    MLang.EXPECT(datalen >= 20, "datalen must be >= 20", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
    byte[] msgCookie = aimutil_getByteArray(data, ofs, 8);
    ofs += 8; //0x12 (18)
    final int msgFormat = aimutil_get16(data, ofs);
    ofs += 2;
    log("parseMessage(): msgFormat=" + LogUtil.toString_Hex0xAndDec(msgFormat) + ", datalen=" + LogUtil.toString_Hex0xAndDec(datalen));
    if (msgFormat != 1 && msgFormat != 2 && msgFormat != 4)
    {
      log("parseMessage(): msg format is unknown: msgFmt=" + msgFormat + ", incoming message ignored");
      return;
    }
    String senderLoginId = aimutil_getString(data, 20);
    ofs += 1 + aimutil_get8(data, 20) + 2; //+2: skip WORD warning level
    final int headerTlvCount = aimutil_get16(data, ofs);
    ofs += 2;
    Aim_tlvlist_t tlvlist = aim_readtlvchain(data, ofs, datalen - ofs);
    int type = (msgFormat == 1 ? 2 : 5);
    byte[] msgBlock = tlvlist.skipNtlvsGetTlvOfTypeAsByteArray(headerTlvCount, type);
    //skipNtlvsGetTlvOfTypeAsByteArray(int n, int type)
    if (msgBlock == null)
      log("parseMessage(): headerCount="+headerTlvCount+", type=" + type + ", msgBlock=" + msgBlock);
    else
    {
      log("parseMessage(): headerCount="+headerTlvCount+", type=" + type + ", msgBlock.len=" + LogUtil.toString_Hex0xAndDec(msgBlock.length));
      switch (msgFormat)
      {
        case 2 : //msg format == 2
        {
          // subformat 0:
          //
          //      00: 00 00
          //      02: C0 F2 1C 00  1E 1D 00 00 //msg cookie (2nd time)
          //      0a: 09 46 13 49  4C 7F 11 D1
          //      12: 82 22 44 45  53 54 00 00
          //      1a: 00 0A 00 02  00 01 00 0F
          //      22: 00 00 27  11
          //          00 5B //some length
          //      28:
          //    28+00:  1B 00 07 00  00 00 00 00
          //            00 00 00 00  00 00 00 00
          //    28+10:  00 00 00 00  00 00 03 00
          //            00 00 00 F7  FF 0E 00 F7
          //    28+20:  FF 00 00 00  00 00 00 00
          //            00 00 00 00
          //    28+2C:  00 01 //msgkind: 0001 is textmsg, 0004 is url msg, 000C is auth msg, 0013 is contacts msg
          //            00 00
          //    (28+30:)
          //  (58:)
          //  --
          //      58: 00 01
          //      5a: 00
          //      5b: 1E 00 //msglen+1
          //              00: 32 30 30 30  62 20 62 65
          //          74 61 20 76  2E 34 2E 35
          //              10: 36 0D 0A 62  75 69 6C 64
          //          20 33 32 36  34 "2000b beta v.4.56\r\nbuild 3264" //msgtext
          //          (1d:)
          //      00 80 80 00 00 FF FF FF 00
          //--
          //
          // subformat 1:
          //  00 01   //msg format 2, subformat 1 ?
          //  f7 1b 4a 00  2a 74 00 00 //mcookie
          //  09 46 13 49  4c 7f 11 d1 //const
          //  82 22 44 45  53 54 00 00 //const
          //  00 0b 00 02
          //  00 01 //msgkind?

          if (msgBlock.length < 2)
          {
            log("parseMessage(): msgblock length < 2, incoming message ignored.");
            return;
          }
          int msgSubFormat = aimutil_get16(msgBlock, 0);
          log("parseMessage(): msgSubFormat==" + LogUtil.toString_Hex0xAndDec(msgSubFormat));
          if (msgSubFormat != 0)
          {
            log("parseMessage(): *** NOT IMPLEMENTED!!! *** msg subformat != 0, incoming message ignored.");
            return;
          }
          int msgOfs = 0x5b;
          if (msgBlock.length < msgOfs + 2)
          {
            log("parseMessage(): msgblock too short, missing msg text, incoming message ignored.");
            return;
          }
          int msgLenLo = (msgBlock[msgOfs++] & 0xff);
          int msgLen = (((msgBlock[msgOfs++] & 0xff) << 8) | msgLenLo) - 1;
          log("parseMessage(): msgLen=" + LogUtil.toString_Hex0xAndDec(msgLen) + ", msgOfs=" + LogUtil.toString_Hex0xAndDec(msgOfs));
          if (msgLen <= 0)
          {
            log("parseMessage(): msgLen <= 0, incoming message ignored.");
            return;
          }
          byte[] msgBytes = aimutil_getByteArray(msgBlock, msgOfs, msgLen);
          byte enc = (byte) aimutil_get8(msgBlock, msgOfs-3);






          //msg kind: 0x01 is textmsg, 04 is url msg, 0C & 06 is authreq msg, 13 is contacts msg
          int msgKind = aimutil_get16(msgBlock, 0x28 + 0x2C);
          log("parseMessage(): msgKind=" + LogUtil.toString_Hex0xAndDec(msgKind));

          boolean xfesAreValid; //if valid, then we should not convert 0xFE to "\r\n"
          switch (msgKind)
          {
            case 0x01: //txt
              xfesAreValid = true;
              break;

            //case 0x13: //contacts
            //case 0x04: //url

            default:
              xfesAreValid = false;
              break;
          }
          if (msgKind == 0x13)
          {
            parseIncomingContacts(senderLoginId, msgBytes, ctx);
          }
          else
          {
            String msg1;
            if (xfesAreValid)
              msg1 = getMsgText(msgBytes, enc);
            else
              msg1 = byteArray2stringConvertXfes(msgBytes); //stoneage
            ctx.getICQ2KMessagingNetwork().fireMessageReceived(senderLoginId, loginId, msg1);
          }






          //send msg ack
          //--
          ////Msgack start:
          //00 04 00 0B
          //00 00
          //00 00 00 0B //snac req id = 0xb
          //C0 F2 1C 00  1E 1D 00 00 //msg cookie of acked msg
          //00 02  //channel id of acked msg
          //08 3x 3x 3x 3x 3x 3x 3x 3x //sender of acked msg //12345678
          //        00 03
          //
          //        1B 00 07 00  00 00 00 00
          //        00 00 00 00  00 00 00 00
          //        00 00 00 00  00 00 03 00
          //        00 00 00 F7  FF 0E 00 F7
          //        FF 00 00 00  00 00 00 00
          //        00 00 00 00  00 01
          //  --
          //        00 00 00 00  00 01 00 00
          //        00 00 00 00  FF FF FF FF
          ////msgack end.
          //--
          ////Acked msg msgBlock, offset 28:
          //    00: 1B 00 07 00  00 00 00 00
          //        00 00 00 00  00 00 00 00
          //    10: 00 00 00 00  00 00 03 00
          //        00 00 00 F7  FF 0E 00 F7
          //    20: FF 00 00 00  00 00 00 00
          //        00 00 00 00  00 01 00 00
          //      (30:)
          //  (58:)
          SNAC msgAck = new SNAC(4, 0xb, 0, 0, 0xb);
          msgAck.addByteArray(msgCookie);
          msgAck.addWord(msgFormat);
          msgAck.addStringPrependedWithByteLength(senderLoginId);
          msgAck.addWord(3);
          byte[] block28 = aimutil_getByteArray(msgBlock, 0x28, 0x30-2);
          msgAck.addByteArray(block28);
          block28 = null;
          msgAck.addByteArray(new byte[] {00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
          Aim_conn_t conn = bosconn;
          MLang.EXPECT_NOT_NULL(conn, "bosconn", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
          log("parseMessage(): sending msgAck");
          msgAck.send(conn);


          break;
        }
        case 1 : //msg format == 1
        {
          //   05 01
          //   00 01 //!!!
          //      01
          //   01 01
          //   00 08
          //      00 00 00 00
          //      68 75 68 3f         //"huh?"
          //--
          //
          //   05 01
          //   00 04 //!!!
          //      01 01 01 02
          //   01 01
          //   00 1c
          //      00 00 //enc: 0000 is "ASCII", 0002 is Unicode bib endian "UnicodeBig"
          //      00 00 //ignored 0000 when ASCII, FFFF when UnicodeBig
          //      74 79 70   65 20 6d 73 - 67 20 61 6e   64 20 70 72   "type ms g and pr e"
          //      65 73 73 20   65 6e 74 65 - 72   "ess ente r"
          //--
          int msgOfs = 2;
          msgOfs += 2 + aimutil_get16(msgBlock, msgOfs);
          msgOfs += 2;

          //final block length
          msgOfs += 2;

          //flag1 = aimutil_get16(msgBlock);
          msgOfs += 2;
          //flag2 = aimutil_get16(msgBlock);
          msgOfs += 2;
          MLang.EXPECT(msgOfs < msgBlock.length, "Incoming protocol violation: msgOfs > msgBlock.length", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
          byte enc = (byte) aimutil_get8(msgBlock, msgOfs - 3);
          byte[] msgBytes = aimutil_getByteArray(msgBlock, msgOfs, msgBlock.length - msgOfs);
          String msg = getMsgText(msgBytes, enc);
          ctx.getICQ2KMessagingNetwork().fireMessageReceived(senderLoginId, loginId, msg);
          break;
        }
        case 4 : //msg format == 4
        {
          //incoming auth request
          //
          //00 06 00 04 / 00 03 00 00 //? //sometimes 00 13
          //[...]
          //00 05 00 2F
          //(msgBlock starts here:)
          //  39 97 B5 03 //sender uin
          //  0C //sometimes "06"  //01 is textmsg, 04 is url msg, 0C & 06 is authreq msg, 13 is contacts msg
          //  00 27
          //     00
          //     6D 75 6B FE  50 6F 6C 69   6E 61 FE 42  65 6C 6F 73
          //     74 6F 74 73  6B 79 FE 6D   75 7A 6B 40  6D 61 69 6C
          //     2E 72 75 FE  31 FE
          //  00
          //--
          //
          //incoming URL message
          //
          //00 06 00 04 / 00 03 00 13
          //[...]
          //00 05 00 3c /
          //(msgBlock starts here:)
          //.  be 5c 94 01  //sender uin  //"BE 5C 94 01" aka 0x01945CBE == 26500286
          //.  04  //msg kind: 0x01 is textmsg, 04 is url msg, 0C & 06 is authreq msg, 13 is contacts msg
          //.  00 34  //msg text length
          //.    00
          //.    69 6f 70 fe "iop."
          //.    66 69 6c    "fil e"
          //.    65 3a 2f 2f   2f 53 3a 2f  73 6f 66 74   77 61 72 65   "e:///S:/ software /"
          //.    2f 6a 61 76   61 2f 6a 64  6b 31 2e 32   2f 64 6f 63   "/java/jd k1.2/doc s"
          //.    73 2f 69 6e   64 65 78 2e  68 74 6d 6c   "s/index. html"
          //.  00
          //--
          //
          //also incoming contacts messages
          //
          if (msgBlock.length < 7)
          {
            log("parseMessage(): msgblock length < 7, incoming message ignored.");
            return;
          }

          //msg kind: 0x01 is textmsg, 04 is url msg, 0C & 06 is authreq msg, 13 is contacts msg
          int msgKind = aimutil_get8(msgBlock, 4);
          log("parseMessage(): msgKind=" + LogUtil.toString_Hex0xAndDec(msgKind));
          Log4jUtil.dump(CAT, "", msgBlock, "icq msgblock: ");
          boolean xfesAreValid; //if valid, then we should not convert 0xFE to "\r\n"
          switch (msgKind)
          {
            case 0x06: //authreq06
            case 0x0C: //authreq0C
              handleIncomingAuthRequest(senderLoginId, ctx);
              return;
            case 0x01: //txt
              xfesAreValid = true;
              break;
            //case 0x13: //contacts
            //case 0x04: //url
            default:
              xfesAreValid = false;
              break;
          }
          int msgOfs1 = 0x5;
          int msgLen1 = aimutil_get16(msgBlock, msgOfs1) - 1;
          log("parseMessage(): msgLen1=" + LogUtil.toString_Hex0xAndDec(msgLen1));
          if (msgLen1 == 0)
          {
            log("parseMessage(): msgLen1 == 0, incoming message ignored.");
            return;
          }
          msgOfs1 += 3;
          log("parseMessage(): msgOfs1=" + LogUtil.toString_Hex0xAndDec(msgOfs1));
          byte[] msgBytes1 = aimutil_getByteArray(msgBlock, msgOfs1, msgLen1);
          byte enc = (byte) aimutil_get8(msgBlock, msgOfs1-1);
          if (msgKind == 0x13)
          {
            parseIncomingContacts(senderLoginId, msgBytes1, ctx);
            return;
          }
          String msg1;
          if (xfesAreValid)
            msg1 = getMsgText(msgBytes1, enc);
          else
            msg1 = byteArray2stringConvertXfes(msgBytes1);
          ctx.getICQ2KMessagingNetwork().fireMessageReceived(senderLoginId, loginId, msg1);
          break;
        }
      }
    }
  }

  private static String getMsgText(byte[] data, byte encoding)
  {
    if (encoding == 0x02)
    {
      try
      {
        return new String(data, "UnicodeBig");
      }
      catch (Exception ex)
      {
        CAT.error("cannot parse UnicodeBig encoding, reverted to default enc", ex);
      }
    }
    //encoding == 0x00 is "sender-machine-locale-encoding";
    return byteArray2string(data);
  }

  private void parseIncomingContacts(String senderLoginId, byte[] ba, PluginContext ctx)
  throws MessagingNetworkException
  {
    List uins = new ArrayList(30);
    List nicks = new ArrayList(30);
    int chunkStart = 0;
    int chunkLen = 0;
    int chunkCount = 0;
    String s = null;
    for (int i = 0; i < ba.length; i++)
    {
      byte b = ba[i];
      if (b != (byte) 0xFE)
      {
        chunkLen++;
        continue;
      }
      else
      {
        s = byteArray2string(ba, chunkStart, chunkLen);
        chunkCount++;
        if (chunkCount <= 1) //ignore
        {
          //ignore number of contacts
        }
        else // chunkCount > 1
        {
          if ((chunkCount & 1) == 0) //uin (2, 4, ...)
            uins.add(s);
          else // if ((chunkCount & 1) == 1) //nick (3, 5, ...)
            nicks.add(s);
        }

        i++;
        for (; i < ba.length; i++)
        {
          if (ba[i] != 0xFE)
          {
            i--;
            break;
          }
        }
        chunkStart = i + 1;
        chunkLen = 0;
        if (i >= ba.length)
          break;
      }
    }
    MLang.EXPECT_EQUAL(uins.size(), nicks.size(), "incoming contacts: uins.size()", "incoming contacts: nicks.size()", MessagingNetworkException.CATEGORY_STILL_CONNECTED);
    String[] uinssa  = (String[]) uins.toArray(EMPTY_STRING_ARRAY);
    String[] nickssa = (String[]) nicks.toArray(EMPTY_STRING_ARRAY);

    ctx.getICQ2KMessagingNetwork().fireContactsReceived(senderLoginId, loginId, uinssa, nickssa);
  }

  private static String[] EMPTY_STRING_ARRAY = new String[] {};

  /*
  Parses incoming offline message packet.
  */
  private void parseOfflineMessage(Command_rx_struct rxframe, PluginContext ctx) throws MessagingNetworkException, java.io.IOException
  {
    //offline auth request
    //
    //00 15 00 03   //offline msg? (flags=0 1)
    //00 01  //flags
    //00 01 00 02  //reqid
    //00 01 00 4a
    //  48 00   //length
    //  7f 0c 55 07 //plugin's own uin
    //  41 00 02 00
    //  be 5c 94 01 //sender's uin
    //  d1 07 07 03   ".U.A... .\...... ."
    //  13 38
    //  06 00
    //  32
    //    00
    //    6a 6f 78 79 fe 45   75 67 65 6e   "jo xy.Eugen e"
    //        65 2f 4a 6f   65 fe 46 69 - 6c 69 70 70   6f 76 fe 6a   "e/Joe.Fi lippov.j o"
    //        6f 65 40 69   64 69 73 79 - 73 2e 69 61   65 2e 6e 73   "oe@idisy s.iae.ns k"
    //        6b 2e 73 75   fe 31 fe
    //  00   "k.su.1.."
    //--
    //
    //offline auth req
    //
    //[plugin's uin: 122994538]
    //00 15 00 03
    //00 01
    //00 01 00 02
    //00 01 00 50
    //.  4e 00
    //.  6a bf 54 07
    //.  41 00 02 00
    //.  1b 37 4d 07
    //.  d1 07 07 03
    //.  14 1a
    //.  06 00   //authreq msg
    //.  38
    //.    00
    //.    6a 6f - 78 79 74 65   73 74 20 31   "....8.jo xytest 1 2"
    //         32 fe 6a 6f   78 79 74 65 - 73 74 20 31   32 fe fe 6a   "2.joxyte st 12..j o"
    //         6f 78 79 74   65 73 74 31 - 32 40 6e 6f   77 68 65 72   "oxytest1 2@nowher e"
    //         65 2e 63 6f   6d fe 30 fe - 61 73 75 73   21 00   "e.com.0. asus!."
    //--
    //
    //2A 02 10 B5 00 2E   *..÷ //srv snds
    //00 15 00 03 //offline msg? (flags=0 1)
    //00 01 //flags
    //00 01 00 02 //reqid
    //00 01 00 20
    //
    //00:  1E 00
    //02:    BE 5C 94 01  //my (receiver) uin
    //06:      41 00 02 00
    //0a:      8E 95 B5 02  //sender uin?
    //0e:    D1 07 07 03
    //12:    08 19
    //14:      01 00 //normal msg
    //16:    06
    //   00
    //18:      61 67 6F 72 61   agora
    //1d:    00 00 00
    //20:
    final byte[] data = rxframe.data;
    final int datalen = data.length;
    //
    Aim_tlvlist_t tlvlist = aim_readtlvchain(data, 10, datalen - 10);
    //boolean flag_AIM_IMFLAGS_ACK = tlvlist.getNthTlvOfType(1, 3) != null;
    //boolean flag_AIM_IMFLAGS_AWAY = tlvlist.getNthTlvOfType(1, 4) != null;
    byte[] msgBlock = tlvlist.getNthTlvOfTypeAsByteArray(1, 1);
    if (msgBlock != null)
    {
    int msgOfs;
    msgOfs = 0x14;
    int t1 = aimutil_get8(msgBlock, msgOfs++);
    int t0 = aimutil_get8(msgBlock, msgOfs++);
    final int msgType = (t0 << 8) | t1;
    //
    msgOfs = 0x0a; //offset of sender uin
    //0x01 94 5C BE == 26500286
    //  h0 h1 h2 h3
    MLang.EXPECT(msgOfs + 4 <= msgBlock.length, "msgOfs + 4 must be <= msgBlock.length (A)", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
    int h3 = aimutil_get8(msgBlock, msgOfs++);
    int h2 = aimutil_get8(msgBlock, msgOfs++);
    int h1 = aimutil_get8(msgBlock, msgOfs++);
    int h0 = aimutil_get8(msgBlock, msgOfs++);
    final long senderUin = (((((h0 << 8) | h1) << 8) | h2) << 8) | h3;
    //msgType == 0x01 for plain text msg
    if (msgType == 0x06)
    {
      //auth msg
      sendAuthResponsePositive(String.valueOf(senderUin), ctx);
      return;
    }
    else
    {
      msgOfs += 7;
      MLang.EXPECT(msgOfs + 3 <= msgBlock.length, "msgOfs + 3 must be <= msgBlock.length (B)", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      int msgLen = aimutil_get16(msgBlock, msgOfs) - 1;
      msgOfs += 3;
      MLang.EXPECT_POSITIVE(msgLen, "msgLen", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      MLang.EXPECT(msgOfs + msgLen <= msgBlock.length, "msgOfs + msgLen must be <= msgBlock.length (C)", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
      byte[] msgBytes = aimutil_getByteArray(msgBlock, msgOfs, msgLen);
      String msg = byteArray2string(msgBytes);
      ctx.getICQ2KMessagingNetwork().fireMessageReceived(String.valueOf(senderUin), loginId, msg);
    }
    }
    else
    log("parseOfflineMessage(): msgBlock TLV (type 1, position 1) does not exist, message ignored.");
  }

  public void removeFromContactList(String dstLoginId, PluginContext ctx) throws MessagingNetworkException
  {
    log("removeFromContactList()");
    try
    {
      ASSERT_LOGGED_IN();
      int dstUin = MLang.parseInt_NonNegative(dstLoginId, "dstLoginId", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      Integer dstUin_ = new Integer(dstUin);
      if (contactListUinInt2cli.remove(dstUin_) == null)
      {
        log(dstLoginId + " is not in a contact list, ignored");
        return;
      }
      send_removeSingleUserFromContactList(dstLoginId);
    }
    catch (Exception ex)
    {
      handleException(ex, "removeFromContactList", ctx);
    }
  }
  private void rx_handle_negchan_middle(Command_rx_struct command, PluginContext context) throws MessagingNetworkException
  {
    /* AIM_CONN_TYPE_AUTH is used only by the older login protocol */
    if (command.conn.type == AIMConstants.AIM_CONN_TYPE_AUTH)
    {
      log("NOT_IMPLEMENTED: rx_handle_negchan_middle, AIM_CONN_TYPE_AUTH");
      //consumenonsnac(command, 0x0017, 0x0003);
      return;
    }
    rx_handleConnectionError(command.data, true, context);
  }
  private void rx_handleConnectionError(byte[] command, boolean alsoFireMessageToUser, PluginContext context) throws MessagingNetworkException
  {
    String msg = "";
    int code = -1;
    Aim_tlvlist_t tlvlist = new Aim_tlvlist_t(this, command);
    if (tlvlist.getNthTlvOfType(1, 0x0009) != null)
      code = tlvlist.getNthTlvOfTypeAs16Bit(1, 0x0009);
    if (tlvlist.getNthTlvOfType(1, 0x000b) != null)
      msg = tlvlist.getNthTlvOfTypeAsString(1, 0x000b);
    log("rx_handleConnectionError: message=\"" + msg + "\", errCode=" + code);
    if (code == 0x0001)
    {
      String s = "Logged off: your ICQ number is probably used on another computer.";
      if (alsoFireMessageToUser)
        fireErrorMessage(s, context);
      log("rx_handleConnectionError: errCode=0x0001, shutting down.");
      shutdown(context, MessagingNetworkException.CATEGORY_LOGGED_OFF_YOU_LOGGED_ON_FROM_ANOTHER_COMPUTER, s);
      replaceThrowMessagingNetworkException(s, MessagingNetworkException.CATEGORY_LOGGED_OFF_YOU_LOGGED_ON_FROM_ANOTHER_COMPUTER);
    }
  }
  private void rx_handleGenericServiceError(byte[] flapDataField, PluginContext ctx)
  {
    //Generic Errors (SNAC family/subtype */1 and 9/9):
    //Family 0x0001: Generic Service Controls
    //Family 0x0002: Location/User Information Services
    //Family 0x0003: Buddy List Service
    //Family 0x0004: Messaging Service
    //Family 0x0005: Advertisements Service
    //Family 0x0007: Administrative/Account/Userinfo change Service
    //Family 0x0008: Display Popup Service
    //Family 0x0009: BOS-specific
    //Family 0x0009 subtype 0x0009: Server BOS error
    //Family 0x000A: User Lookup/Search Service
    //Family 0x000B: Statistics/Reports Service
    //Family 0x000C: Translate Service
    //Family 0x000D: Chatrooms Navigation
    //Family 0x000E: Chatrooms Service
    //
    Lang.ASSERT(flapDataField.length >= 4, "data length must be >= 4, but it is " + flapDataField.length);
    int snacFamily = aimutil_get16(flapDataField, 0);
    int snacSubtype = aimutil_get16(flapDataField, 2);
    log("rx_handleGenericServiceError: family=" + HexUtil.toHexString0x(snacFamily) + ", subtype=" + HexUtil.toHexString0x(snacSubtype));
    String service = "Unknown service ("+HexUtil.toHexString0x(snacFamily)+")";
    switch (snacFamily)
    {
      case 0x0001 :
        service = "Generic Service Controls";
        break;
      case 0x0002 :
        service = "Location/UserInfo Services";
        break;
      case 0x0003 :
        service = "Buddy List Service";
        break;
      case 0x0004 :
        service = "Messaging Service";
        break;
      case 0x0005 :
        service = "Advert Service";
        break;
      case 0x0007 :
        service = "Administrative Service";
        break;
      case 0x0008 :
        service = "Display Popup Service";
        break;
      case 0x0009 :
        if (snacSubtype != 0x0009)
          service = "BOS-specific errors";
        else
          service = "BOS Server 0x0009 errors";
        break;
      case 0x000A :
        service = "User Search Service";
        break;
      case 0x000B :
        service = "Stats/Reports Service";
        break;
      case 0x000C :
        service = "Translate Service";
        break;
      case 0x000D :
        service = "ChatNav Service";
        break;
      case 0x000E :
        service = "Chatrooms Service";
        break;
    }

    String errmsg = "Error: generic ICQ " + service + " error.";

    if (snacFamily != 0x0003) //Buddy List Service
      fireErrorMessage(errmsg, ctx);
    else
      log("// ignored: "+errmsg);
  }
  /**
  Returns true if and only if the rxframe was handled.
  */
  private boolean rxdispatch_BIG_SWITCH(final Command_rx_struct rxframe, PluginContext ctx) throws MessagingNetworkException, java.io.IOException
  {
    final Aim_conn_t conn = rxframe.conn;
    if (rxframe.hdr_oscar_type != 2)
      return false; //channel
    if (rxframe.data.length < 10)
      return false; //not a SNAC
    final int seqnum = rxframe.hdr_oscar_seqnum;
    final int family = aimutil_get16(rxframe.data, 0);
    final int subtype = aimutil_get16(rxframe.data, 2);
    final int flag0 = rxframe.data[4];
    final int flag1 = rxframe.data[5];
    final int flags = aimutil_get16(rxframe.data, 4);
    final long requestId = aimutil_get32(rxframe.data, 6);
    final byte[] snacData = AllocUtil.createByteArray(this, rxframe.data.length - 10);
    System.arraycopy(rxframe.data, 10, snacData, 0, snacData.length);
    SNAC p = null;

    boolean handled = false;

    switch (family)
    {
      //case 0x0009 :
      //switch (subtype)
      //{
      //case 0x0003 : //OPTIONAL
      ////bos.c::rights
      //log("rxdispatch_switch: BOS_RIGHTS");
      //////faimtest_bosrights
      ////ret = userfunc(sess, rx, maxpermits, maxdenies);
      ////dvprintf("faimtest: BOS rights: Max permit = %d / Max deny = %d\n", maxpermits, maxdenies);
      //aim_bos_clientready(conn);
      //return true;
      //break;
      //case AIM_CB_FAM_ACK:
      //switch (subtype)
      //{
      //case AIM_CB_ACK_ACK: //"NULL" is in libfaim.
      ////NULL; return true;
      //break;
      //}
      //break;
      case AIM_CB_FAM_GEN :
        switch (subtype)
        {
          case 0x0018 :
            //faimtest_hostversions; return true;
            break;
          case AIM_CB_GEN_SERVERREADY : //REQUIRED
            break;
            //see loginSequence_20waitForServerReady()
          case 0x0007 :
            break;
          case AIM_CB_GEN_REDIRECT :
            //faimtest_handleredirect; return true;
            break;
          case AIM_CB_GEN_MOTD :
            //faimtest_parse_motd; return true;
            break;
          case AIM_CB_GEN_RATECHANGE :
            //faimtest_parse_ratechange; return true;
            break;
          case AIM_CB_GEN_EVIL :
            //faimtest_parse_evilnotify; return true;
            break;
        }
        break;
      case AIM_CB_FAM_STS :
        switch (subtype)
        {
          case AIM_CB_STS_SETREPORTINTERVAL :
            //faimtest_reportinterval; return true;
            break;
        }
        break;
      case AIM_CB_FAM_BUD : //0x0003
        switch (subtype)
        {
          case AIM_CB_BUD_RIGHTSINFO :
            //faimtest_parse_buddyrights; return true;
            break;
          case AIM_CB_BUD_ONCOMING : //0x0003/0x000b
            //faimtest_parse_oncoming; return true;
            //5.1 Oncoming Buddies
            //The "oncoming buddy" command can occur at three different times during the lifecycle of an AIM session. The first, is at the end of the login process, just after the AIM message server is notified of the contents of your buddy list (Phase 3D, Command HI). The second is if/when one of the buddies in that list comes online who wasnt' before, and the third occurs at a regular interval while the connection is otherwise idle. This third case is used for updating your buddy list to make sure you didn't miss anything before. The command syntax for all three cases is exactly the same:

            //Fig 5.1.1 Oncoming Buddy SNAC
            //Position Length Data
            //SNAC header: Word 0x0003
            //SNAC header: Word 0x000b
            //SNAC header: Word 0x0000
            //SNAC header: DWord 32-bits of seeminly gibberish  //reqid
            //0 Byte Oncoming Screen Name Length
            //1 ASCII String Oncoming Screen Name (NOT null terminated)
            //2 Word Unsigned Int containing current Warning Level of Oncoming SN
            //4 Word Class (0x0004 for Free, 0x0003 for AOL)
            //6 Word 0x0001
            //8 Word 0x0002
            //10 Word Class Part Two (0x0010 for Free, 0x0004 for AOL)
            //12 Word 0x0002
            //14 Word 0x0004
            //16 DWord Unsigned Long (32bit) containing "Member Since" date
            //20 Word 0x0003
            //22 Word 0x0004
            //24 DWord Unsigned Long (32bit) containing "On Since" date
            //28* Word 0x0004
            //30* Word 0x0002
            //32* Word 0x0000
            //*Only exist for members of the "Free" or "Trial" classes

            //A note about classes: Every AIM Screen Name is associated with a class. AOL members (who are really just using the AOLIM?AIM Bridge) are in the "AOL" class. Members who are using the AIM-only service are under the "Free" class. And, "Free" members who have had thier account less than thirty days or so, are in the "Trial" class.

            //For those who don't know what "UNIX time_t format" is, it's the format used to represent times as unsigned long's in UNIX and some DOS-based libc's. I't simply the number of seconds elapsed from the 01 January 1970 00:00:00 UTC. (This is often referred to as "the UNIX epoch".) Both of the times in this command (at positions 27 and 35) are stored in this format (and yes, these will fail because of the y2.048k bug).

            //Note, that there's also an "Idle for" field in this command somewhere. It may very well be the last word of the command (since I don't think you can get the idle time of an AOL member anyway). Since I've found no good way to "be idle", I can't really figure out exactly where it is.

            String screenName = aimutil_getString(snacData, 0);
            int status = aimutil_get16(snacData, 0x50);
            log("rxdispatch_switch(): '" + screenName + "' changed status to " + StatusUtil.translateStatusOscarToString(status));
            if (loginId.equals(screenName))
              setStatus_Oscar_Internal(status, false, ctx, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED, null);
            else
              setContactStatus_Oscar(screenName, status, ctx, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED, null);
            handled = true;
            break;
          case AIM_CB_BUD_OFFGOING : //0x0003/0x000c
            screenName = aimutil_getString(snacData, 0);
            log("rxdispatch_switch(): '" + screenName + "' changed status to " + StatusUtil.translateStatusOscarToString(StatusUtil.OSCAR_STATUS_OFFLINE));
            if (loginId.equals(screenName))
              setStatus_Oscar_Internal(StatusUtil.OSCAR_STATUS_OFFLINE, false, ctx, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR, "icq server reported you as offline");
            else
              setContactStatus_Oscar(screenName, StatusUtil.OSCAR_STATUS_OFFLINE, ctx, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED, null);
            handled = true;
            break;
        }
        break;
      case AIM_CB_FAM_MSG : //0x0004
        switch (subtype)
        {
          case AIM_CB_MSG_INCOMING : //0x0004/0x0007
            parseMessage(rxframe, ctx); //faimtest_parse_incoming_im
            handled = true;
            break;
          case 0x0001: //AIM_CB_MSG_ERROR
            handled = parseMsgError(rxframe.data);
            break;
          case AIM_CB_MSG_MISSEDCALL :
            break;
          case 0x000C: //AIM_CB_MSG_ACK
            handled = parseHostAck(rxframe.data);
            break;
        }
        break;
      case 0x0015 :
        switch (subtype)
        {
          case 0x0003 :
          {
            long waitingForReqId;
            synchronized (operationResponseLock)
            {
              waitingForReqId = this.operationRequestId;
            }
            if (waitingForReqId == requestId)
            {
              switch (flags)
              {
                case 0x0001 :
                  handled = parseUserDetails(rxframe.data, ctx);
                  break;
                //case 0x0000 : //occurs!
                default :  //handle any flag just for safety
                  fireErrorFetchingUserDetails(rxframe.data, ctx);
                  handled = true;
                  break;
              }
            }
            break;
          }
        }
        break;

        //case AIM_CB_FAM_LOC:
        //switch (subtype)
        //{
        //case AIM_CB_LOC_ERROR:
        ////faimtest_parse_locerr; return true;
        //break;
        //}
        //break;
        //aim_conn_addhandler(sess, bosconn, 0x000a, 0x0001, faimtest_parse_searcherror, 0);
        //aim_conn_addhandler(sess, bosconn, 0x000a, 0x0003, faimtest_parse_searchreply, 0);
        //aim_conn_addhandler(sess, bosconn, AIM_CB_FAM_LOC, AIM_CB_LOC_USERINFO, faimtest_parse_userinfo, 0);
        //aim_conn_addhandler(sess, bosconn, AIM_CB_FAM_LOC, AIM_CB_LOC_RIGHTSINFO, faimtest_locrights, 0);
        //aim_conn_addhandler(sess, bosconn, 0x0004, 0x0005, faimtest_icbmparaminfo, 0);

        /////////////////aim_conn_addhandler(sess, bosconn, AIM_CB_FAM_SPECIAL, AIM_CB_SPECIAL_CONNERR, faimtest_parse_connerr, 0);
        /////////////////aim_conn_addhandler(sess, bosconn, 0x0001, 0x001f, faimtest_memrequest, 0);
        /////////////////aim_conn_addhandler(sess, bosconn, 0xffff, 0xffff, faimtest_parse_unknown, 0);
        /////////////////case AIM_CB_FAM_SPECIAL / AIM_CB_SPECIAL_CONNCOMPLETE: faimtest_conncomplete
    }
    if (!handled)
    {
      if (subtype == 1 || (family == 9 && subtype == 9))
      {
        rx_handleGenericServiceError(rxframe.data, ctx);
        handled = true;
      }
    }
    return handled;
  }


  private void send_addSingleUserToContactList(String dstLoginId) throws MessagingNetworkException, java.io.IOException
  {
    byte[] buddyName = string2byteArray(dstLoginId);
    MLang.EXPECT(buddyName.length > 0 && buddyName.length < 256, "buddy name is too long or too short.\r\nDetails: length must be 0..255, but it is " + buddyName.length + ". Buddy name: '" + dstLoginId + "'.", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    SNAC p = new SNAC(0003, 0004);
    p.addByte(buddyName.length);
    p.addByteArray(buddyName);
    log("send_addSingleUserToContactList(): send: add \"" + dstLoginId + "\" to a contact list.");
    p.send(bosconn);
  }
  private void send_removeSingleUserFromContactList(String dstLoginId) throws MessagingNetworkException, java.io.IOException
  {
    //0x0003/0x0005: Client: Remove buddy from buddy list
    byte[] buddyName = string2byteArray(dstLoginId);
    MLang.EXPECT(buddyName.length > 0 && buddyName.length < 256, "buddy name is too long or too short.\r\nDetails: length must be 0..255, but it is " + buddyName.length + ". Buddy name: '" + dstLoginId + "'.", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    SNAC p = new SNAC(3, 5);
    p.addByte(buddyName.length);
    p.addByteArray(buddyName);
    log("send_removeSingleUserFromContactList(): send: remove \"" + dstLoginId + "\" from a contact list.");
    p.send(bosconn);
  }
  private void send_setClientStatus(int newStatus_Oscar) throws MessagingNetworkException, java.io.IOException
  {
    //set me invisible packet:
    //snac 0009/0005 flags 0 0 snacid
    //2A 02 05 2A 00 0A
    //00 09 00 05 00 00 00 00 00 05
    //
    //set my status packet:
    //snac 0001/001e flags 0 0 snacid
    //2A 02 14 C5 00 12
    //00 01 00 1E 00 00 00 00 00 1E
    //00 06 00 04 00 03
    //xx xx  //status (word)
    //
    switch (newStatus_Oscar)
    {
      case StatusUtil.OSCAR_STATUS_AWAY :
      case StatusUtil.OSCAR_STATUS_DND :
      case StatusUtil.OSCAR_STATUS_FREE_FOR_CHAT :
      case StatusUtil.OSCAR_STATUS_NA :
      case StatusUtil.OSCAR_STATUS_OCCUPIED :
      case StatusUtil.OSCAR_STATUS_ONLINE :
        log("sending client status: "+newStatus_Oscar);
        SNAC p = new SNAC(1, 0x001e, 0, 0, snac_nextid++);
        p.addByteArray(new byte[] {0, 6, 0, 4, 0, 3});
        p.addWord(newStatus_Oscar);
        p.send(bosconn);
        break;
      case StatusUtil.OSCAR_STATUS_OFFLINE :
        Lang.ASSERT_FALSE("OSCAR_STATUS_OFFLINE: should never happen here");
        break;
      default :
        Lang.ASSERT_FALSE("unknown status value: " + newStatus_Oscar + ", ignored");
        break;
    }
  }
  private void sendAuthResponsePositive(String authRequestSenderLoginId, final PluginContext ctx) throws MessagingNetworkException, java.io.IOException
  {
    log("sendAuthResponsePositive(): \""+authRequestSenderLoginId+"\" requested authorization, sending auth response.");
    //2A 02 4F  AA 00 2E
    //00 04 00 06
    //00 00
    //00 0B 00 06 //(or 0 3 0 6) (or 0 1 0 6)
    //65 42 F7 05  50 45 00 00 //msgcook (arbitrary)
    //00 04  //msgchannel
    //08 36 32 32 33 32 33 37 37 //sender uin
    //00 05 00 09 //const
    //  7F 0C 55 07 //my uin
    //  08 00 01 00  00 //const
    //00 06 00 00 //const
    SNAC p = new SNAC(4, 6, 0, 0, 0x000b0006);
    byte[] cookie = new byte[8];
    new Random().nextBytes(cookie);
    p.addByteArray(cookie);
    p.addWord(4); //msg channel
    p.addStringPrependedWithByteLength(authRequestSenderLoginId);
    p.addByteArray(new byte[] {(byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x09, //
    (byte) (uin & 0xff), //
    (byte) ((uin >> 8) & 0xff), //
    (byte) ((uin >> 16) & 0xff), //
    (byte) ((uin >> 24) & 0xff), //
    (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, 0, 6, 0, 0});
    Aim_conn_t conn = bosconn;
    MLang.EXPECT_NOT_NULL(conn, "bosconn", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    p.send(conn);
    //0x01 94 5C BE == 26500286
  }
  /**
   * No-op: WinAIM 4.x sends these every minute to keep
   * the connection alive.
   */
  private void sendKeepAlive(Aim_conn_t conn, PluginContext ctx) throws MessagingNetworkException
  {
    log("sending keepAlive");
    Command_tx_struct newpacket = aim_tx_new(conn, AIMConstants.AIM_FRAMETYPE_OSCAR, 0x0005, 0);
    if (newpacket == null)
    return; //snac_nextid;
    newpacket.data = new byte[] {};
    aim_tx_sendframe(newpacket);
    return; //snac_nextid;
  }

  public void sendMessage(String dstLoginId, String text, PluginContext ctx) throws MessagingNetworkException
  {
    log("sendMessage() start");
    try
    {
      ASSERT_LOGGED_IN();
      int dstUin = MLang.parseInt_NonNegative(dstLoginId, "dstLoginId", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      Lang.ASSERT_NOT_NULL(text, "text");
      sendMessage0(bosconn, dstLoginId, text, ctx);
      log("sendMessage() finished (success)");
    }
    catch (Exception ex)
    {
      log("sendMessage() finished (failed)");
      handleException(ex, "sendMessage", ctx);
    }
  }

  public void sendContacts(String dstLoginId, String[] nicks, String[] loginIds, PluginContext ctx)
  throws MessagingNetworkException
  {
    log("sendContacts() start");
    try
    {
      ASSERT_LOGGED_IN();
      int dstUin = MLang.parseInt_NonNegative(dstLoginId, "dstLoginId", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      Lang.ASSERT_NOT_NULL(nicks, "nicks");
      Lang.ASSERT_NOT_NULL(loginIds, "loginIds");
      sendContacts0(bosconn, dstLoginId, nicks, loginIds, ctx);
      log("sendContacts() finished (success)");
    }
    catch (Exception ex)
    {
      log("sendContacts() finished (failed)");
      handleException(ex, "sendContacts", ctx);
    }
  }



  private byte nextDecId = (byte) 0xd9;
  private int nextMsgId = 0x1;
  private final Object operationResponseLock = new Object();
  private byte[] msgCookie = null;
  private long   operationRequestId = -1;
  private boolean sendMsgAckReceived = false;
  private boolean msgError;

  /**
    Called by this.'boolean rxdispatch_BIG_SWITCH' method.
    Returns true if and only if handled.
  */
  private boolean parseHostAck(final byte[] snacData)
  {
    Lang.ASSERT_NOT_NULL(snacData, "snacData");
    Lang.ASSERT_EQUAL(snacData[0], 0,   "snacData[0]", "0");
    Lang.ASSERT_EQUAL(snacData[1], 4,   "snacData[1]", "0");
    Lang.ASSERT_EQUAL(snacData[2], 0,   "snacData[2]", "0");
    Lang.ASSERT_EQUAL(snacData[3], 0xC, "snacData[3]", "0xC");

    final byte[] sentMsgKuka = this.msgCookie;
    final long sentMsgId = this.operationRequestId;
    if (sentMsgKuka == null || sentMsgId == -1)
    {
      CAT.debug("msg ack received TOO LATE, but ok");
      return true;
    }

    boolean eq = true;
    long tmpId = sentMsgId;
    for (int i = 0; i < 4; i++)
    {
      if ( snacData[9 - i] != (byte)(tmpId & 0xFF) )
      {
        eq = false;
        break;
      }
      tmpId >>= 8;
    }
    if (eq)
    {
      for (int i = 0; i < 8; i++)
      {
        if (snacData[ 10 + i ] != sentMsgKuka[i])
        {
          eq = false;
          break;
        }
      }
    }

    if (!eq)
    {
      CAT.debug("unknown msg ack");
      return false; //unknown ack;
    }

    //my ack!

    //CAT.debug("operationResponseLock, parseAck, before synchronized...");
    synchronized (operationResponseLock)
    {
      //CAT.debug("operationResponseLock, parseAck, entered synchronized...");
      this.sendMsgAckReceived = true;
      CAT.debug("msg ack received");
      operationResponseLock.notify();
    }
    return true;
  }

  /**
    Called by this.'boolean rxdispatch_BIG_SWITCH' method.
    Returns true if and only if handled.
  */
  private boolean parseMsgError(final byte[] snacData)
  {
    //00 04 00 01   00 00   00 01 00 06  00 0e

    CAT.debug("parseMsgError");
    Lang.ASSERT_NOT_NULL(snacData, "snacData");

    final long sentMsgId = this.operationRequestId;
    if (sentMsgId == -1)
    {
      CAT.debug("msg error received in no context, generic msgerror handler invoked");
      return false;
    }

    boolean eq = true;
    long tmpId = sentMsgId;
    for (int i = 0; i < 4; i++)
    {
      if ( snacData[9 - i] != (byte)(tmpId & 0xFF) )
      {
        eq = false;
        break;
      }
      tmpId >>= 8;
    }

    if (!eq)
    {
      CAT.debug("unknown msg error, generic msgerror handler invoked");
      return false;
    }

    int errcode = -1;

    if (snacData.length >= 12)
    {
      errcode = aimutil_get16(snacData, 10);
    }
    //last msg gave error
    synchronized (operationResponseLock)
    {
      CAT.debug("last msg returned error, errcode: " + (errcode == -1 ? "-1" : HexUtil.toHexString0x(errcode)));
      this.msgError = true;
      operationResponseLock.notify();
    }
    return true;
  }

  private void unlockSendMsgWait()
  {
    synchronized (operationResponseLock)
    {
      //CAT.debug("logout notified operationResponseLock.wait()");
      operationResponseLock.notify();
    }
  }

  /**
    Send an ICBM (instant message).
    Everything is exactly like libfaim/im.c/aim_send_im_ext(...),
    except for the 0006 0000 flag at the end of the packet,
    which is not applicable to AOL IM.
  */
  private void sendMessage0(final Aim_conn_t conn, final String dstLoginId, final String text, PluginContext ctx) throws MessagingNetworkException, java.io.IOException
  {
    Lang.ASSERT_NOT_NULL(text, "text");
    MLang.EXPECT_NOT_NULL_NOR_EMPTY(text, "text", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    final byte[] msg_bytes = encodeAsAscii7Html(text);
    MLang.EXPECT(msg_bytes.length <= AIMConstants.MAXMSGLEN, "message is too long: htmlencoded message length is " + msg_bytes.length + ", which exceeds a maximum of " + AIMConstants.MAXMSGLEN, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    long msg_id = 6 | ((((long) 0xffff) & this.nextMsgId++) << 16);
    SNAC p = new SNAC(0x0004, 0x0006, 0, 0, msg_id);

    byte[] cookie = new byte[8];
    new Random().nextBytes(cookie);
    p.addByteArray(cookie); //copied from libfaim

    p.addWord(1); //msg channel id //copied from libfaim
    p.addStringPrependedWithByteLength(dstLoginId); //recipient //copied from libfaim
    p.addWord(2); //copied from libfaim

    p.addWord(msg_bytes.length + 0x10);  //copied from libfaim
    p.addDWord(0x05010004);           //copied from libfaim
    p.addWord(0x0101);  //copied from libfaim
    p.addDWord(0x01020101);//copied from libfaim

    p.addWord(msg_bytes.length + 4); //copied from libfaim
    p.addDWord(0x00000000);          //in libfaim: FLAG_ASCII7 => 0x0000 0000 //copied from libfaim
    p.addByteArray(msg_bytes);       //copied from libfaim

    //request server ack for offline recipient
    p.addDWord(0x00060000);

    //request server ack for online recipient
    p.addDWord(0x00030000);//copied from libfaim

    //packet created.

    sendMsgRateControl2_1(dstLoginId, cookie, msg_id, p, conn, ctx);
  }

  private void sendMsgRateControl2_1(String dstLoginId, byte[] msgKuka, long snacRequestId, final SNAC p, final Aim_conn_t conn, PluginContext ctx)
  throws MessagingNetworkException, IOException
  {
    long pauseMillis;
    if (rate2msgsSendTimeQueue.size() >= ICQ2KMessagingNetwork.REQPARAM_ADVANCED_RATECONTROL_RATE2_MAXIMUM_MSGCOUNT)
    {
      Long oldest = (Long) rate2msgsSendTimeQueue.peek();
      pauseMillis = oldest.longValue() + ICQ2KMessagingNetwork.REQPARAM_ADVANCED_RATECONTROL_RATE2_PERIOD_MILLIS - System.currentTimeMillis();
      if (pauseMillis > 0)
      {
        sleep("sendmsg() rate2", pauseMillis);
      }
    }
    try
    {
      synchronized (operationResponseLock)
      {
        this.msgError = false;
        this.operationRequestId = snacRequestId;
        this.msgCookie = msgKuka;
      }

      p.send(conn); //packet sent.

      synchronized (operationResponseLock)
      {
        if (rate2msgsSendTimeQueue.size() >= ICQ2KMessagingNetwork.REQPARAM_ADVANCED_RATECONTROL_RATE2_MAXIMUM_MSGCOUNT)
        {
          //remove the oldest msg
          rate2msgsSendTimeQueue.get();
        }
        rate2msgsSendTimeQueue.put(new Long(System.currentTimeMillis()));

        //waitForOperationResponse(ICQ2KMessagingNetwork.REQPARAM_SENDTEXTMSG_SERVER_RESPONSE_TIMEOUT_SECONDS * 1000, "Error sending text message");

        if (this.msgError)
        {
          fireErrorMessage("ICQ server reports: generic error sending message to "+dstLoginId+".", ctx);
          return;
        }
        /*
        if (!this.sendMsgAckReceived)
        {
          fireErrorMessage("Error sending message to "+dstLoginId+": send timeout expired.", ctx);
          return;
        }
        */
      }
    }
    finally
    {
      synchronized (operationResponseLock)
      {
        this.sendMsgAckReceived = false;
        this.operationRequestId = -1;
        this.msgCookie = null;
        this.msgError = false;
      }
    }
  }

  private void waitForOperationResponse(int timeoutMillis, String errMsg)
  throws MessagingNetworkException
  {
    try {
      CAT.debug("operationResponseLock.wait, src="+loginId+", running="+running);
      operationResponseLock.wait(timeoutMillis);
    } catch (InterruptedException exx) {
      CAT.debug("operationResponseLock.wait interrupted", exx);
      replaceThrowMessagingNetworkException("Error sending text message: interrupted.", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    }

    if (this.shuttingDown)
      replaceThrowMessagingNetworkException("Error sending text message: user selected logout.", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
  }

  /**
    Sends a SNAC header over FLAP channel 2.
  */
  private void sendSnac(Aim_conn_t conn, int family, int subtype, int flags1, int flags2, int snac_data_field_length) throws java.io.IOException, MessagingNetworkException
  {
    sendSnac(conn, family, subtype, flags1, flags2, 0, snac_data_field_length);
  }
  /**
  Sends a SNAC header over FLAP channel 2.
  */
  private void sendSnac(Aim_conn_t conn, int family, int subtype, int flags1, int flags2, long requestId, int snac_data_field_length) throws java.io.IOException, MessagingNetworkException
  {
    conn.write(new FLAPHeader(2, conn.getNextOutputStreamSeqnum(), 10 + snac_data_field_length).byteArray);
    byte[] snacHeader = new byte[10];
    int offset = 0;
    offset += aimutil_put16(snacHeader, offset, family);
    offset += aimutil_put16(snacHeader, offset, subtype);
    offset += aimutil_put8(snacHeader, offset, flags1);
    offset += aimutil_put8(snacHeader, offset, flags2);
    offset += aimutil_put32(snacHeader, offset, requestId);
    conn.write(snacHeader);
  }

  /** Status of contact list entries */
  private void setContactStatus_Oscar(String dstLoginId, int newStatus_Oscar, PluginContext ctx,
    int reasonCategory, String reasonMessage) throws MessagingNetworkException
  {
    log("setContactStatus_Oscar");
    try
    {
      Lang.ASSERT(!this.loginId.equals(dstLoginId), "this.loginId.equals(dstLoginId) must be false here");
      int dstUin = MLang.parseInt_NonNegative(dstLoginId, "dstLoginId", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
      Integer dstUin_ = new Integer(dstUin);
      ContactListItem cli = getContactListItem(dstUin_);
      if (cli == null)
      {
        log("Session.setContactStatus_Oscar(): '" + dstLoginId + "' is not on contact list, statusOscar change ignored");
        return;
      }
      int oldStatus_Oscar = cli.getStatusOscar();
      cli.setStatusOscar(newStatus_Oscar); //marks it as NOT obsolete
      if (oldStatus_Oscar == newStatus_Oscar)
        return;
      //ignore any status events if this Session is already logged out
      if (this.status_Oscar != StatusUtil.OSCAR_STATUS_OFFLINE)
      {
        int oldStatus_MN = StatusUtil.translateStatusOscarToMN(oldStatus_Oscar);
        int newStatus_MN = StatusUtil.translateStatusOscarToMN(newStatus_Oscar);
        if (oldStatus_MN != newStatus_MN)
        {
          fireContactListEntryStatusChangeMN_Uncond(dstLoginId, newStatus_MN, ctx, reasonCategory, reasonMessage);
        }
        else
          log("setStatus request ignored by icq2k plugin: attempted to set the same status");
      }
      else
        log("setStatus request ignored by icq2k plugin: we are offline, hence silent");
    }
    catch (Exception ex)
    {
      handleException(ex, "setContactStatus", ctx);
    }
  }

  protected void fireContactListEntryStatusChangeMN_Uncond(String dstLoginId, int newStatus_MN, PluginContext ctx,
    int reasonCategory, String reasonMessage) throws MessagingNetworkException
  {
    ctx.getICQ2KMessagingNetwork().fireStatusChanged_MN_Uncond(this.loginId, dstLoginId, newStatus_MN,
      reasonCategory, reasonMessage);
  }

  protected void fireSessionStatusChangeMN_Uncond(int newStatus_MN, PluginContext ctx,
    int reasonCategory, String reasonMessage) throws MessagingNetworkException
  {
    ctx.getICQ2KMessagingNetwork().fireStatusChanged_MN_Uncond(this.loginId, this.loginId, newStatus_MN,
      reasonCategory, reasonMessage);
  }

  private void setRunning(boolean newRunning)
  {
    synchronized (logoutLock)
    {
      running = newRunning;
    }
  }
  /**
  To be called from plugin user classes via ICQ2KMessagingNetwork;
  should never be called from other plugin classes.
  <p>
  Only works if already logged in, otherwise throws an AssertException.
  <p>Does not allow setting an OFFLINE status, since
  ICQ2KMessagingNetwork.logout() should be called instead.
  */
  public void setStatus_Oscar_External(int newStatus, PluginContext ctx) throws MessagingNetworkException
  {
    log("setStatus_Oscar_External to " + StatusUtil.translateStatusOscarToString(newStatus) + //
      " (current status: " + StatusUtil.translateStatusOscarToString(this.status_Oscar) + ")");
    CAT.debug("setStatus_Oscar_External", new Exception("dumpStack"));
    try
    {
      ASSERT_LOGGED_IN();
      Lang.ASSERT(newStatus != StatusUtil.OSCAR_STATUS_OFFLINE, "OSCAR_STATUS_OFFLINE should never happen here; use Session.logout() instead.");
      setStatus_Oscar_Internal(newStatus, true, ctx, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED, null);
    }
    catch (Exception ex)
    {
      handleException(ex, "setStatus_Oscar_External", ctx);
    }
  }

  /** Status of the session itself */
  private void setStatus_Oscar_Internal(final int newStatus_Oscar, boolean sendToICQServer, PluginContext ctx,
    int reasonCategory, String reasonMessage)
  throws MessagingNetworkException
  {
    try
    {
      if (sendToICQServer)
        StatusUtil.EXPECT_IS_OSCAR_STATUS(newStatus_Oscar);
      int oldStatus_MN;
      int newStatus_MN;
      synchronized (logoutLock)
      {
        final int oldStatus_Oscar = this.status_Oscar;
        if (oldStatus_Oscar == newStatus_Oscar)
          return;
        this.status_Oscar = newStatus_Oscar;
        oldStatus_MN = StatusUtil.translateStatusOscarToMN(oldStatus_Oscar);
        newStatus_MN = StatusUtil.translateStatusOscarToMN(newStatus_Oscar);
        if (oldStatus_MN != newStatus_MN)
        {
          if (newStatus_Oscar == StatusUtil.OSCAR_STATUS_OFFLINE)
          {
            //contactListUinInt2cli = null;
            synchronized (lastErrorLock)
            {
              if (lastError == null)
              {
                setLastError(new MessagingNetworkException(reasonMessage, reasonCategory));
              }
            }
          }
          fireSessionStatusChangeMN_Uncond(newStatus_MN, ctx, reasonCategory, reasonMessage);
        }
        else
          log("setStatus request ignored by icq2k plugin: attempted to set the same status");
      }
      if (newStatus_Oscar != StatusUtil.OSCAR_STATUS_OFFLINE)
      {
        synchronized (this)
        {
          if (sendToICQServer)
          {
            send_setClientStatus(newStatus_Oscar);
          }
        }
      }
    }
    catch (Exception ex)
    {
      handleException(ex, "setStatus0_Oscar", ctx);
    }
  }
  /**
    Shuts the session and everything down and throws no exceptions.
  */
  void shutdown(PluginContext ctx, int reasonCategory, String reasonMessage)
  {
    synchronized (shuttingDownLock)
    {
      if (shuttingDown)
        return;
      shuttingDown = true;
    }
    log("shutdown("+reasonMessage+")");
    CAT.debug("shutdown", new Exception("dumpStack"));
    try
    {
      logout0(ctx, reasonCategory, reasonMessage);
    }
    catch (Exception ex)
    {
      CAT.debug("ex in logout0", ex);
    }
  }
  private void shutdownAt(long stopTime, String operationDetails, PluginContext ctx)
  throws MessagingNetworkException
  {
    if (System.currentTimeMillis() >= stopTime)
    {
      String s = "icq server operation timed out: " + operationDetails;
      shutdown(ctx, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR, s);
      replaceThrowMessagingNetworkException(s, MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
    }
  }
  void sleep(String activityName, long millis) throws MessagingNetworkException
  {
    try
    {
      log(activityName + ": sleeping " + ((millis / 100) / (float) 10) + " sec.");
      synchronized (logoutLock)
      {
        logoutLock.wait(millis);
      }
    }
    catch (InterruptedException ex)
    {
      CAT.debug(ex.getMessage(), ex);
      replaceThrowMessagingNetworkException("thread interrupted", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    }
    if (shuttingDown)
      replaceThrowMessagingNetworkException("logged out", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
  }
  public static byte[] string2byteArray(String s)
  {
    //if (s == null || s.length() == 0)
    //return new byte[] {};
    //try
    //{
    //return s.getBytes("ISO8859_1");
    //}
    //catch (java.io.UnsupportedEncodingException ex)
    //{
    //CAT.error(ex.getMessage(), ex);
    return s.getBytes();
    //}
  }

  private boolean scheduledSendStatus = false;

  public void setScheduledSendStatus(boolean scheduled)
  {
    //synchronized (scheduledSendStatusLock)
    //{
      this.scheduledSendStatus = scheduled;
    //}
  }

  public boolean isScheduledSendStatus()
  {
    //synchronized (scheduledSendStatusLock)
    //{
      return scheduledSendStatus;
    //}
  }

  private final Object scheduledSendStatusLock = new Object();

  public Object getFireStatusLock()
  {
    return scheduledSendStatusLock;
  }

  protected boolean isTickRunningException = false;

  protected void handleTickException(PluginContext ctx, String reasonMsg)
  {
    isTickRunningException = running;
    shutdown(ctx, MessagingNetworkException.CATEGORY_LOGGED_OFF_DUE_TO_NETWORK_ERROR, reasonMsg);
    isTickRunningException = false;
  }

  /**
    Called periodically by ResourceManager thread(s).
    Returns true if and only if this method should be called as soon as possible
    (e.g. if the socket input stream has data waiting).
  */
  public boolean tick(PluginContext ctx)
  {
    //log("tick()");
    try
    {
      Lang.ASSERT(authconn == null, "authconn must be null here");
      Aim_conn_t bosconn = this.bosconn;
      if (bosconn == null || bosconn.isClosed())
      {
        log("tick(): bosconn is closed, shutting down");
        handleTickException(ctx, "icq server connection interrupted");
        return false;
      }
      ICQ2KMessagingNetwork plugin = ctx.getICQ2KMessagingNetwork();
      if (plugin.isKeepAlivesUsed())
      {
        if (lastKeepaliveMillis + plugin.getKeepAlivesIntervalMillis() <= System.currentTimeMillis())
        {
          sendKeepAlive(bosconn, ctx);
          lastKeepaliveMillis = System.currentTimeMillis();
        }
      }
      try {
        synchronized (getFireStatusLock()) {
          if (scheduledSendStatus) {
            scheduledSendStatus = false;
            long now = System.currentTimeMillis();
            Enumeration e = getContactListItems();
            while (e.hasMoreElements())
            {
              ContactListItem cli = (ContactListItem) e.nextElement();
              long cliTime = cli.getScheduledStatusChangeSendTimeMillis();
              if (now >= cliTime)
              {
                CAT.debug("status delivery delay expired, delivering.");
                cli.setScheduledStatusChangeSendTimeMillis(Long.MAX_VALUE);
                ctx.getICQ2KMessagingNetwork().fireStatusChanged_MN_Uncond(this.loginId, cli.getDstLoginId(), StatusUtil.translateStatusOscarToMN(cli.getStatusOscar()), MessagingNetworkException.CATEGORY_NOT_CATEGORIZED, null);
              }
              else
              if (cliTime != Long.MAX_VALUE && now < cliTime)
              {
                scheduledSendStatus = true;
              }
            }
          }
        }
      }
      catch (Exception ex)
      {
        CAT.debug("ex while scheduledSendStatus, ex ignored", ex);
      }
      final int av = bosconn.available();
      if (av < 0)
      {
        log("tick(): inputStream BUG: bosconn.available() is < 0, shutting down");
        shutdown(ctx, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED, "inputStream BUG");
        return false;
      }
      if (av == 0)
        return false; //no data is waiting
      try
      {
        handleIncomingData(bosconn, ctx);
      }
      catch (java.io.IOException exx)
      {
        CAT.error("ioerror while parsing incoming data, shutting down", exx);
        handleTickException(ctx, "network connection error: " + exx);
        return false;
      }
      catch (Exception ex)
      {
        CAT.debug("non-io exc while parsing incoming data, exc ignored", ex);
      }

      bosconn = this.bosconn;
      return (bosconn != null && !bosconn.isClosed() && bosconn.available() > 0); //data is still waiting
    }
    catch (Exception ex)
    {
      CAT.error("tick(): fatal exception, shutting down", ex);
      shutdown(ctx, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED, "unexpected fatal exception while Session.tick(): "+ex);
      return false;
    }
  }

  public UserDetailsImpl fetchUserDetails(String dstLoginId, PluginContext ctx) throws MessagingNetworkException
  {
    log("fetchUserDetails("+StringUtil.toPrintableString(dstLoginId)+")");
    try
    {
      ASSERT_LOGGED_IN();

      return fetchUserDetails0(dstLoginId, ctx);
    }
    catch (Exception ex)
    {
      handleException(ex, "fetchUserDetails", ctx);
      throw new AssertException("this point is never reached");
    }
  }

  private long waitingForUserDetailsFor = -1;
  private UserDetailsImpl userDetails = null;
  private boolean userDetailsUnknownFormat = false;
  private boolean errorFetchinguserDetails = false;

  private void fireErrorFetchingUserDetails(final byte[] data, PluginContext ctx)
  throws MessagingNetworkException, java.io.IOException
  {
    log("fireErrorFetchingUserDetails() start");
    synchronized (operationResponseLock)
    {
      errorFetchinguserDetails = true;
      operationResponseLock.notify();
    }
  }

  private boolean parseUserDetails(final byte[] data, PluginContext ctx)
  throws MessagingNetworkException, java.io.IOException
  {
    try
    {
      log("parseUserDetails() start");

      /*
        00 15 00 03 00 01
        00 1b 00 02 //snac id
        [0x0a:] 00 01 00 6f //tlv
        [0x0e:] 6d 00 //len
        [0x10:] 8e 95 b5 02 //uin of requester
        [0x14:] da 07  //const
        [0x16:] 34 00  //variable?
        [0x18:] c8 00 //const
        [0x1a:] 0a  //const
        09
        00
        41 61 6e 6e 6b 6b 68 68 "Aannkkhh" //nick
        00
        0c
        00
        45 76 67 65  6e 69 69 2f  4a 6f 65 "Evgenii/Joe"
      */

      synchronized (operationResponseLock)
      {
        if (this.userDetails == null)
        {
          //final long snacId = aimutil_get32(data, 6);
          MLang.EXPECT_EQUAL(aimutil_get16(data, 10), 1, "tlv type", "1", MessagingNetworkException.CATEGORY_STILL_CONNECTED);
          int ofs = 0x10;
          final long requesterUin = aimutil_getIcqUin(data, ofs);
          ofs+= 4;
          MLang.EXPECT_EQUAL(this.uin, requesterUin, "this.uin", "requesterUin", MessagingNetworkException.CATEGORY_STILL_CONNECTED);
          MLang.EXPECT_EQUAL(aimutil_get16(data, ofs), 0xda07, "field1", "0xda07", MessagingNetworkException.CATEGORY_STILL_CONNECTED);
          ofs+= 4; //skip variable field2
          MLang.EXPECT_EQUAL(aimutil_get16(data, ofs), 0xc800, "field3", "0xc800", MessagingNetworkException.CATEGORY_STILL_CONNECTED);
          ofs+= 2;
          MLang.EXPECT_EQUAL(aimutil_get8(data, ofs), 0xa, "field4", "0xa", MessagingNetworkException.CATEGORY_STILL_CONNECTED);
          ofs++;
          int len;

          len = aimutil_get8(data, ofs) - 1;
          if (len < 0) len = 0;
          ofs+= 2; //skip preceding 00
          String nick = byteArray2string(aimutil_getByteArray(data, ofs, len));
          ofs+= len+1; //skip trailing 00

          len = aimutil_get8(data, ofs) - 1;
          if (len < 0) len = 0;
          ofs+= 2; //skip preceding 00
          String firstName = byteArray2string(aimutil_getByteArray(data, ofs, len));
          ofs+= len+1; //skip trailing 00

          len = aimutil_get8(data, ofs) - 1;
          if (len < 0) len = 0;
          ofs+= 2; //skip preceding 00
          String lastName = byteArray2string(aimutil_getByteArray(data, ofs, len));
          ofs+= len+1; //skip trailing 00

          len = aimutil_get8(data, ofs) - 1;
          if (len < 0) len = 0;
          ofs+= 2; //skip preceding 00
          String email = byteArray2string(aimutil_getByteArray(data, ofs, len));
          ofs+= len+1; //skip trailing 00

          this.userDetails = new UserDetailsImpl(
            StringUtil.mkNull(nick.trim()),
            StringUtil.mkNull( (firstName.trim() + " " + lastName.trim()).trim() ),
            StringUtil.mkNull(email.trim()));

          operationResponseLock.notify();
          return true;
        }
        else
          log("parseUserDetails: extra info ignored");
      }
    }
    catch (MessagingNetworkException ex)
    {
      CAT.debug("userDetails response unknown format", ex);
      synchronized (operationResponseLock)
      {
        this.userDetailsUnknownFormat = true;
        operationResponseLock.notify();
      }
    }
    return false;
  }

  private UserDetailsImpl fetchUserDetails0(String dstLoginId, PluginContext ctx) throws MessagingNetworkException, IOException
  {
    /*
      request to fetch from our online icq plugin user
        requester uin = 26500286  (0x01 94 5C BE)

      00 15 / 00 02 //snac family/subtype
      00 00 //snac flags
      00 0D 00 02 //snac request id

      00 01 / 00 10 //tlv
         [00:] 0E 00 //length?
         [02:] BE 5C 94 01 //requester uin = 26500286  (0x01 94 5C BE)
         [06:] D0 07 34 00 B2 04 //?
         [0C:] xx xx xx xx //requested uin //in the same format
    */

    final long id = ((snac_nextid++ & 0xFFFF) << 16) | 2;
    SNAC p = new SNAC(0x0015, 0x0002, 0, 0, id);
    p.addWord(0x0001);
    p.addWord(0x0010);
    p.addWord(0x0e00);
    p.addIcqUin(uin); //requester
    p.addWord(0xd007);
    p.addWord(0x3400);
    p.addWord(0xb204);

    long dstUin = MLang.parseLong(dstLoginId, "dstLoginId", MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);

    p.addIcqUin(dstUin); //requested
    try
    {
      synchronized (operationResponseLock)
      {
        this.operationRequestId = id;
        this.waitingForUserDetailsFor = dstUin;
        this.userDetails = null;
        this.userDetailsUnknownFormat = false;
        this.errorFetchinguserDetails = false;
      }

      p.send(getBosConnNotNull()); //packet sent.

      synchronized (operationResponseLock)
      {
        waitForOperationResponse(ICQ2KMessagingNetwork.REQPARAM_SERVER_RESPONSE_TIMEOUT1_SECONDS * 1000, "Error retrieving user info for "+dstLoginId);

        UserDetailsImpl result = this.userDetails;
        if (result == null)
        {
          if (this.userDetailsUnknownFormat)
            throw new MessagingNetworkException("can't get userinfo for "+dstLoginId+": cannot parse packet.", MessagingNetworkException.CATEGORY_STILL_CONNECTED);
          else
          if (this.errorFetchinguserDetails)
            throw new MessagingNetworkException("icq server refuses to get userinfo for "+dstLoginId+".", MessagingNetworkException.CATEGORY_STILL_CONNECTED);
          else
            throw new MessagingNetworkException("can't get userinfo for "+dstLoginId+": response timeout expired.", MessagingNetworkException.CATEGORY_STILL_CONNECTED);
        }
        else
        {
          return result;
        }
      }
    }
    finally
    {
      synchronized (operationResponseLock)
      {
        this.waitingForUserDetailsFor = -1;
        this.userDetails = null;
        this.userDetailsUnknownFormat = false;
        this.operationRequestId = -1;
        this.errorFetchinguserDetails = false;
      }
    }
  }

  private void sendContacts0(final Aim_conn_t conn, final String dstLoginId, final String[] nicks, final String[] loginIds, PluginContext ctx)
  throws MessagingNetworkException, java.io.IOException
  {
    Lang.ASSERT_EQUAL(nicks.length, loginIds.length, "nicks.length", "loginIds.length");
    //2î11111111înick1î22222222înick2î
    ByteArrayOutputStream bas = new ByteArrayOutputStream(128);
    int i = 0;
    bas.write(Integer.toString(nicks.length).getBytes());
    bas.write((byte) 0xfe);
    while (i < nicks.length)
    {
      bas.write(string2byteArray(loginIds[i]));
      bas.write((byte) 0xfe);
      bas.write(string2byteArray(nicks[i]));
      bas.write((byte) 0xfe);
      i++;
    }
    sendContacts0(conn, dstLoginId, bas.toByteArray(), ctx);
  }

  private void sendContacts0(final Aim_conn_t conn, final String dstLoginId, final byte[] text, PluginContext ctx)
  throws MessagingNetworkException, java.io.IOException
  {
    Lang.ASSERT_NOT_NULL(text, "text");
    Lang.ASSERT_POSITIVE(text.length, "text.length");
    final byte[] msg_bytes = text;
    MLang.EXPECT(msg_bytes.length <= AIMConstants.MAXMSGLEN, "contacts message is too long: length==" + msg_bytes.length + ", which exceeds a maximum of " + AIMConstants.MAXMSGLEN, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
    long msg_id = 6 | ((((long) 0xffff) & this.nextMsgId++) << 16);
    SNAC p = new SNAC(0x0004, 0x0006, 0, 0, msg_id);

    // 0004 0006  0000 0010 0006   33 46 3e 04 af 09 00 00   00 02   08 3x 3x 3x 3x 3x 3x 3x 3x 00 05   00dc   00 00    33 46 3e 04 af 09 00 00    09 46 13 49 4c 7f 11 d1 82 22 44 45 53 54 00 00 00 0a 00 02 00 01 00 0f 00 00 27 11   00b4   1b 00 07 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 03 00 00 00 00 fa ff 0e 00 fa ff 00 00 00 00 00 00 00 00 00 00 00   0013     00 00 00 01   007f                           00           38 fe 31 38 35 37 33 36 37 34 fe 61 6e 74 69 63 68 fe 36 32 36 30 38 36 31 34 fe 61 72 74 fe 33 30 38 33 32 34 32 32 fe 42 61 62 61 6b fe 31 31 31 33 32 36 32 31 39 fe 62 65 6e 6e 20 67 75 6e fe 39 34 38 37 38 38 34 30 fe 64 72 6e 61 6d 65 fe 32 32 39 30 32 35 35 32 fe 65 6c fe 37 37 30 34 37 34 37 31 fe 66 6d 69 6b 65 fe 36 33 32 36 31 36 33 35 fe 47 72 65 65 6e 6d 61 6e fe 00 00 03 00 00    -- eo l
    // f/s------  ---- ---------   kuka-------------------   CONST   recipient----------------- CONST   len1   CONST    kuka-------------------    CONST------------------------------------------------------------------------------   len2   CONST--------------------------------------------------------------------------- decid CONST decid CONST---------------------------   msgkind  CONST------   len3==(msg_bytes.length+1)     ENCODING     msg_bytes

    byte[] cookie = this.msgCookie = new byte[8];
    new Random().nextBytes(cookie);
    p.addByteArray(cookie);
    p.addWord(2); //msg channel id aka msgfmt
    p.addStringPrependedWithByteLength(dstLoginId); //recipient
    p.addWord(5);

    p.addWord(msg_bytes.length - 0x59 + 0xB7); //len1
    p.addWord(0);
    p.addByteArray(cookie);
    p.addByteArray(new byte[] {(byte) 0x09, (byte) 0x46, (byte) 0x13, (byte) 0x49, (byte) 0x4c, (byte) 0x7f, (byte) 0x11, (byte) 0xd1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45, (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0f, (byte) 0x00, (byte) 0x00, (byte) 0x27, (byte) 0x11});
    p.addWord(msg_bytes.length - 0x59 + 0x8F); //len2
    p.addByteArray(new byte[] {(byte) 0x1b, (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
    p.addWord(0xfdff); //decid
    p.addWord(0x0e00); //const
    p.addWord(0xfdff); //decid
    p.addByteArray(new byte[] {(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00});
    p.addWord(0x0013); //msgkind
    p.addDWord(1); //const
    p.addWord(msg_bytes.length + 1); //len3
    p.addByte(0); //encoding
    p.addByteArray(msg_bytes);       //copied from libfaim
    p.addByte(0); //const

    //request server ack for offline recipient
    p.addDWord(0x00060000);

    //request server ack for online recipient
    p.addDWord(0x00030000);

    //packet created.

    sendMsgRateControl2_1(dstLoginId, cookie, msg_id, p, conn, ctx);
  }
}