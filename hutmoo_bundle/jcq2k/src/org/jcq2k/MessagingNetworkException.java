package org.jcq2k;

import org.jcq2k.util.joe.*;

public class MessagingNetworkException extends Exception
{
  public final static int CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_PLUGIN_LOGOUT_CALLER = 0;
  public final static int CATEGORY_LOGGED_OFF_YOU_LOGGED_ON_FROM_ANOTHER_COMPUTER = 1;
  public final static int CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR = 2;
  public final static int CATEGORY_LOGGED_OFF_DUE_TO_NETWORK_ERROR = 3;
  public final static int CATEGORY_STILL_CONNECTED = 4;
  public final static int CATEGORY_NOT_CATEGORIZED = -1;

  private final int category;

  public MessagingNetworkException(int category)
  {
    this(null, category);
  }

  public MessagingNetworkException(String s, int category)
  {
    super(s);
    switch (category)
    {
      case CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_PLUGIN_LOGOUT_CALLER :
      case CATEGORY_LOGGED_OFF_YOU_LOGGED_ON_FROM_ANOTHER_COMPUTER :
      case CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR :
      case CATEGORY_LOGGED_OFF_DUE_TO_NETWORK_ERROR :
      case CATEGORY_STILL_CONNECTED :
      case CATEGORY_NOT_CATEGORIZED :
        this.category = category;
        break;
      default:
        throw new AssertException("invalid category: "+category);
    }
  }

  public int getCategory()
  {
    return category;
  }

  public String getCategoryMessage()
  {
    return getCategoryMessage(category);
  }

  public static String getCategoryMessage(int category)
  {
    String c;
    switch (category)
    {
      case CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_PLUGIN_LOGOUT_CALLER :
        c = "logged off on behalf of the caller";
        break;
      case CATEGORY_LOGGED_OFF_YOU_LOGGED_ON_FROM_ANOTHER_COMPUTER :
        c = "you logged on from another computer";
        break;
      case CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR :
        c = "logged off on behalf of messaging server or due to a protocol violation";
        break;
      case CATEGORY_LOGGED_OFF_DUE_TO_NETWORK_ERROR :
        c = "logged off due to network error";
        break;
      case CATEGORY_STILL_CONNECTED :
        c = "non-fatal error, user has not been been logged off";
        break;
      case CATEGORY_NOT_CATEGORIZED :
        c = "no category";
        break;
      default:
        c = "BUGGGG: invalid category: "+category;
        break;
    }
    return c;
  }

  public String toString()
  {
    return super.toString() + " [" + getCategoryMessage() + "]";
  }
}