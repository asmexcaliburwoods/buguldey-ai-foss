package org.jcq2k.util.joe;

import java.util.*;
import java.io.*;

//
public class HexUtil
{
  public final static char[] HEX_DIGITS_CHARS = "0123456789abcdef".toCharArray();
  public final static byte[] HEX_DIGITS_BYTES = "0123456789abcdef".getBytes();
public static void dump_(byte[] data, String linePrefix)
{
  dump_(data, linePrefix, data.length);
}
public static void dump_(byte[] data, String linePrefix, int lenToPrint)
{
	synchronized (System.err)
	{
		dump_(System.err, data, linePrefix, lenToPrint);
		System.err.flush();
	}
}
public static void dump_(OutputStream os, byte[] data, String linePrefix)
{
  dump_(os, data, linePrefix, data.length);
}
public static void dump_(OutputStream os, byte[] data, String linePrefix, int lenToPrint)
{
	Lang.ASSERT_NOT_NULL(data, "data");
	int length = data.length;
	StringBuffer sb = new StringBuffer();
	try
	{
		sb.append("\r\n" + linePrefix);
		int printed = 0;
		int printedThisLine = 0;
		int lineStart = 0;
		int actuallyPrinted = 0;
		//int lenToPrint = length;
		if (lenToPrint > data.length)
			lenToPrint = data.length;
		//int len3 = lenToPrint;
		//if ((len3 & 15) > 0)
		//len3 = (len3 & 0xFFFFFFF0) + 16;
		while (printed < lenToPrint)
		{
			if (((printedThisLine & 3) == 0) && (printedThisLine > 0))

				//if ((printedThisLine & 15) == 8)
				//sb.append("- ");
				//else
				sb.append("  ");
			sb.append((printed >= lenToPrint ? "  " : pad_(Integer.toHexString(data[printed] & 0xff), 2)).toLowerCase() + " ");
			printed++;
			printedThisLine++;
			if (printed < lenToPrint)
				actuallyPrinted++;
			if ((printedThisLine >= 16) || (printed == lenToPrint))
			{
				sb.append("  ");
				dumpChars(sb, data, lineStart, actuallyPrinted);
				lineStart = printed;
				printedThisLine = 0;
				actuallyPrinted = 0;
				sb.append("\r\n");
				if (printed < lenToPrint)
					sb.append(linePrefix);
			}
		}
	}
	catch (Exception e)
	{
		sb.append("\r\n");
		Logger.printException(e);
	}
	try
	{
		os.write(sb.append("\r\n").toString().getBytes());
	}
	catch (IOException ex)
	{
		Logger.printException(ex);
	}
}
private static void dumpChars(StringBuffer sb, byte[] data, int lineStart, int maxLen)
{
  int printed = lineStart;
  int printedThisLine = 0;
  sb.append("\"");
  while (printed < data.length && printedThisLine <= maxLen)
  {
	if (((printedThisLine & 7) == 0) && (printedThisLine > 0))
	  sb.append(" ");
	if (data[printed] >= 32)
	  sb.append((char) data[printed]);
	else
	  sb.append(".");
	printed++;
	printedThisLine++;
  }
  sb.append("\"");
}
/**
   * Insert the method's description here.
   * Creation date: (03.12.99 11:26:13)
   * @return java.lang.String
   * @param str java.lang.String
   * @param resultingStringLength int
   */
private static String pad_(String str, int resultingStringLength)
{
  StringBuffer buf = new StringBuffer();
  while (buf.length() < resultingStringLength - str.length())
	buf.append("0");
  return buf.append(str).toString().toLowerCase();
}
public static String toHexString(int word)
{
  return pad_(Integer.toHexString(word & 0xffff), 4);
}
public static String toHexString(long n, long mask, int resultingStringLength)
{
  return pad_(Long.toHexString(n & mask), resultingStringLength);
}
public static String toHexString0x(int word)
{
  return "0x"+pad_(Integer.toHexString(word & 0xffff), 4);
}
public static String toHexString0x(long n, long mask, int resultingDigitStringLengthWithout0x)
{
  return "0x" + toHexString(n, mask, resultingDigitStringLengthWithout0x);
}
}