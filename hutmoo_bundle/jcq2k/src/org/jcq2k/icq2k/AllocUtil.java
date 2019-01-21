package org.jcq2k.icq2k;

import org.jcq2k.*;
/**
Utility class for safe allocation of byte arrays.
<p>
Useful when the length for the byte array is received
from network.
*/
public final class AllocUtil
{
  public final static int BYTE_ARRAY_SIZE_MAX = 10240 - 1;
  public static byte[] createByteArray(Session ses, int size) throws MessagingNetworkException
  {
    if (size < 0 || size > BYTE_ARRAY_SIZE_MAX)
      ses.replaceThrowMessagingNetworkException("invalid length for byte array: " + size + ", must be [0..." + BYTE_ARRAY_SIZE_MAX + "].", MessagingNetworkException.CATEGORY_LOGGED_OFF_ON_BEHALF_OF_MESSAGING_SERVER_OR_PROTOCOL_ERROR);
    return new byte[size];
  }
}