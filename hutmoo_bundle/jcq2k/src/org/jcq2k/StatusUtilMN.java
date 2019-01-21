package org.jcq2k;

import org.jcq2k.util.*;
import org.jcq2k.util.joe.*;

/**
  Utility class for conversion of MN integer status values to String.
*/                                 
public final class StatusUtilMN
{
  private StatusUtilMN()
  {
  }
  public static String translateStatusMNToString(int status)
  {
    String translatedStatus;
    switch (status)
    {
      case MessagingNetwork.STATUS_ONLINE :
        translatedStatus = "MN_STATUS_ONLINE";
        break;
      case MessagingNetwork.STATUS_BUSY :
        translatedStatus = "MN_STATUS_BUSY";
        break;
      case MessagingNetwork.STATUS_OFFLINE :
        translatedStatus = "MN_STATUS_OFFLINE";
        break;
      default :
        translatedStatus = "MN_STATUS_INVALID(value=="+HexUtil.toHexString0x(status)+")";
        break;
    }
    return translatedStatus;
  }
}
