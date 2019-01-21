package org.jcq2k.icq2k;

import org.jcq2k.*;
import org.jcq2k.util.*;
import org.jcq2k.util.joe.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
  Provides connectivity with the public ICQ service of
  America Online, Inc., using the protocol of ICQ2000a/b clients,
  called AOL OSCAR protocol.
  <p>
  <b>Basics of OSCAR.</b>
  <p>
  Login stage.
  <ul>
  <li>The plugin connects to the authorization/login
  service of AOL, usually located at login.icq.com:5190.
  <li>Plugin sends the icq number and password.
  <li>Auth service sends either some authorization error, or,
  altenatively,
  <li>it sends host and port of <i>basic OSCAR service</i>
  ("BOS"), and a 256-byte authorization cookie.
  <li>Here, either plugin or server can close the auth connection socket.
  </ul>
  <p>
  Main stage: handshake.
  <ul>
  <li>The plugin connects to the BOS service using
  the host and port received.
  <li>Plugin sends the authorization cookie.
  <li>Plugin sends a contact list.
  <li>Plugin performs some necessary actions: it sends and receives
  some packets of unknown purpose.
  (Otherwise it will be disconnected.)
  <li>Plugin sets its online status.
  <li>Plugin is now connected.
  </ul>
  <p>
  Main stage: life.
  <ul>
  <li>Plugin can send plaintext messages to BOS.
  <li>Plugin can send client status change messages to BOS.
  <li>Plugin can add or remove contact list items,
  and send messages about that to BOS.
  <li>The BOS server can send plaintext messages to a plugin.
  <li>The BOS server can send contact list items status change
  messages to a plugin.
  <li>The BOS server can send error messages to a plugin.
  (They are converted into plain text messages of
  MessagingNetwork.)
  </ul>
  <p>
  Main stage: death.
  <ul>
  <li>To disconnect from any AOL service connection (including
  auth one, and BOS one), it is safe to just close
  the TCP/IP socket.
  <li>The TCP/IP socket can be closed.
  This means session death.
  <li>There can be any IO error in the TCP/IP socket.
  This means session death, too.
  </ul>
  <p>
  For more information about the OSCAR protocol, see
  the <a href=http://www.zigamorph.net/faim/protocol/>libfaim
  documentation</a>.  The same documentation should be in javaCard CVS.

  <p>
  @see ServeSessionsThread
  @see Session
  @see ResourceManager
  @see SNACFamilies
*/

public class ICQ2KMessagingNetwork implements MessagingNetwork
{

  private final static org.log4j.Category CAT = org.log4j.Category.getInstance(ICQ2KMessagingNetwork.class.getName());
  static final boolean ENFORCE_ICQ_VALIDATION = false;

  /**
    30 is a good value for this config parameter.
    @see AutoConfig
    @see Session#fetchUserDetails(...)
  */
  public static int REQPARAM_SERVER_RESPONSE_TIMEOUT1_SECONDS;

  /**
    500 is a good value for this config parameter.
    @see AutoConfig
    @see Session#sendMessage0(Aim_conn_t, String, String)
  */
  public static long REQPARAM_ADVANCED_RATECONTROL_SENDPACKET_MILLIS;

  /**
    60000 is a good value for this config parameter.
    @see AutoConfig
    @see Session#sendMessage0(Aim_conn_t, String, String)
  */
  public static long REQPARAM_ADVANCED_RATECONTROL_RATE2_PERIOD_MILLIS;

  /**
    10 is a good value for this config parameter.
    @see AutoConfig
    @see Session#sendMessage0(Aim_conn_t, String, String)
  */
  public static int REQPARAM_ADVANCED_RATECONTROL_RATE2_MAXIMUM_MSGCOUNT;

  /**
    30 is good value for this config param.
    @see AutoConfig
    @see Session#sendMessage0(Aim_conn_t, String, String)
  */
  public static int REQPARAM_SENDTEXTMSG_SERVER_RESPONSE_TIMEOUT_SECONDS;

