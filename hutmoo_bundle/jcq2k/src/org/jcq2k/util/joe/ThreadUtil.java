package org.jcq2k.util.joe;

import java.util.*;
public final class ThreadUtil
{
public static void sleep(long millis) throws RuntimeException
{
	try
	{
		Thread.currentThread().sleep(millis);
	}
	catch (InterruptedException ex)
	{
		Logger.printException(ex);
		throw new RuntimeException("interrupted");
	}
}
}