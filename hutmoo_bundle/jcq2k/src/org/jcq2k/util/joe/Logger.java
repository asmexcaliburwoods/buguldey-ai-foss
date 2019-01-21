package org.jcq2k.util.joe;

/**
 * Insert the type's description here.
 * Creation date: (01.12.00 14:21:47)
 * @author:
 */
public class Logger
{
  public final static java.text.DateFormat DATE_FORMAT = new java.text.SimpleDateFormat("dd MMM hh:mm:ss", java.util.Locale.US);
  public static boolean DEBUG = true;

  private Logger()
  {
  }
  public static String formatCurrentDate()
  {
		return formatDate(new java.util.Date());
  }
  public static String formatDate(java.util.Date date)
  {
		return DATE_FORMAT.format(date);
  }
  public static String formatStackTrace(Throwable tr)
  {
		java.io.StringWriter stackTrace = new java.io.StringWriter();
		java.io.PrintWriter pw = new java.io.PrintWriter(stackTrace);
		tr.printStackTrace(pw);
		pw.flush();
		pw.close();
		stackTrace.flush();
		try
		{
		stackTrace.close();
		}
		catch (java.io.IOException ex)
		{
		  ex.printStackTrace();
		}
		return stackTrace.toString().replace('/', '.');
  }
  public static void log(String s)
  {
		if (DEBUG)
		  System.err.println(org.jcq2k.util.joe.Logger.formatCurrentDate() + ": " + s);
  }
  public static void printException(Throwable tr)
  {
		printException(tr, System.err);
  }
  public static void printException(Throwable tr, java.io.PrintStream p)
  {
		synchronized (p)
		{
		p.print(formatCurrentDate() + ": Logger.printException(");
		if (tr == null)
		{
		  p.println("null)");
		  return;
		}
		p.println(tr.getClass().getName() + ")");
		if (tr instanceof java.util.MissingResourceException)
		{
		  java.util.MissingResourceException mre = (java.util.MissingResourceException) tr;
		  p.println("Missing resource\nResource key:\t" + org.jcq2k.util.joe.StringUtil.toPrintableString(mre.getKey()) + "\nClass: " + mre.getClassName());
		}

		//
		p.print(org.jcq2k.util.joe.Logger.formatStackTrace(tr));
		p.print(org.jcq2k.util.joe.Logger.formatStackTrace(new Exception("Where is this called from?")));

		//
		//if (tr instanceof java.sql.SQLException)
		//{
		////java.sql.SQLException se = (java.sql.SQLException) tr;

		////p.println(" Nested exception : "Missing resource\nResource key:\t" + org.jcq2k.util.joe.StringUtil.toPrintableString(mre.getKey()) + "\nClass: " + mre.getClassName());

		//}
		}
  }
}