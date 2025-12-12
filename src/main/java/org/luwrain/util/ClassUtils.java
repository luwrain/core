// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import org.luwrain.core.*;

public final class ClassUtils
{
    static private final String LOG_COMPONENT = "core";

    /**
     * Creates new instance of the requested class and ensures that
     * it can be safely assigned to some class or interface.
     *
     *
     * @param classLoader The class loader to use
 @param className The name of the class to create instance of
 * @param ensureInstanceOf The class object of the class to check the casting is possible (can be {@code null} what means to checking is required)
 * @return The created object or {@code null}, if something goes wrong (detailed information goes only to the log)
 */
    static public Object newInstanceOf(ClassLoader classLoader, String className, Class ensureInstanceOf)
    {
	NullCheck.notNull(classLoader, "classLoader");
	NullCheck.notEmpty(className, "className");
	final Object obj;
	try {
	    obj = Class.forName(className, true, classLoader).getDeclaredConstructor().newInstance();
	}
	catch (Throwable e)
	{
	    Log.error(LOG_COMPONENT, "unable to create an instance of the class " + className + ":" + e.getClass().getName() + ": " + e.getMessage());
	    return null;
	}
	if (ensureInstanceOf == null)
	    return obj;
	if (!ensureInstanceOf.isInstance(obj))
	{
	    Log.error(LOG_COMPONENT, "the newly created instance of the class " + className + " is not an instance of " + ensureInstanceOf.getName());
	    return null;
	}
	return obj;
    }

}
