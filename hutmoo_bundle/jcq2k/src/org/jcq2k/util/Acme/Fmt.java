package org.jcq2k.util.Acme;

// Decompiled by Jad v1.5.6g. Copyright 1997-99 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: fieldsfirst splitstr
// Source File Name:   Fmt.java

public class Fmt
{
  public static final int ZF = 1;
  public static final int LJ = 2;
  public static final int HX = 4;
  public static final int OC = 8;
  private static final int WN = 16;

  public Fmt()
  {
  }
  public static String doubleToString(double d)
  {
		if (Double.isNaN(d))
		return "NaN";
		if (d == (-1.0D / 0.0D))
		return "-Inf";
		if (d == (1.0D / 0.0D))
		return "Inf";
		boolean negative = false;
		if (d < 0.0D)
		{
		negative = true;
		d = -d;
		}
		String unsStr = Double.toString(d);
		int eInd = unsStr.indexOf(101);
		if (eInd == -1)
		eInd = unsStr.indexOf(69);
		String mantStr;
		String expStr;
		int exp;
		if (eInd == -1)
		{
		mantStr = unsStr;
		expStr = "";
		exp = 0;
		}
		else
		{
		mantStr = unsStr.substring(0, eInd);
		expStr = unsStr.substring(eInd + 1);
		if (expStr.startsWith("+"))
		  exp = Integer.parseInt(expStr.substring(1));
		else
		  exp = Integer.parseInt(expStr);
  }
  int dotInd = mantStr.indexOf(46);
  String numStr;
  if (dotInd == -1)
		numStr = mantStr;
  else
		numStr = mantStr.substring(0, dotInd);
  long num;
  if (numStr.length() == 0)
		num = 0L;
  else
		num = Integer.parseInt(numStr);
  StringBuffer newMantBuf = new StringBuffer(numStr + ".");
  double p = Math.pow(10D, exp);
  double frac = d - (double) num * p;
  String digits = "0123456789";
  int nDigits = 16 - numStr.length();
  for (int i = 0; i < nDigits; i++)
  {
		p /= 10D;
		int dig = (int) (frac / p);
		if (dig < 0)
		dig = 0;
		if (dig > 9)
		dig = 9;
		newMantBuf.append(digits.charAt(dig));
		frac -= (double) dig * p;
  }
  if ((int) (frac / p + 0.5D) == 1)
  {
		boolean roundMore = true;
		for (int i = newMantBuf.length() - 1; i >= 0; i--)
		{
		int dig = digits.indexOf(newMantBuf.charAt(i));
		if (dig == -1)
		  continue;
		if (++dig == 10)
		{
		  newMantBuf.setCharAt(i, '0');
		  continue;
		}
		newMantBuf.setCharAt(i, digits.charAt(dig));
		roundMore = false;
		break;
		}
		if (roundMore)
		newMantBuf.append("ROUNDMORE");
  }
  int len;
  for (len = newMantBuf.length(); newMantBuf.charAt(len - 1) == '0'; newMantBuf.setLength(--len));
  if (newMantBuf.charAt(len - 1) == '.')
		newMantBuf.setLength(--len);
  return (negative ? "-" : "") + newMantBuf + (expStr.length() == 0 ? "" : "e" + expStr);
  }
  public static String fmt(byte b)
  {
		return fmt(b, 0, 0);
  }
  public static String fmt(byte b, int minWidth)
  {
		return fmt(b, minWidth, 0);
  }
  public static String fmt(byte b, int minWidth, int flags)
  {
		boolean hexadecimal = (flags & 0x4) != 0;
		boolean octal = (flags & 0x8) != 0;
		if (hexadecimal)
		return fmt(Integer.toString(b & 0xff, 16), minWidth, flags | 0x10);
		if (octal)
		return fmt(Integer.toString(b & 0xff, 8), minWidth, flags | 0x10);
		else
		return fmt(Integer.toString(b & 0xff), minWidth, flags | 0x10);
  }
  public static String fmt(char c)
  {
		return fmt(c, 0, 0);
  }
  public static String fmt(char c, int minWidth)
  {
		return fmt(c, minWidth, 0);
  }
  public static String fmt(char c, int minWidth, int flags)
  {
		return fmt((new Character(c)).toString(), minWidth, flags);
  }
  public static String fmt(double d)
  {
		return fmt(d, 0, 0, 0);
  }
  public static String fmt(double d, int minWidth)
  {
		return fmt(d, minWidth, 0, 0);
  }
  public static String fmt(double d, int minWidth, int sigFigs)
  {
		return fmt(d, minWidth, sigFigs, 0);
  }
  public static String fmt(double d, int minWidth, int sigFigs, int flags)
  {
		if (sigFigs != 0)
		return fmt(sigFigFix(doubleToString(d), sigFigs), minWidth, flags | 0x10);
		else
		return fmt(doubleToString(d), minWidth, flags | 0x10);
  }
  public static String fmt(float f)
  {
		return fmt(f, 0, 0, 0);
  }
  public static String fmt(float f, int minWidth)
  {
		return fmt(f, minWidth, 0, 0);
  }
  public static String fmt(float f, int minWidth, int sigFigs)
  {
		return fmt(f, minWidth, sigFigs, 0);
  }
  public static String fmt(float f, int minWidth, int sigFigs, int flags)
  {
		if (sigFigs != 0)
		return fmt(sigFigFix(Float.toString(f), sigFigs), minWidth, flags | 0x10);
		else
		return fmt(Float.toString(f), minWidth, flags | 0x10);
  }
  public static String fmt(int i)
  {
		return fmt(i, 0, 0);
  }
  public static String fmt(int i, int minWidth)
  {
		return fmt(i, minWidth, 0);
  }
  public static String fmt(int i, int minWidth, int flags)
  {
		boolean hexadecimal = (flags & 0x4) != 0;
		boolean octal = (flags & 0x8) != 0;
		if (hexadecimal)
		return fmt(Long.toString((long) i & 0xffffffffL, 16), minWidth, flags | 0x10);
		if (octal)
		return fmt(Long.toString((long) i & 0xffffffffL, 8), minWidth, flags | 0x10);
		else
		return fmt(Integer.toString(i), minWidth, flags | 0x10);
  }
  public static String fmt(long l)
  {
		return fmt(l, 0, 0);
  }
  public static String fmt(long l, int minWidth)
  {
		return fmt(l, minWidth, 0);
  }
  public static String fmt(long l, int minWidth, int flags)
  {
		boolean hexadecimal = (flags & 0x4) != 0;
		boolean octal = (flags & 0x8) != 0;
		if (hexadecimal)
		if ((l & 0xf000000000000000L) != 0L)
		  return fmt(Long.toString(l >>> 60, 16) + fmt(l & 0xfffffffffffffffL, 15, 5), minWidth, flags | 0x10);
		else
		  return fmt(Long.toString(l, 16), minWidth, flags | 0x10);
		if (octal)
		{
		if ((l & 0x8000000000000000L) != 0L)
		  return fmt(Long.toString(l >>> 63, 8) + fmt(l & 0x7fffffffffffffffL, 21, 9), minWidth, flags | 0x10);
		else
		  return fmt(Long.toString(l, 8), minWidth, flags | 0x10);
		}
		else
		{
		return fmt(Long.toString(l), minWidth, flags | 0x10);
		}
  }
  public static String fmt(Object o)
  {
		return fmt(o, 0, 0);
  }
  public static String fmt(Object o, int minWidth)
  {
		return fmt(o, minWidth, 0);
  }
  public static String fmt(Object o, int minWidth, int flags)
  {
		return fmt(o.toString(), minWidth, flags);
  }
  public static String fmt(String s)
  {
		return fmt(s, 0, 0);
  }
  public static String fmt(String s, int minWidth)
  {
		return fmt(s, minWidth, 0);
  }
  public static String fmt(String s, int minWidth, int flags)
  {
		int len = s.length();
		boolean zeroFill = (flags & 0x1) != 0;
		boolean leftJustify = (flags & 0x2) != 0;
		boolean hexadecimal = (flags & 0x4) != 0;
		boolean octal = (flags & 0x8) != 0;
		boolean wasNumber = (flags & 0x10) != 0;
		if ((hexadecimal || octal || zeroFill) && !wasNumber)
		throw new InternalError("org.jcq2k.util.Acme.Fmt: number flag on a non-number");
		if (zeroFill && leftJustify)
		throw new InternalError("org.jcq2k.util.Acme.Fmt: zero-fill left-justify is silly");
		if (hexadecimal && octal)
		throw new InternalError("org.jcq2k.util.Acme.Fmt: can't do both hex and octal");
		if (len >= minWidth)
		return s;
		int fillWidth = minWidth - len;
		StringBuffer fill = new StringBuffer(fillWidth);
		for (int i = 0; i < fillWidth; i++)
		if (zeroFill)
		  fill.append('0');
		else
		  fill.append(' ');
		if (leftJustify)
		return s + fill;
		if (zeroFill && s.startsWith("-"))
		return "-" + fill + s.substring(1);
		else
		return fill + s;
  }
  public static String fmt(short s)
  {
		return fmt(s, 0, 0);
  }
  public static String fmt(short s, int minWidth)
  {
		return fmt(s, minWidth, 0);
  }
  public static String fmt(short s, int minWidth, int flags)
  {
		boolean hexadecimal = (flags & 0x4) != 0;
		boolean octal = (flags & 0x8) != 0;
		if (hexadecimal)
		return fmt(Integer.toString(s & 0xffff, 16), minWidth, flags | 0x10);
		if (octal)
		return fmt(Integer.toString(s & 0xffff, 8), minWidth, flags | 0x10);
		else
		return fmt(Integer.toString(s), minWidth, flags | 0x10);
  }
  private static String sigFigFix(String s, int sigFigs)
  {
		String sign;
		String unsigned;
		if (s.startsWith("-") || s.startsWith("+"))
		{
		sign = s.substring(0, 1);
		unsigned = s.substring(1);
		}
		else
		{
		sign = "";
		unsigned = s;
		}
		int eInd = unsigned.indexOf(101);
		if (eInd == -1)
		eInd = unsigned.indexOf(69);
		String mantissa;
		String exponent;
		if (eInd == -1)
		{
		mantissa = unsigned;
		exponent = "";
		}
		else
		{
		mantissa = unsigned.substring(0, eInd);
		exponent = unsigned.substring(eInd);
		}
		int dotInd = mantissa.indexOf(46);
		StringBuffer number;
		StringBuffer fraction;
		if (dotInd == -1)
		{
		number = new StringBuffer(mantissa);
		fraction = new StringBuffer("");
		}
		else
		{
		number = new StringBuffer(mantissa.substring(0, dotInd));
		fraction = new StringBuffer(mantissa.substring(dotInd + 1));
		}
		int numFigs = number.length();
		int fracFigs = fraction.length();
		if ((numFigs == 0 || number.equals("0")) && fracFigs > 0)
		{
		numFigs = 0;
		for (int i = 0; i < fraction.length(); i++)
		{
		  if (fraction.charAt(i) != '0')
		  break;
		  fracFigs--;
		}
		}
		int mantFigs = numFigs + fracFigs;
		if (sigFigs > mantFigs)
		{
		for (int i = mantFigs; i < sigFigs; i++)
		  fraction.append('0');
		}
		else
		if (sigFigs < mantFigs && sigFigs >= numFigs)
		  fraction.setLength(fraction.length() - (fracFigs - (sigFigs - numFigs)));
		else
		  if (sigFigs < numFigs)
		  {
		  fraction.setLength(0);
		  for (int i = sigFigs; i < numFigs; i++)
				number.setCharAt(i, '0');
		  }
		if (fraction.length() == 0)
		return sign + number + exponent;
		else
		return sign + number + "." + fraction + exponent;
  }
}