  static
  {
    AutoConfig.fetchFromClassLocalResourceProperties(ICQ2KMessagingNetwork.class, true, false);
    if (REQPARAM_ADVANCED_RATECONTROL_SENDPACKET_MILLIS <= 0)
      throw new RuntimeException("REQPARAM_ADVANCED_RATECONTROL_SENDPACKET_MILLIS autoconfig property must be positive, but it is " + REQPARAM_ADVANCED_RATECONTROL_SENDPACKET_MILLIS);
    if (REQPARAM_ADVANCED_RATECONTROL_RATE2_MAXIMUM_MSGCOUNT <= 0)
      throw new RuntimeException("REQPARAM_ADVANCED_RATECONTROL_RATE2_MAXIMUM_MSGCOUNT autoconfig property must be positive, but it is " + REQPARAM_ADVANCED_RATECONTROL_RATE2_MAXIMUM_MSGCOUNT);
    if (REQPARAM_ADVANCED_RATECONTROL_RATE2_PERIOD_MILLIS <= 0)
      throw new RuntimeException("REQPARAM_ADVANCED_RATECONTROL_RATE2_PERIOD_MILLIS autoconfig property must be positive, but it is " + REQPARAM_ADVANCED_RATECONTROL_RATE2_PERIOD_MILLIS);
    if (REQPARAM_SENDTEXTMSG_SERVER_RESPONSE_TIMEOUT_SECONDS <= 0)
      throw new RuntimeException("REQPARAM_SENDTEXTMSG_SERVER_RESPONSE_TIMEOUT_SECONDS autoconfig property must be positive, but it is " + REQPARAM_SENDTEXTMSG_SERVER_RESPONSE_TIMEOUT_SECONDS);
  }

  //
  public static final byte ICQ2K_NETWORK_ID = 1;
  public static final String ICQ2K_NETWORK_NAME = "ICQ";
  private final Vector messagingNetworkListeners = new Vector(1, 1);
  private final static int loginServerPort;
  protected final PluginContext context = new PluginContext(this);
  protected final ResourceManager resourceManager = makeResourceManagerInstance();
  private final static InetAddress loginServerInetAddress;

  final static InetAddress socksProxyInetAddress;
  final static int socksProxyPort;
  final static String socksProxyUserName;
  final static String socksProxyPassword;
  final static boolean keepAlivesUsed;
  final static int keepAlivesIntervalMillis;
  final static long serverResponseTimeoutMillis;
  final static long socketTimeoutMillis;
  private final static Properties props;

