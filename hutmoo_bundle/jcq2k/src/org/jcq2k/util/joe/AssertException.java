package org.jcq2k.util.joe;

// Decompiled by Jad v1.5.6g. Copyright 1997-99 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: fieldsfirst splitstr
// Source File Name:   AssertException.java

public class AssertException extends RuntimeException
{
  private static String MUG_REPORT_MSG = "Please send a bug report to the software development team.";

  private AssertException()
  {
	  this(null);
  }
public AssertException(String s)
{
	super((s == null ? "" : "\r\n" + s) + "\r\n" + MUG_REPORT_MSG);
}
}