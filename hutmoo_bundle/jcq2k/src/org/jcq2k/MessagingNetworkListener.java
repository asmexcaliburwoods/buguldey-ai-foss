package org.jcq2k;

public interface MessagingNetworkListener
{
  void messageReceived(byte networkId, String srcLoginId, String dstLoginId, String text);

  void contactsReceived(byte networkId, String srcLoginId, String dstLoginId, String[] contactsLoginIds, String[] contactsNicks);

  void statusChanged(byte networkId, String srcLoginId, String dstLoginId,
     int status, int reasonCategory, String reasonMessage);
}