  static
  {
    try
    {
      final String name = "/" + ICQ2KMessagingNetwork.class.getName().replace('.', '/') + ".properties";
      String propFileDisplayName = "\"" + name + "\" resource";
      java.io.InputStream props_is = ICQ2KMessagingNetwork.class.getResourceAsStream(name);
      if (props_is == null)
        throw new RuntimeException(propFileDisplayName + " must be present in classpath, but the resource does not exist.");
      props = new Properties();
      try
      {
        props.load(props_is);
      }
      catch (java.io.IOException iex)
      {
        CAT.error("ioerror while loading properties resource file " + name, iex);
        throw new RuntimeException("Error while loading properties from " + propFileDisplayName + ":\r\n" + iex);
      }
      finally
      {
        try
        {
          props_is.close();
        }
        catch (Exception ex)
        {
        }
      }
      CAT.info("" + ICQ2KMessagingNetwork.class.getName() + " class startup properties: " + props);
      String loginServerHost = PropertyUtil.getRequiredProperty(props, propFileDisplayName, "icq.server.login.host");
      loginServerPort = PropertyUtil.getRequiredPropertyInt(props, propFileDisplayName, "icq.server.login.port");
      loginServerInetAddress = java.net.InetAddress.getByName(loginServerHost);
      serverResponseTimeoutMillis = PropertyUtil.getRequiredPropertyInt(props, propFileDisplayName, "server.response.timeout.seconds") * 1000;
      if (serverResponseTimeoutMillis <= 0)
        throw new RuntimeException("Invalid property value for server.response.timeout.seconds: " + (int) (serverResponseTimeoutMillis / 1000) + ", must be positive.");
      socketTimeoutMillis = PropertyUtil.getRequiredPropertyInt(props, propFileDisplayName, "socket.timeout.seconds") * 1000;
      if (socketTimeoutMillis < 0)
        throw new RuntimeException("Invalid property value for socket.timeout.seconds: " + (int) (socketTimeoutMillis / 1000) + ", must be non-negative.");
      boolean socksProxyUsed = PropertyUtil.getRequiredPropertyBoolean(props, propFileDisplayName, "socks5.proxy.used");
      if (socksProxyUsed)
      {
        String socksProxyHost = PropertyUtil.getRequiredProperty(props, propFileDisplayName, "socks5.proxy.host");
        socksProxyInetAddress = InetAddress.getByName(socksProxyHost);
        socksProxyPort = PropertyUtil.getRequiredPropertyInt(props, propFileDisplayName, "socks5.proxy.port");
        boolean socksProxyUsernameAuthUsed = PropertyUtil.getRequiredPropertyBoolean(props, propFileDisplayName, "socks5.proxy.username.auth.used");
        if (socksProxyUsernameAuthUsed)
        {
          socksProxyUserName = PropertyUtil.getRequiredProperty(props, propFileDisplayName, "socks5.proxy.username");
          socksProxyPassword = PropertyUtil.getRequiredProperty(props, propFileDisplayName, "socks5.proxy.password");
        }
        else
        {
          socksProxyUserName = null;
          socksProxyPassword = null;
        }
      }
      else
      {
        socksProxyInetAddress = null;
        socksProxyPort = 0;
        socksProxyUserName = null;
        socksProxyPassword = null;
      }
      keepAlivesUsed = PropertyUtil.getRequiredPropertyBoolean(props, propFileDisplayName, "keepalives.used");
      if (keepAlivesUsed)
      {
        int keepAlivesIntervalSeconds = PropertyUtil.getRequiredPropertyInt(props, propFileDisplayName, "keepalives.interval.seconds");
        if (keepAlivesIntervalSeconds < 30)
          throw new RuntimeException("Invalid property value for keepalives.interval.seconds: " + keepAlivesIntervalSeconds + ", must be >= 30.");
        keepAlivesIntervalMillis = 1000 * keepAlivesIntervalSeconds;
      }
      else
      {
        keepAlivesIntervalMillis = 0;
      }
    }
    catch (RuntimeException ex)
    {
    CAT.error("invalid resource properties for icq2k", ex);
      throw ex;
    }
    catch (java.net.UnknownHostException ex2)
    {
    CAT.error("cannot resolve host specified in resource properties for icq2k", ex2);
      throw new RuntimeException("" + ex2);
    }
  }

  public ICQ2KMessagingNetwork()
  {
  }

  public final void addMessagingNetworkListener(MessagingNetworkListener l)
  {
        Lang.ASSERT_NOT_NULL(l, "listener");
        synchronized (messagingNetworkListeners)
        {
          messagingNetworkListeners.removeElement(l);
          messagingNetworkListeners.addElement(l);
        }
  }

  public void addToContactList(String srcLoginId, String dstLoginId) throws MessagingNetworkException
  {
      srcLoginId = normalizeLoginId(srcLoginId);
      dstLoginId = normalizeLoginId(dstLoginId);
      Session session = getSessionNotNull(srcLoginId);
      synchronized (session)
      {
          session.addToContactList(dstLoginId, context);
      }
  }

