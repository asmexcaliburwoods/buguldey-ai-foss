package org.jcq2k.util.Acme;

import java.io.*;
import java.net.*;
import java.util.*;

// Referenced classes of package org.jcq2k.util.Acme:
//      Fmt
public class Utils
{
  public static final long INT_SECOND = 1000L;
  public static final long INT_MINUTE = 60000L;
  public static final long INT_HOUR = 0x36ee80L;
  public static final long INT_DAY = 0x5265c00L;
  public static final long INT_WEEK = 0x240c8400L;
  public static final long INT_MONTH = 0x9a7ec800L;
  public static final long INT_YEAR = 0x757b12c00L;
  public static final long INT_DECADE = 0x4977387000L;
  private static char b64EncodeTable[] = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
  private static int b64DecodeTable[] = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

final private static int READFULLY_CHUNK_SIZE = 2048;

  public Utils()
  {
  }
  public static String absoluteUrlStr(String urlStr, URL contextUrl) throws MalformedURLException
  {
  URL url = new URL(contextUrl, urlStr);
  return url.toExternalForm();
  }
  public static boolean arraycontains(Object array[], Object element)
  {
  for (int i = 0; i < array.length; i++)
  if (array[i].equals(element))
    return true;
  return false;
  }
  public static String arrayToString(Object o)
  {
  return arrayToString(o, 0);
  }
  public static String arrayToString(Object o, int startPos)
  {
  return arrayToString(o, startPos, -1);
  }
  public static String arrayToString(final Object o, final int startPos, final int endPos)
  {
  if (o == null)
  return "null";
  String cl = o.getClass().getName();
  if (!cl.startsWith("["))
  return o.toString();
  StringBuffer sb = new StringBuffer("{");
  if (startPos > 0)
  sb.append("...");
  int length = -1;
  if (o instanceof byte[])
  {
  byte ba[] = (byte[]) o;
  for (int i = startPos; i < (endPos >= 0 ? endPos : (length = ba.length)); i++)
  {
    if (i > 0)
    sb.append(", ");
    sb.append("(byte)");
    sb.append(ba[i]);
  }
  }
  else
  if (o instanceof char[])
  {
    char ca[] = (char[]) o;
    for (int i = startPos; i < (endPos >= 0 ? endPos : (length = ca.length)); i++)
    {
    if (i > 0)
  sb.append(", ");
    sb.append("'");
    sb.append(ca[i]);
    sb.append("'");
    }
  }
  else
    if (o instanceof short[])
    {
    short sa[] = (short[]) o;
    for (int i = startPos; i < (endPos >= 0 ? endPos : (length = sa.length)); i++)
    {
  if (i > 0)
  sb.append(", ");
  sb.append("(short)");
  sb.append(sa[i]);
    }
    }
    else
    if (o instanceof int[])
    {
  int ia[] = (int[]) o;
  for (int i = startPos; i < (endPos >= 0 ? endPos : (length = ia.length)); i++)
  {
  if (i > 0)
  sb.append(", ");
  sb.append(ia[i]);
  }
    }
    else
  if (o instanceof long[])
  {
  long la[] = (long[]) o;
  for (int i = startPos; i < (endPos >= 0 ? endPos : (length = la.length)); i++)
  {
  if (i > 0)
  sb.append(", ");
  sb.append(la[i]);
  sb.append("L");
  }
  }
  else
  if (o instanceof float[])
  {
  float fa[] = (float[]) o;
  for (int i = startPos; i < (endPos >= 0 ? endPos : (length = fa.length)); i++)
  {
  if (i > 0)
    sb.append(", ");
  sb.append(fa[i]);
  sb.append("F");
  }
  }
  else
  if (o instanceof double[])
  {
  double da[] = (double[]) o;
  for (int i = startPos; i < (endPos >= 0 ? endPos : (length = da.length)); i++)
  {
    if (i > 0)
    sb.append(", ");
    sb.append(da[i]);
    sb.append("D");
  }
  }
  else
  if (o instanceof boolean[])
  {
    boolean da[] = (boolean[]) o;
    for (int i = startPos; i < (endPos >= 0 ? endPos : (length = da.length)); i++)
    {
    if (i > 0)
    sb.append(", ");
    sb.append(da[i]);
    }
  }
  else
    if (o instanceof String[])
    {
    String sa[] = (String[]) o;
    for (int i = startPos; i < (endPos >= 0 ? endPos : (length = sa.length)); i++)
    {
    if (i > 0)
    sb.append(", ");
    sb.append("\"");
    sb.append(sa[i]);
    sb.append("\"");
    }
    }
    else
    if (cl.startsWith("[L"))
    {
    Object oa[] = (Object[]) o;
    for (int i = startPos; i < (endPos >= 0 ? endPos : (length = oa.length)); i++)
    {
    if (i > 0)
  sb.append(", ");
    sb.append(oa[i]);
    }
    }
    else
    if (cl.startsWith("[["))
    {
    Object aa[] = (Object[]) o;
    for (int i = startPos; i < (endPos >= 0 ? endPos : (length = aa.length)); i++)
    {
  if (i > 0)
  sb.append(", ");
  sb.append(arrayToString(aa[i]));
    }
    }
    else
    {
    sb.append("(array of unknown type \"" + cl + "\")");
    length = endPos + 1;
    }
  org.jcq2k.util.joe.Lang.ASSERT_NON_NEGATIVE(length, "length");
  if (endPos != -1 && endPos < length)
  sb.append(", ...");
  sb.append("}");
  return sb.toString();
  }
  public static String base64Encode(byte src[])
  {
  StringBuffer encoded = new StringBuffer();
  int phase = 0;
  char c = '\0';
  for (int i = 0; i < src.length; i++)
  switch (phase)
  {
  case 0 :
    /* '\0' */
    c = b64EncodeTable[src[i] >> 2 & 0x3f];
    encoded.append(c);
    c = b64EncodeTable[ (src[i] & 0x3) << 4];
    encoded.append(c);
    phase++;
    break;
  case 1 :
    /* '\001' */
    c = b64EncodeTable[ (b64DecodeTable[c] | src[i] >> 4) & 0x3f];
    encoded.setCharAt(encoded.length() - 1, c);
    c = b64EncodeTable[ (src[i] & 0xf) << 2];
    encoded.append(c);
    phase++;
    break;
  case 2 :
    /* '\002' */
    c = b64EncodeTable[ (b64DecodeTable[c] | src[i] >> 6) & 0x3f];
    encoded.setCharAt(encoded.length() - 1, c);
    c = b64EncodeTable[src[i] & 0x3f];
    encoded.append(c);
    phase = 0;
    break;
  }
  while (phase++ < 3)
  encoded.append('=');
  return encoded.toString();
  }
  public static String base64Encode(String srcString)
  {
  byte src[] = srcString.getBytes();
  return base64Encode(src);
  }
  public static String baseUrlStr(String urlStr)
  {
  if (urlStr.endsWith("/"))
  return urlStr;
  if (urlStrIsDir(urlStr))
  return urlStr + "/";
  else
  return urlStr.substring(0, urlStr.lastIndexOf(47) + 1);
  }
  public static int charCount(String str, char c)
  {
  int n = 0;
  for (int i = 0; i < str.length(); i++)
  if (str.charAt(i) == c)
    n++;
  return n;
  }
  public static void copyStream(InputStream in, OutputStream out) throws IOException
  {
  byte buf[] = new byte[4096];
  int i;
  while ((i = in.read(buf)) != -1)
  out.write(buf, 0, i);
  }
  public static void copyStream(InputStream in, Writer out) throws IOException
  {
  byte buf1[] = new byte[4096];
  char buf2[] = new char[4096];
  int j;
  while ((j = in.read(buf1)) != -1)
  {
  for (int i = 0; i < j; i++)
    buf2[i] = (char) buf1[i];
  out.write(buf2, 0, j);
  }
  }
  public static void copyStream(Reader in, OutputStream out) throws IOException
  {
  char buf1[] = new char[4096];
  byte buf2[] = new byte[4096];
  int j;
  while ((j = in.read(buf1)) != -1)
  {
  for (int i = 0; i < j; i++)
    buf2[i] = (byte) buf1[i];
  out.write(buf2, 0, j);
  }
  }
  public static void copyStream(Reader in, Writer out) throws IOException
  {
  char buf[] = new char[4096];
  int i;
  while ((i = in.read(buf)) != -1)
  out.write(buf, 0, i);
  }
  public static int countOnes(byte n)
  {
  return countOnes((long) n & 255L);
  }
  public static int countOnes(int n)
  {
  return countOnes((long) n & 0xffffffffL);
  }
  public static int countOnes(long n)
  {
  int count = 0;
  for (; n != 0L; n >>>= 1)
  if (odd(n))
    count++;
  return count;
  }
  public static void dumpStack()
  {
  org.jcq2k.util.joe.Logger.printException(new Throwable());
  }
  public static void dumpStack(PrintStream p)
  {
  org.jcq2k.util.joe.Logger.printException(new Throwable(), p);
  }
  public static boolean equalsStrings(String strings1[], String strings2[])
  {
  if (strings1.length != strings2.length)
  return false;
  for (int i = 0; i < strings1.length; i++)
  if (!strings1[i].equals(strings2[i]))
    return false;
  return true;
  }
  public static boolean even(long n)
  {
  return (n & 1L) == 0L;
  }
  public static String fixDirUrlStr(String urlStr)
  {
  if (urlStr.endsWith("/"))
  return urlStr;
  if (urlStrIsDir(urlStr))
  return urlStr + "/";
  else
  return urlStr;
  }
  public static String flattenStrarr(String strs[])
  {
  StringBuffer sb = new StringBuffer();
  for (int i = 0; i < strs.length; i++)
  {
  if (i > 0)
    sb.append(' ');
  sb.append(strs[i]);
  }
  return sb.toString();
  }
  public static int indexOfString(String strings[], String string)
  {
  for (int i = 0; i < strings.length; i++)
  if (string.equals(strings[i]))
    return i;
  return -1;
  }
  public static int indexOfStringIgnoreCase(String strings[], String string)
  {
  for (int i = 0; i < strings.length; i++)
  if (string.equalsIgnoreCase(strings[i]))
    return i;
  return -1;
  }
  public static boolean instanceOf(Object o, Class cl)
  {
  if (o == null || cl == null)
  return false;
  Class ocl = o.getClass();
  if (ocl.equals(cl))
  return true;
  if (!cl.isInterface())
  {
  Class ifs[] = cl.getInterfaces();
  for (int i = 0; i < ifs.length; i++)
    if (instanceOf(o, ifs[i]))
    return true;
  }
  Class scl = cl.getSuperclass();
  return scl != null && instanceOf(o, scl);
  }
  public static String intervalStr(long interval)
  {
  long decades = interval / 0x4977387000L;
  interval -= decades * 0x4977387000L;
  long years = interval / 0x757b12c00L;
  interval -= years * 0x757b12c00L;
  long months = interval / 0x9a7ec800L;
  interval -= months * 0x9a7ec800L;
  long weeks = interval / 0x240c8400L;
  interval -= weeks * 0x240c8400L;
  long days = interval / 0x5265c00L;
  interval -= days * 0x5265c00L;
  long hours = interval / 0x36ee80L;
  interval -= hours * 0x36ee80L;
  long minutes = interval / 60000L;
  interval -= minutes * 60000L;
  long seconds = interval / 1000L;
  interval -= seconds * 1000L;
  long millis = interval;
  if (decades > 0L)
  if (years == 0L)
    return decades + " decade" + pluralStr(decades);
  else
    return decades + " decade" + pluralStr(decades) + ", " + years + " years" + pluralStr(years);
  if (years > 0L)
  if (months == 0L)
    return years + " year" + pluralStr(years);
  else
    return years + " year" + pluralStr(years) + ", " + months + " month" + pluralStr(months);
  if (months > 0L)
  if (weeks == 0L)
    return months + " month" + pluralStr(months);
  else
    return months + " month" + pluralStr(months) + ", " + weeks + " week" + pluralStr(weeks);
  if (weeks > 0L)
  if (days == 0L)
    return weeks + " week" + pluralStr(weeks);
  else
    return weeks + " week" + pluralStr(weeks) + ", " + days + " day" + pluralStr(days);
  if (days > 0L)
  if (hours == 0L)
    return days + " day" + pluralStr(days);
  else
    return days + " day" + pluralStr(days) + ", " + hours + " hour" + pluralStr(hours);
  if (hours > 0L)
  if (minutes == 0L)
    return hours + " hour" + pluralStr(hours);
  else
    return hours + " hour" + pluralStr(hours) + ", " + minutes + " minute" + pluralStr(minutes);
  if (minutes > 0L)
  if (seconds == 0L)
    return minutes + " minute" + pluralStr(minutes);
  else
    return minutes + " minute" + pluralStr(minutes) + ", " + seconds + " second" + pluralStr(seconds);
  if (seconds > 0L)
  {
  if (millis == 0L)
    return seconds + " second" + pluralStr(seconds);
  else
    return seconds + " second" + pluralStr(seconds) + ", " + millis + " millisecond" + pluralStr(millis);
  }
  else
  {
  return millis + " millisecond" + pluralStr(millis);
  }
  }
  public static String lsDateStr(Date date)
  {
  Calendar cal = new GregorianCalendar();
  cal.setTime(date);
  long dateTime = date.getTime();
  if (dateTime == -1L)
  return "------------";
  long nowTime = (new Date()).getTime();
  String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
  String part1 = months[cal.get(2)] + Fmt.fmt(cal.get(5), 3);
  if (Math.abs(nowTime - dateTime) < 0x3ae6bc400L)
  return part1 + Fmt.fmt(cal.get(11), 3) + ":" + Fmt.fmt(cal.get(12), 2, 1);
  else
  return part1 + Fmt.fmt(cal.get(1), 6);
  }
  public static boolean match(String pattern, String string)
  {
  int p = 0;
  do
  {
  int s = 0;
  do
  {
    boolean sEnd = s >= string.length();
    boolean pEnd = p >= pattern.length() || pattern.charAt(p) == '|';
    if (sEnd && pEnd)
    return true;
    if (sEnd || pEnd)
    break;
    if (pattern.charAt(p) != '?')
    {
    if (pattern.charAt(p) == '*')
    {
  p++;
  for (int i = string.length(); i >= s; i--)
  if (match(pattern.substring(p), string.substring(i)))
  return true;
  break;
    }
    if (pattern.charAt(p) != string.charAt(s))
  break;
    }
    p++;
    s++;
  }
  while (true);
  p = pattern.indexOf(124, p);
  if (p == -1)
    return false;
  p++;
  }
  while (true);
  }
  public static boolean odd(long n)
  {
  return (n & 1L) != 0L;
  }
  public static int parseInt(String str, int def)
  {
  try
  {
  return Integer.parseInt(str);
  }
  catch (Exception _ex)
  {
  return def;
  }
  }
  public static long parseLong(String str, long def)
  {
  try
  {
  return Long.parseLong(str);
  }
  catch (Exception _ex)
  {
  return def;
  }
  }
  public static URL plainUrl(String urlStr) throws MalformedURLException
  {
  return plainUrl(null, urlStr);
  }
  public static URL plainUrl(URL context, String urlStr) throws MalformedURLException
  {
  URL url = new URL(context, urlStr);
  String fileStr = url.getFile();
  int i = fileStr.indexOf(63);
  if (i != -1)
  fileStr = fileStr.substring(0, i);
  url = new URL(url.getProtocol(), url.getHost(), url.getPort(), fileStr);
  if (!fileStr.endsWith("/") && urlStrIsDir(url.toExternalForm()))
  {
  fileStr = fileStr + "/";
  url = new URL(url.getProtocol(), url.getHost(), url.getPort(), fileStr);
  }
  return url;
  }
  public static String pluralStr(long n)
  {
  if (n == 1L)
  return "";
  else
  return "s";
  }
  public static InputStream popenr(String cmd)
  {
  try
  {
  return runCommand(cmd).getInputStream();
  }
  catch (IOException _ex)
  {
  return null;
  }
  }
  public static OutputStream popenw(String cmd)
  {
  try
  {
  return runCommand(cmd).getOutputStream();
  }
  catch (IOException _ex)
  {
  return null;
  }
  }
  public static long pow(long a, long b) throws ArithmeticException
  {
  if (b < 0L)
  throw new ArithmeticException();
  long r = 1L;
  while (b != 0L)
  {
  if (odd(b))
    r *= a;
  b >>>= 1;
  a *= a;
  }
  return r;
  }
  public static int read(InputStream in, byte b[], int off, int len) throws IOException
  {
  if (len <= 0)
  return 0;
  int c = in.read();
  if (c == -1)
  return -1;
  if (b != null)
  b[off] = (byte) c;
  int i;
  for (i = 1; i < len; i++)
  {
  c = in.read();
  if (c == -1)
    break;
  if (b != null)
    b[off + i] = (byte) c;
  }
  return i;
  }
public static void readFully(InputStream in, byte b[]) throws IOException
{
  readFully(in, b, 0, b.length);
}
public static void readFully(InputStream in, byte b[], int offset, int len) throws IOException
{
  readFullyWithTimeout(in, b, offset, len, 0);
}
public static void readFullyWithTimeout(InputStream in, byte b[], int offset, int len, long timeoutMillis) throws IOException
{
  if (len == 0)
    return;
  long failTime = -1;
  if (timeoutMillis < 0)
    throw new RuntimeException("Illegal argument: timeoutMillis < 0");
  boolean failAfterTimeout = timeoutMillis != 0;
  if (failAfterTimeout)
    failTime = System.currentTimeMillis() + timeoutMillis;
  if (len <= READFULLY_CHUNK_SIZE)
  {
    while (in.available() < len)
    {
      if (failAfterTimeout && System.currentTimeMillis() >= failTime)
        throw new IOException("connection timed out");
      sleep_io();
    }
    int r;
    for (int l = offset; l < len; l += r)
    {
      if (failAfterTimeout && System.currentTimeMillis() >= failTime)
        throw new IOException("connection timed out");
      if (Thread.currentThread().isInterrupted())
        throw new InterruptedIOException();
      r = in.read(b, l, len - l);
      if (r == -1)
        r = 0; //throw new IOException("Unexpected end of stream encountered.");
      if (r == 0)
      {
        sleep_io();
      }
    }
  }
  else
  {
    // len > READFULLY_CHUNK_SIZE
    int bytesRead = 0;
    while (len > bytesRead)
    {
      long timeout2 = 0;
      if (failAfterTimeout)
      {
        timeout2 = failTime - System.currentTimeMillis();
        if (timeout2 <= 0)
          throw new IOException("connection timed out");
      }
      int curChunkLen = Math.min(READFULLY_CHUNK_SIZE, len - bytesRead);
      readFullyWithTimeout(in, b, offset + bytesRead, curChunkLen, timeout2);
      bytesRead += curChunkLen;
    }
  }
}
public static void readFullyWithTimeout(InputStream in, byte b[], long timeoutMillis) throws IOException
{
  readFullyWithTimeout(in, b, 0, b.length, timeoutMillis);
}
  public static Process runCommand(String cmd) throws IOException
  {
  Runtime runtime = Runtime.getRuntime();
  String shCmd[] = new String[3];
  shCmd[0] = "/bin/sh";
  shCmd[1] = "-c";
  shCmd[2] = cmd;
  return runtime.exec(shCmd);
  }
  public static int sameSpan(String str1, String str2)
  {
  int i;
  for (i = 0; i < str1.length() && i < str2.length() && str1.charAt(i) == str2.charAt(i); i++);
  return i;
  }
private static void sleep_io() throws InterruptedIOException
{
  try
  {
    //if (new Date().getSeconds() < 1)
      //throw new InterruptedIOException("test exception");
    Thread.currentThread().sleep(20);
  }
  catch (InterruptedException ex)
  {
    throw new InterruptedIOException();
  }
}
  public static void sortStrings(String strings[])
  {
  for (int i = 0; i < strings.length - 1; i++)
  {
  for (int j = i + 1; j < strings.length; j++)
    if (strings[i].compareTo(strings[j]) > 0)
    {
    String t = strings[i];
    strings[i] = strings[j];
    strings[j] = t;
    }
  }
  }
  public static String[] splitStr(String str)
  {
  StringTokenizer st = new StringTokenizer(str);
  int n = st.countTokens();
  String strs[] = new String[n];
  for (int i = 0; i < n; i++)
  strs[i] = st.nextToken();
  return strs;
  }
  public static String[] splitStr(String str, char delim)
  {
  int n = 1;
  int index = -1;
  do
  {
  index = str.indexOf(delim, index + 1);
  if (index == -1)
    break;
  n++;
  }
  while (true);
  String strs[] = new String[n];
  index = -1;
  for (int i = 0; i < n - 1; i++)
  {
  int nextIndex = str.indexOf(delim, index + 1);
  strs[i] = str.substring(index + 1, nextIndex);
  index = nextIndex;
  }
  strs[n - 1] = str.substring(index + 1);
  return strs;
  }
  public static int strCSpan(String str, String charSet)
  {
  return strCSpan(str, charSet, 0);
  }
  public static int strCSpan(String str, String charSet, int fromIdx)
  {
  int i;
  for (i = fromIdx; i < str.length(); i++)
  if (charSet.indexOf(str.charAt(i)) != -1)
    break;
  return i - fromIdx;
  }
  public static int strSpan(String str, String charSet)
  {
  return strSpan(str, charSet, 0);
  }
  public static int strSpan(String str, String charSet, int fromIdx)
  {
  int i;
  for (i = fromIdx; i < str.length(); i++)
  if (charSet.indexOf(str.charAt(i)) == -1)
    break;
  return i - fromIdx;
  }
  public static int system(String cmd)
  {
  try
  {
  return runCommand(cmd).waitFor();
  }
  catch (IOException _ex)
  {
  return -1;
  }
  catch (InterruptedException _ex)
  {
  return -1;
  }
  }
  public static String urlDecoder(String encoded)
  {
  StringBuffer decoded = new StringBuffer();
  int len = encoded.length();
  int i = 0;
  while (i < len)
  {
  if (encoded.charAt(i) == '%' && i + 2 < len)
  {
    int d1 = Character.digit(encoded.charAt(i + 1), 16);
    int d2 = Character.digit(encoded.charAt(i + 2), 16);
    if (d1 != -1 && d2 != -1)
    decoded.append((char) ((d1 << 4) + d2));
    i += 2;
  }
  else
    if (encoded.charAt(i) == '+')
    decoded.append(' ');
    else
    decoded.append(encoded.charAt(i));
  i++;
  }
  return decoded.toString();
  }
  public static boolean urlStrIsAbsolute(String urlStr)
  {
  return urlStr.startsWith("/") || urlStr.indexOf(":/") != -1;
  }
  public static boolean urlStrIsDir(String urlStr)
  {
  if (urlStr.endsWith("/"))
  return true;
  String urlStrWithSlash = urlStr + "/";
  try
  {
  URL url = new URL(urlStrWithSlash);
  InputStream f = url.openStream();
  f.close();
  return true;
  }
  catch (Exception _ex)
  {
  return false;
  }
  }
}