package org.jcq2k.util.joe;

public class LogUtil {
public static String toString_Hex0xAndDec(int word)
{
	return HexUtil.toHexString0x(word) + " (" + (word & 0xFFFF) + ")";
}
}