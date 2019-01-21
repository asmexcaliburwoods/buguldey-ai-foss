package org.jcq2k.util.joe;

// Decompiled by Jad v1.5.6g. Copyright 1997-99 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: fieldsfirst splitstr
// Source File Name:   Util.java

public class Util
{
  private static java.util.ResourceBundle resourceBundle = java.util.ResourceBundle.getBundle("org.jcq2k.util.joe.locale");

  public Util()
  {
  }
  static String getResourceString(String key)
  {
		org.jcq2k.util.joe.Lang.ASSERT_NOT_NULL(key, "key");
		return resourceBundle.getString(org.jcq2k.util.joe.LocaleUtil.prepareKey(key));
  }
  public static int parseInt(String s, int i)
  {
		try
		{
		  return Integer.parseInt(s);
		}
		catch (Exception _ex)
		{
		  return i;
		}
  }
  public static long parseLong(String s, long l)
  {
		try
		{
		return Long.parseLong(s);
		}
		catch (Exception _ex)
		{
		return l;
		}
  }
}