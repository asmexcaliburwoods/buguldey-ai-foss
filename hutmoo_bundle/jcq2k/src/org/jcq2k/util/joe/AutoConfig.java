package org.jcq2k.util.joe;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

//
public class AutoConfig
{
  protected final static Hashtable primTypeNames = new Hashtable(10);
  static
  {
    primTypeNames.put("long", "java.lang.Long");
    primTypeNames.put("int", "java.lang.Integer");
    primTypeNames.put("boolean", "java.lang.Boolean");
    primTypeNames.put("char", "java.lang.Character");
    primTypeNames.put("byte", "java.lang.Byte");
    primTypeNames.put("float", "java.lang.Float");
    primTypeNames.put("double", "java.lang.Double");
  }
/**
Fetches all <code>public static</code> autoconfig fields of the given class
<code>class</code>.  To be fetched, these autoconfig fields must have field names starting
with <code>REQPARAM_</code> and/or <code>OPTPARAM_</code> prefix.
<p>
In this <code>fetch</code> method, the source for
autoconfig fields is the resource file named
<code>"/"+ className.replace('.', '/') + ".properties"</code>.
This file is parsed using standard Java Properties mechanism.  For any
<code>public static</code> class field,
which has a <code>REQPARAM_</code>/<code>OPTPARAM_</code> prefix, the
value of the property with the key matching the field name is taken.
This value is then parsed and assigned to the given static member field
of the class.
<p>
If the <code>public static</code> member field with the
<code>REQPARAM_</code> prefix doesn't have a matching property, or
if any autoconfig field property value cannot be parsed, then
the misconfig error occurs.  If the <code>systemExitOnMisconfig</code>
argument is <code>true</code>, the exception is reported, and <code>System.exit(1)</code>
is called.  If the <code>systemExitOnMisconfig</code> is <code>false</code>,
the runtime exception with error details is thrown.
<p>
Insert this method call in the static initializer of the class.
*/
public static void fetchFromClassLocalResourceProperties(Class clazz, boolean propertiesResourceIsRequired, boolean systemExitOnMisconfig) throws RuntimeException
{
  try
  {
    Properties prop_ = null;
    try
    {
      String resFilePathName = PropertyUtil.getResourceFilePathName(clazz);
      prop_ = PropertyUtil.loadResourceProperties(clazz, resFilePathName);
    }
    catch (Exception ex)
    {
      if (propertiesResourceIsRequired)
        throw ex;
      else
        return;
    }
    final Properties prop = prop_;
    StaticFieldParameterSet.assignValues(clazz, new StringMap()
    {
      public String getValue(String key)
      {
        return prop.getProperty(key);
      }
      public java.lang.reflect.Constructor getValueConstructor(Class targetClass) throws NoSuchMethodException, SecurityException
      {
        //if (Sound.class.isAssignableFrom(constructor.getDeclaringClass()))
        //return constructor.newInstance(new Object[] {Applet.this, value});
        //else
        String tn = (String) primTypeNames.get(targetClass.getName());
        if (tn != null)
        {
          try
          {
            return Class.forName(tn).getConstructor(new Class[] {String.class});
          }
          catch (ClassNotFoundException ex)
          {
            Logger.printException(ex);
          }
        }
        return targetClass.getConstructor(new Class[] {String.class});
      }
      public Object newValueInstance(java.lang.reflect.Constructor constructor, String value) throws java.lang.reflect.InvocationTargetException, InstantiationException, IllegalAccessException, IllegalArgumentException
      {
        //if (Sound.class.isAssignableFrom(constructor.getDeclaringClass()))
        //return constructor.newInstance(new Object[] {Applet.this, value});
        //else
        return constructor.newInstance(new Object[] {value});
      }
    });
  }
  catch (RuntimeException ex2)
  {
    if (systemExitOnMisconfig)
    {
      Logger.log("AutoConfig: exception while assignment autoconfig parameters for " + clazz);
      ex2.printStackTrace();
      System.exit(1);
    }
    else
    {
      throw ex2;
    }
  }
  catch (Exception ex)
  {
    if (systemExitOnMisconfig)
    {
      Logger.log("AutoConfig: exception while assignment autoconfig parameters for " + clazz);
      ex.printStackTrace();
      System.exit(1);
    }
    else
    {
      Logger.printException(ex);
      throw new RuntimeException("" + ex);
    }
  }
}

public static void fetchFromClassLocalResourceProperties_propertyResourceRequired(Class clazz) throws RuntimeException
{
  fetchFromClassLocalResourceProperties(clazz, true, false);
}
}