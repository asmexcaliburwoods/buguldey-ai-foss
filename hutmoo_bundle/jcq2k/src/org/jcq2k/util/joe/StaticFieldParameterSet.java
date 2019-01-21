package org.jcq2k.util.joe;

import java.util.*;
import java.lang.reflect.*;
/**
 * Insert the type's description here.
 * Creation date: (06.06.00 16:22:26)
 * @author:
 */
public abstract class StaticFieldParameterSet
{
/**
 * Reads field values from Properties.
 */
private static void assignValue(Object assignTo, Field f, StringMap map, String value, boolean isRequired) throws PropertyException
{
	if (value == null)
	{
		if (isRequired)
		{
			throw new PropertyException("Fatal error: Property with key \"" + f.getName() + "\" not specified.");
		}
		else
			return;
	}
	convertAndAssign(assignTo, f, map, value);
}
/**
 * Reads field values from Properties.
 */
public static void assignValues(Class assignTo, StringMap map) throws PropertyException
{
	inspect(assignTo, assignTo, map);
}
/**
 * Reads field values from Properties.
 */
private static void convertAndAssign(Object assignTo, Field f, StringMap map, String value) throws PropertyException
{
	try
	{
		Class c = f.getType();
		Constructor con;
		try
		{
			con = map.getValueConstructor(c);
		}
		catch (Exception ex)
		{
			throw new AssertException("Error getting conversion constructor for field " + f.getName() + " of class " + c.getName() + ": " + ex);
		}
		Object convertedValue = map.newValueInstance(con, value);
		f.set(assignTo, convertedValue);
	}
	catch (InvocationTargetException ex0)
	{
		Throwable ex = ex0.getTargetException();
		ex.printStackTrace();
		throw new PropertyException("Error while property assignment: " + ex);
	}
	catch (Exception ex)
	{
		throw new PropertyException("Error while property assignment: " + ex);
	}
}
/**
 * Reads field values from Properties.
 */
private static void inspect(Class classToInspect, Object assignTo, StringMap map) throws PropertyException
{
	if (classToInspect == null)
		return;
	Field[] fields = classToInspect.getDeclaredFields();
	for (int i = 0; i < fields.length; i++)
	{
		Field f = fields[i];
		int mod = f.getModifiers();
		if ((!Modifier.isFinal(mod)) && Modifier.isPublic(mod) && Modifier.isStatic(mod))
		{
			String fieldName = f.getName();
			boolean isRequired = fieldName.startsWith("REQPARAM_");
			if (isRequired || fieldName.startsWith("OPTPARAM_"))
			{
				assignValue(assignTo, f, map, map.getValue(fieldName), isRequired);
			}
		}
	}
	//inspect(classToInspect.getSuperclass(), assignTo, map);
}
}