package org.jcq2k.icq2k;

import org.jcq2k.*;
import org.jcq2k.util.joe.*;

public class UserDetailsImpl implements UserDetails
{
  private final String nick;
  private final String realName;
  private final String email;

  public UserDetailsImpl(String nick, String realName, String email)
  {
    this.nick = nick;
    this.realName = realName;
    this.email = email;
  }

  /** Can return null (if no nick specified) */
  public String getNick()
  {
    return nick;
  }

  /** Can return null (if no realName specified) */
  public String getRealName()
  {
    return realName;
  }

  /** Can return null (if no email specified) */
  public String getEmail()
  {
    return email;
  }
}