package org.jcq2k.util.joe;

// Decompiled by Jad v1.5.6g. Copyright 1997-99 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: fieldsfirst splitstr
// Source File Name:   StringUtil.java

public class StringUtil
{
  public static final String S_QUOTE = "\"";
  public static final String S_EMPTY = "";
  public static final String S_NULL = "null";

  public StringUtil()
  {
  }
  /**
  Concatenate vector entries using
  empty string separator.
  */
  public static String concat(java.util.Vector v)
  {
		return concat(v, S_EMPTY);
  }
  public static String concat(java.util.Vector v, String separator)
  {
		Lang.ASSERT_NOT_NULL(separator, "separator");
		Lang.ASSERT_NOT_NULL(v, "vector");
		synchronized (v)
		{
		java.util.Enumeration e = v.elements();
		StringBuffer sb = new StringBuffer();
		if (e.hasMoreElements())
		  sb.append(e.nextElement().toString());
		while (e.hasMoreElements())
		  sb.append(separator).append(e.nextElement().toString());
		return sb.toString();
		}
  }
  public static boolean isEmptyOrNull(String s)
  {
		return isNullOrEmpty(s);
  }
  public static boolean isNullOrEmpty(String s)
  {
		return s == null || s.length() == 0;
  }
  public static boolean isNullOrTrimmedEmpty(String s)
  {
		return s == null || s.trim().length() == 0;
  }
  public static boolean isTrimmedEmptyOrNull(String s)
  {
		return isNullOrTrimmedEmpty(s);
  }
  public static String mkEmpty(String s)
  {
		return s == null ? S_EMPTY : s;
  }
  public static String mkEmptyAndTrim(String s)
  {
		return s == null ? S_EMPTY : s.trim();
  }
  public static String mkNull(String s)
  {
		return s == null ? null : (s.length() == 0 ? null : s);
  }
  public static String mkNullAndTrim(String s)
  {
		if (s == null)
		return null;
		else
		{
		String s1 = s.trim();
		return s1.length() == 0 ? null : s1;
		}
  }
  /**
  Concatenate vector entries using
  empty string separator.
  */
  public static boolean startsWith(String s, String prefix)
  {
		Lang.ASSERT_NOT_NULL(s, "s");
		Lang.ASSERT_NOT_NULL(prefix, "prefix");
		return s.length() >= prefix.length() && s.startsWith(prefix);
  }
  public static String toPrintableString(Object object)
  {
		if (object == null)
		return "null";
		else
		return "\"" + object + "\"";
  }
}