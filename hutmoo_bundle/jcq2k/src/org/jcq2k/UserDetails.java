package org.jcq2k;

public interface UserDetails
{
  /** Can return null (if no nick specified) */
  String getNick();

  /** Can return null (if no realname specified) */
  String getRealName();

  /** Can return null (if no email specified) */
  public String getEmail();
}