  void fireMessageReceived(String srcLoginId, String dstLoginId, String messageText)
  {
    srcLoginId = normalizeLoginId(srcLoginId);
    dstLoginId = normalizeLoginId(dstLoginId);
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(srcLoginId, "srcLoginId");
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(dstLoginId, "dstLoginId");
    Lang.ASSERT_NOT_NULL(messageText, "messageText");

    //
    synchronized (messagingNetworkListeners)
    {
      for (int i = 0; i < messagingNetworkListeners.size(); i++)
      {
        MessagingNetworkListener l = (MessagingNetworkListener) messagingNetworkListeners.elementAt(i);
        Session ses = getSession0(dstLoginId);
        Lang.ASSERT_NOT_NULL(ses, "session for " + dstLoginId);
        //synchronized (ses) //commented out because of deadlock bug
        //{
        CAT.debug("ICQ FIRES EVENT to core: messageReceived: src " + srcLoginId + ", dst " + dstLoginId + ",\r\ntext "+StringUtil.toPrintableString(messageText)+",\r\nlistener: " + l);
        l.messageReceived(getNetworkId(), srcLoginId, dstLoginId, messageText);
        //}
      }
    }
  }

  void fireContactsReceived(String srcLoginId, String dstLoginId, String[] contactsLoginIds, String[] contactsNicks)
  {
    srcLoginId = normalizeLoginId(srcLoginId);
    dstLoginId = normalizeLoginId(dstLoginId);
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(srcLoginId, "srcLoginId");
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(dstLoginId, "dstLoginId");
    Lang.ASSERT_NOT_NULL(contactsLoginIds, "contactsLoginIds");
    Lang.ASSERT_NOT_NULL(contactsNicks, "contactsNicks");
    Lang.ASSERT_EQUAL(contactsLoginIds.length, contactsNicks.length, "contactsLoginIds.length", "contactsNicks.length");

    synchronized (messagingNetworkListeners)
    {
      for (int i = 0; i < messagingNetworkListeners.size(); i++)
      {
        MessagingNetworkListener l = (MessagingNetworkListener) messagingNetworkListeners.elementAt(i);
        Session ses = getSession0(dstLoginId);
        Lang.ASSERT_NOT_NULL(ses, "session for " + dstLoginId);
        //synchronized (ses) //commented out because of deadlock bug
        //{
        CAT.debug("ICQ FIRES EVENT to core: contactsReceived: src " + srcLoginId + ", dst " + dstLoginId + ",\r\nnumber of contacts "+contactsLoginIds.length+",\r\nlistener: " + l);
        l.contactsReceived(getNetworkId(), srcLoginId, dstLoginId, contactsLoginIds, contactsNicks);
        //}
      }
    }
  }

  /**
    Unconditionally fires a status change event.
  */
  void fireStatusChanged_MN_Uncond(String srcLoginId, String dstLoginId, int status_mn, int reasonCategory, String reasonMessage) throws MessagingNetworkException
  {
    srcLoginId = normalizeLoginId(srcLoginId);
    dstLoginId = normalizeLoginId(dstLoginId);
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(srcLoginId, "srcLoginId");
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(dstLoginId, "dstLoginId");
    org.jcq2k.util.MLang.EXPECT_IS_MN_STATUS(status_mn, "status_mn");

    CAT.debug("fireStatusChanged_MN_Uncond", new Exception("dumpStack"));

    synchronized (messagingNetworkListeners)
    {
      Session sess = getSession0(srcLoginId);
      if (sess != null)
      {
        if (status_mn == MessagingNetwork.STATUS_OFFLINE && srcLoginId.equals(dstLoginId))
        {
          sess.shutdown(context, reasonCategory, reasonMessage);
          getResourceManager().removeSession(sess);
        }

        for (int i = 0; i < messagingNetworkListeners.size(); i++)
        {
          MessagingNetworkListener l = (MessagingNetworkListener) messagingNetworkListeners.elementAt(i);
          //src can be already logged off; no session
          //synchronized (getSessionLock(srcLoginId)) {
          CAT.debug("ICQ FIRES EVENT to core: statusChanged: src " + srcLoginId + " dst " + dstLoginId + ",\r\nstatus: "+StatusUtil.translateStatusMNToString(status_mn)+", listener: " + l);
          l.statusChanged(getNetworkId(), srcLoginId, dstLoginId, status_mn, reasonCategory, reasonMessage);
          //}
        }
      }
      else
        CAT.debug("fireSttChg_Uncond: session is null, statusChange to "+StatusUtilMN.translateStatusMNToString(status_mn)+" ignored");
    }
  }
  public int getClientStatus(String srcLoginId) throws MessagingNetworkException
  {
      srcLoginId = normalizeLoginId(srcLoginId);
      Session session = getSession0(srcLoginId);
      if (session == null)
          return STATUS_OFFLINE;
      synchronized (session)
      {
          return StatusUtil.translateStatusOscarToMN(session.getStatus_Oscar());
      }
  }
  /**
    Get comment for network.
  */
  public String getComment() {
    return "";
  }
  final static int getKeepAlivesIntervalMillis() {
      return keepAlivesIntervalMillis;
  }
  /**
    Authorization server host.  Specified in resource properties.
  */
  final static java.net.InetAddress getLoginServerInetAddress() {
      return loginServerInetAddress;
  }
  /**
    Authorization server port.  Specified in resource properties.
  */
  final static int getLoginServerPort() {
      return loginServerPort;
  }
  /**
   Returns the network name.
  */
  public String getName()
  {
      return ICQ2K_NETWORK_NAME;
  }
  public byte getNetworkId()
  {
      return ICQ2K_NETWORK_ID;
  }
  final ResourceManager getResourceManager()
  {
    return resourceManager;
  }
  protected ResourceManager makeResourceManagerInstance()
  {
    return new ResourceManager(context);
  }
  public long getServerResponseTimeoutMillis()
  {
    return serverResponseTimeoutMillis;
  }
  /**
    Can return null, srcLoginId should never be null.
  */
  public Session getSession(String loginId)
  {
    return getSession0(loginId);
  }

