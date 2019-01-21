package org.jcq2k.util.joe;

public class Log4jUtil
{
	public static boolean OPTPARAM_STDERR_DEBUGLEVEL_INFO_WARNINGS_ERRORS_ONLY = true;
	static
	{
		AutoConfig.fetchFromClassLocalResourceProperties(Log4jUtil.class, false, false);
	}
/**
   Log a message with debug priority.
   @param      message        string to write in the log file
   @since version 0.8.0
   */
public static void dump(org.log4j.Category CAT, String message, byte[] byteArray, String dumpLinePrefix)
{
	String s = null;
	boolean debugEnabled = CAT.isDebugEnabled();
	if (debugEnabled || !OPTPARAM_STDERR_DEBUGLEVEL_INFO_WARNINGS_ERRORS_ONLY)
	{
		java.io.ByteArrayOutputStream bas = new java.io.ByteArrayOutputStream(5 * byteArray.length);
		HexUtil.dump_(bas, byteArray, dumpLinePrefix);
		try
		{
			bas.flush();
		}
		catch (java.io.IOException ex)
		{
			Logger.printException(ex);
		}
		s = message + "\r\n" + new String(bas.toByteArray());
		if (!OPTPARAM_STDERR_DEBUGLEVEL_INFO_WARNINGS_ERRORS_ONLY)
		{
			Logger.log(s);
		}
		if (debugEnabled)
		{
			CAT.debug(s);
		}
	}
}
}