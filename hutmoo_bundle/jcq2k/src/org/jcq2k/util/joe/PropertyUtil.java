package org.jcq2k.util.joe;

import java.io.*;
import java.util.*;
public final class PropertyUtil {
/**
 * PropertyUtil constructor comment.
 */
public static String getRequiredProperty(Properties p, String propfileDisplayName, String key) throws RuntimeException
{
  Lang.ASSERT_NOT_NULL(p, "properties");
  Lang.ASSERT_NOT_NULL_NOR_TRIMMED_EMPTY(key, "key");
  String value = p.getProperty(key);
  if (value == null)
    throw new RuntimeException("Property  \"" + key + "\" must be present in propfile named \"" + propfileDisplayName + "\", but it does not exist.");
  return value;
}
/**
 * PropertyUtil constructor comment.
 */
public static boolean getRequiredPropertyBoolean(Properties p, String propfileDisplayName, String key) throws RuntimeException
{
  String value = getRequiredProperty(p, propfileDisplayName, key).trim();
  if ("yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value))
    return true;
  if ("no".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))
    return false;
  throw new NumberFormatException("Property \"" + key + "\" must be either \"yes\", \"no\", \"false\", or \"true\", but is "+StringUtil.toPrintableString(value)+".  Propfile name: \"" + propfileDisplayName + "\".");
}
/**
 * PropertyUtil constructor comment.
 */
public static int getRequiredPropertyInt(Properties p, String propfileDisplayName, String key) throws RuntimeException
{
  String value = getRequiredProperty(p, propfileDisplayName, key);
  try
  {
    return Integer.parseInt(value);
  }
  catch (NumberFormatException ex)
  {
    throw new NumberFormatException("Property \"" + key + "\" must be integer number, but it is not.  Propfile name: \"" + propfileDisplayName + "\".");
  }
}
public static String getResourceFilePathName(Class clazz)
{
  return getResourceFilePathName(clazz, ClassNameUtil.getShortClassName(clazz) + ".properties");
}
public static String getResourceFilePathName(String fullClazzName)
{
  return getResourceFilePathName(fullClazzName, ClassNameUtil.getShortClassName(fullClazzName) + ".properties");
}
public static String getResourceFilePathName(Class clazz, String shortFileName)
{
  return "/" + ClassNameUtil.getPackageName(clazz).replace('.', '/') + "/" + shortFileName;
}
public static String getResourceFilePathName(String fullClazzName, String shortFileName)
{
  return "/" + ClassNameUtil.getPackageName(fullClazzName).replace('.', '/') + "/" + shortFileName;
}
public static Properties loadResourceProperties(String absoluteResourceFilePathName) throws RuntimeException
{
  return loadResourceProperties(PropertyUtil.class, absoluteResourceFilePathName);
}

public static Properties loadResourceProperties(Class clazz, String resourceFilePathName) throws RuntimeException
{
  String propFileDisplayName = resourceFilePathName + " resource";
  java.io.InputStream props_is = clazz.getResourceAsStream(resourceFilePathName);
  if (props_is == null)
    throw new RuntimeException(propFileDisplayName + "\r\nmust be present in classpath, but it does not exist.");
  Properties props = new Properties();
  try
  {
    props.load(props_is);
  }
  catch (java.io.IOException iex)
  {
    Logger.printException(iex);
    throw new RuntimeException("Error while loading properties from\r\n" + propFileDisplayName + ":\r\n" + iex);
  }
  finally
  {
    try
    {
      props_is.close();
    }
    catch (Exception ex)
    {
    }
  }
  return props;
}
}