  private Session getSession0(String loginId)
  {
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(loginId, "loginId");
    return getResourceManager().getSession(loginId);
  }

  private Session getSessionNotNull(String loginId) throws MessagingNetworkException
  {
    Lang.ASSERT_NOT_NULL_NOR_EMPTY(loginId, "loginId");
    return getResourceManager().getSessionNotNull(loginId);
  }

  public long getSocketTimeoutMillis()
  {
    return socketTimeoutMillis;
  }

  public int getStatus(String srcLoginId, String dstLoginId) throws MessagingNetworkException
  {
      if (srcLoginId == null || dstLoginId == null)
      {
          throw new AssertException("\r\nUnable to get status: invalid(null) arguments in a call");
      }
      srcLoginId = normalizeLoginId(srcLoginId);
      dstLoginId = normalizeLoginId(dstLoginId);
      Session session = getSessionNotNull(srcLoginId);
      return StatusUtil.translateStatusOscarToMN(session.getContactStatus_Oscar(dstLoginId, context));
  }
  final static boolean isKeepAlivesUsed() {
      return keepAlivesUsed;
  }
  public void login(String srcLoginId, String password, java.lang.String[] contactList, int statusMN) throws MessagingNetworkException
  {
      org.jcq2k.util.MLang.EXPECT_IS_MN_STATUS(statusMN, "loginStatusMN");
      Lang.ASSERT(statusMN != MessagingNetwork.STATUS_OFFLINE, "cannot call login(..., STATUS_OFFLINE)");

      CAT.debug("login", new Exception("dumpStack"));

      //
      srcLoginId = normalizeLoginId(srcLoginId);
      Session session = getResourceManager().createSession(srcLoginId);
      synchronized (session)
      {
          try
          {
              session.login_Oscar(password, contactList, StatusUtil.translateStatusMNToOscar(statusMN), context);
          }
          finally
          {
              try
              {
                  if (session.getStatus_Oscar() == StatusUtil.OSCAR_STATUS_OFFLINE)
                  {
                      getResourceManager().removeSession(session);
                  }
              }
              catch (Exception ex)
              {
                CAT.debug("unexpected exception while login(), exception ignored", ex);
              }
          }
      }
  }

  public void logout(String srcLoginId) throws MessagingNetworkException
  {
      setClientStatus(srcLoginId, MessagingNetwork.STATUS_OFFLINE);
  }

