package org.jcq2k.util.joe;


public class NumberParseUtil
{

public static String extractAllDigitsPrefix(String s)
{
  char[] ca = s.toCharArray();
  int i;
  for (i = 0; i < ca.length; i++)
  {
    if (!Character.isDigit(ca[i])) break;
  }
  return s.substring(0, i);
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
public static int parseInt(String value, String valueName) throws ExpectException
{
  try
  {
    return Integer.parseInt(value);
  }
  catch (Exception _ex)
  {
    throw new ExpectException("the value named \"" + valueName + "\" must be a java integer number, but it is " + StringUtil.toPrintableString(value) + ".");
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
public static long parseLong(String value, String valueName) throws ExpectException
{
  try
  {
    return Long.parseLong(value);
  }
  catch (Exception _ex)
  {
    throw new ExpectException("the value named \"" + valueName + "\" must be a java long number, but it is " + StringUtil.toPrintableString(value) + ".");
  }
}
}