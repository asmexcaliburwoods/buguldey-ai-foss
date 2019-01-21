package org.jcq2k.util;

// Decompiled by Jad v1.5.6g. Copyright 1997-99 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: fieldsfirst splitstr
// Source File Name:   AssertException.java

public class ExpectException extends org.jcq2k.MessagingNetworkException
{
  private ExpectException(int cat)
  {
    super(cat);
  }
  public ExpectException(String s, int cat)
  {
    super(s, cat);
  }
}