  private static String normalizeLoginId(String loginId)
  {
      Lang.ASSERT_NOT_NULL(loginId, "loginId");
      return loginId.trim();
  }


  public void removeFromContactList(String srcLoginId, String dstLoginId) throws MessagingNetworkException
  {
      srcLoginId = normalizeLoginId(srcLoginId);
      dstLoginId = normalizeLoginId(dstLoginId);
      Session session = getSessionNotNull(srcLoginId);
      synchronized (session)
      {
          session.removeFromContactList(dstLoginId, context);
      }
  }

  public final void removeMessagingNetworkListener(MessagingNetworkListener l)
  {
        Lang.ASSERT_NOT_NULL(l, "listener");
        messagingNetworkListeners.removeElement(l);
  }

  public void sendMessage(String srcLoginId, String dstLoginId, String text)
  throws MessagingNetworkException
  {
    srcLoginId = normalizeLoginId(srcLoginId);
    dstLoginId = normalizeLoginId(dstLoginId);
    Session session = getSessionNotNull(srcLoginId);
    synchronized(session)
    {
      session.sendMessage(dstLoginId, text, context);
    }
  }

  public void sendContacts(String srcLoginId, String dstLoginId, String[] nicks, String[] loginIds)
  throws MessagingNetworkException
  {
    srcLoginId = normalizeLoginId(srcLoginId);
    dstLoginId = normalizeLoginId(dstLoginId);
    Session session = getSessionNotNull(srcLoginId);
    synchronized(session)
    {
      session.sendContacts(dstLoginId, nicks, loginIds, context);
    }
  }

  public void setClientStatus(String srcLoginId, int status_mn) throws MessagingNetworkException
  {
    org.jcq2k.util.MLang.EXPECT_IS_MN_STATUS(status_mn, "loginStatusMN");
    srcLoginId = normalizeLoginId(srcLoginId);
    Session session;

    CAT.debug("[" + srcLoginId + "] setClientStatus to "+StatusUtil.translateStatusMNToString(status_mn));
    CAT.debug("setClientStatus", new Exception("dumpStack"));

    if (status_mn == MessagingNetwork.STATUS_OFFLINE)
    {
      session = getSession0(srcLoginId);
      if (session == null)
      {
        CAT.warn("icq2k [" + srcLoginId + "] logout: session already logged out, request ignored");
        return;
      }
      session.setLastError(new MessagingNetworkException("logout requested by caller", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_PLUGIN_LOGOUT_CALLER));
    }
    else
    {
      session = getSessionNotNull(srcLoginId);
    }
    setClientStatus0(session, status_mn);
  }
  private void setClientStatus0(Session session, int status_mn) throws MessagingNetworkException
  {
    if (status_mn == MessagingNetwork.STATUS_OFFLINE)
    {
      //logoff is not synchronized (almost)
      session.logout(context,
        MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_PLUGIN_LOGOUT_CALLER,
        "logout requested by caller");
      synchronized (session)
      {
        getResourceManager().removeSession(session);
      }
    }
    else
    {
      //change status to non-offline is synchronized
      synchronized (session)
      {
        session.setStatus_Oscar_External(StatusUtil.translateStatusMNToOscar(status_mn), context);
      }
    }
  }

  public UserDetails getUserDetails(String srcLoginId, String dstLoginId) throws MessagingNetworkException
  {
    srcLoginId = normalizeLoginId(srcLoginId);
    dstLoginId = normalizeLoginId(dstLoginId);
    Session session = getSessionNotNull(srcLoginId);
    synchronized (session)
    {
      return session.fetchUserDetails(dstLoginId, context);
    }
  }

  public void init()
  {
    CAT.info("icq2k: init()");
    getResourceManager().init();
    CAT.info("icq2k: init() done");
  }
  public void deinit()
  {
    CAT.info("icq2k: deinit()");
    messagingNetworkListeners.clear();
    getResourceManager().deinit();
    CAT.info("icq2k: deinit() done");
  }
}
