package org.jcq2k.util.joe;

import java.util.*;

/**
 * Insert the type's description here.
 * Creation date: (10.11.00 15:07:27)
 * @author:
 */
public final class LocaleUtil
{

  /**
   * LocaleUtil constructor comment.
   */
  private LocaleUtil()
  {
  }
  public static String prepareKey(String key)
  {
		Lang.ASSERT_NOT_NULL(key, key);
		return key.replace(' ', '_').replace(':', '_').replace('\'', '_');
  }
}