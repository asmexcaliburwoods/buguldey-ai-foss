package org.jcq2k.test;

import org.jcq2k.util.joe.*;

public class ICQMessagingTest
{
  public static String REQPARAM_SRC_LOGIN_ID;
  public static String REQPARAM_SRC_PASSWORD;
  public static String REQPARAM_DST_LOGIN_ID;
  public static String REQPARAM_CONTACT_LIST_ENTRY_LOGIN_ID;
  public static String REQPARAM_CONTACT_LIST_LOGIN_IDS;

  public static String REQPARAM_MESSAGING_NETWORK_IMPL_CLASS_NAME;

  static
  {
    AutoConfig.fetchFromClassLocalResourceProperties(ICQMessagingTest.class, true, false);
  }
}
