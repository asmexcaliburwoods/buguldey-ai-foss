package org.jcq2k.util.joe;

public final class ClassNameUtil {
public final static String getPackageName(Class clazz)
{
  return getPackageName(clazz.getName());
}
public final static String getPackageName(String fullClassName)
{
  int p = fullClassName.lastIndexOf('.');
  if (p == -1)
  {
    return "";
  }
  else
  {
    return fullClassName.substring(0, p);
  }
}
public final static String getShortClassName(Object o)
{
  if (o instanceof Class)
  {
    return getShortClassName(((Class)o).getName());
  }
  else
  {
    return getShortClassName(o.getClass().getName());
  }
}
public final static String getShortClassName(String fullClassName)
{
  int p = fullClassName.lastIndexOf('.');
  if (p == -1)
  {
    return fullClassName;
  }
  else
  {
    return fullClassName.substring(p + 1);
  }
}
}