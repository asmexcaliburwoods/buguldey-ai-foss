package org.jcq2k.util;

import org.jcq2k.*;
import org.jcq2k.util.joe.StringUtil;

public class MLang
{

  public static void EXPECT(boolean conditionExpected, String errorMessage, int cat)
  throws ExpectException
  {
    if (!conditionExpected)
      if (errorMessage.startsWith("\r\n"))
        throw new ExpectException(errorMessage, cat);
      else
        throw new ExpectException("\r\n" + errorMessage, cat);
  }
  public static void EXPECT_EQUAL(long longValue1, long longValue2, String value1Name, String value2Name, int cat)
  throws ExpectException
  {
    EXPECT(longValue1 == longValue2, "The \"" + value1Name + "\" and \"" + value2Name + "\" must be equal, but they are not: "+value1Name+"=" + longValue1 + ", "+value2Name+"="+longValue2+".", cat);
  }

  public static void EXPECT_FALSE(String locationName, int cat)
  throws ExpectException
  {
    EXPECT(false, "This point should never be reached:\r\n" + StringUtil.toPrintableString(locationName), cat);
  }

  public static void EXPECT_IS_MN_STATUS(int statusValue, String valueName) throws ExpectException
  { EXPECT(statusValue == MessagingNetwork.STATUS_ONLINE || statusValue == MessagingNetwork.STATUS_OFFLINE || statusValue == MessagingNetwork.STATUS_BUSY, "invalid status value: " + statusValue + ", valueName: " + valueName, MessagingNetworkException.CATEGORY_NOT_CATEGORIZED);
  }
  public static void EXPECT_NON_NEGATIVE(long longValue, String valueName, int cat) throws ExpectException
  { EXPECT(longValue >= 0L, ("The value named") + " \"" + valueName + "\" " + ("should be zero or greater than zero, but it is") + " " + longValue + ".", cat);
  }
  public static void EXPECT_NOT_NULL(Object objectValue, String valueName, int cat) throws ExpectException
  { EXPECT(objectValue != null, "Non-null object " + StringUtil.toPrintableString(valueName) + " expected, but it is null.", cat);
  }
  public static void EXPECT_NOT_NULL_NOR_EMPTY(String stringValue, String valueName, int cat) throws ExpectException
  { EXPECT(!StringUtil.isNullOrEmpty(stringValue), "The string named " + StringUtil.toPrintableString(valueName) + " should be neither null nor empty, but it is " + StringUtil.toPrintableString(stringValue), cat);
  }
  public static void EXPECT_NOT_NULL_NOR_TRIMMED_EMPTY(String stringValue, String valueName, int cat) throws ExpectException
  { EXPECT(!StringUtil.isNullOrEmpty(stringValue), "The string named " + StringUtil.toPrintableString(valueName) + " should be neither null, empty, nor contain whitespace only, but it is " + StringUtil.toPrintableString(stringValue), cat);
  }
  public static void EXPECT_POSITIVE(long longValue, String valueName, int cat) throws ExpectException
  { EXPECT(longValue > 0L, "The \"" + valueName + "\" should be greater than zero, but it is " + longValue + ".", cat);
  }
  public static int parseInt(String value, String valueName, int cat) throws ExpectException
  { EXPECT_NOT_NULL_NOR_EMPTY(value, valueName, cat);
    int i = -1;
    try
    {
      i = Integer.parseInt(value);
    }
    catch (NumberFormatException ex)
    {
      throw new ExpectException(valueName+" must be an integer number, but it is '" + value + "'", cat);
    }
    return i;
  }
  public static int parseInt_NonNegative(String value, String valueName, int cat) throws ExpectException
  { int i = parseInt(value, valueName, cat);
    EXPECT_NON_NEGATIVE(i, valueName, cat);
    return i;
  }
  public static long parseLong(String value, String valueName, int cat) throws ExpectException
  { EXPECT_NOT_NULL_NOR_EMPTY(value, valueName, cat);
    long i = -1;
    try
    {
      i = Long.parseLong(value);
    }
    catch (NumberFormatException ex)
    {
      throw new ExpectException(valueName+" must be a long number, but it is '" + value + "'", cat);
    }
    return i;
  }
  public static long parseLong_NonNegative(String value, String valueName, int cat) throws ExpectException
  { long i = parseInt(value, valueName, cat);
    EXPECT_NON_NEGATIVE(i, valueName, cat);
    return i;
  }
}