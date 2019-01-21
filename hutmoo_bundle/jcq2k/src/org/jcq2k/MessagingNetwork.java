package org.jcq2k;

public interface MessagingNetwork
{
  static int STATUS_ONLINE = 1;
  static int STATUS_BUSY = 2;
  static int STATUS_OFFLINE = 3;

  void addMessagingNetworkListener(MessagingNetworkListener l);
  void addToContactList(String srcLoginId, String dstLoginId) throws MessagingNetworkException;
  int getClientStatus(String srcLoginId) throws MessagingNetworkException;
  public String getComment();
  public String getName();
  byte getNetworkId();
  int getStatus(String srcLoginId, String dstLoginId) throws MessagingNetworkException;
  void login(String srcLoginId, String password, String[] contactList, int status) throws MessagingNetworkException;
  void logout(String srcLoginId) throws MessagingNetworkException;
  void removeFromContactList(String srcLoginId, String dstLoginId) throws MessagingNetworkException;
  void removeMessagingNetworkListener(MessagingNetworkListener l) throws MessagingNetworkException;
  void sendMessage(String srcLoginId, String dstLoginId, String text) throws MessagingNetworkException;
  void sendContacts(String srcLoginId, String dstLoginId, String[] nicks, String[] loginIds) throws MessagingNetworkException;
  void setClientStatus(String srcLoginId, int status) throws MessagingNetworkException;
  /** Postcondition: returns non-null or throws MessagingNetworkException */
  UserDetails getUserDetails(String srcLoginId, String dstLoginId) throws MessagingNetworkException;
  void init();
  void deinit();
}