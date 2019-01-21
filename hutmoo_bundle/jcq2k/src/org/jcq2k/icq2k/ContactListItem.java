package org.jcq2k.icq2k;

import java.io.*;
import java.net.*;
import java.util.*;
import org.jcq2k.*;
import org.jcq2k.util.joe.*;
import org.log4j.Category;

public class ContactListItem
{
  private final static org.log4j.Category CAT = org.log4j.Category.getInstance(ContactListItem.class.getName());
  private final Session session;
  private final String dstLoginId;
  private boolean statusObsolete = false;

  public ContactListItem(Session session, String dstLoginId)
  {
    Lang.ASSERT_NOT_NULL(session, "session");
    this.session = session;
    Lang.ASSERT_NOT_NULL(dstLoginId, "dstLoginId");
    this.dstLoginId = dstLoginId;
  }

  public String getDstLoginId()
  {
    return dstLoginId;
  }


  private int statusOscar = StatusUtil.OSCAR_STATUS_OFFLINE;

  public int getStatusOscar()
  { return statusOscar; }

  public void setStatusOscar(int status)
  {
    CAT.debug("setStatusOscar to "+StatusUtil.translateStatusOscarToString(status));
    statusOscar = status;
    setStatusObsolete(false);
  }

  public void setStatusObsolete(boolean b)
  {
    statusObsolete = b;
  }

  public boolean isStatusObsolete()
  {
    return statusObsolete;
  }


  private long contactStatusLastChangeTimeMillis = 0; //in the year 1970

  /**
    The time of the last time when MessagingNetwork impl
    has fired the listeners.statusChanged(...) event.

    @see org.jcq2k.MessagingNetworkListener
  */
  public long getContactStatusLastChangeTimeMillis()
  { return contactStatusLastChangeTimeMillis; }

  /**
    @see #getContactStatusLastChangeTimeMillis()
  */
  public void setContactStatusLastChangeTimeMillis(long time)
  { contactStatusLastChangeTimeMillis = time; }


  private long scheduledStatusChangeSendTimeMillis = Long.MAX_VALUE; //currently no plans to send anything

  public void setScheduledStatusChangeSendTimeMillis(long time)
  {
    scheduledStatusChangeSendTimeMillis = time;
    session.setScheduledSendStatus(session.isScheduledSendStatus() || (time < Long.MAX_VALUE));
  }

  public long getScheduledStatusChangeSendTimeMillis()
  { return scheduledStatusChangeSendTimeMillis; }
}