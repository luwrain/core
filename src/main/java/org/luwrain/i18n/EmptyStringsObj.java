/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.i18n;

import java.lang.reflect.*;
import static java.util.Objects.*;

public final class EmptyStringsObj implements InvocationHandler
{
    @SuppressWarnings("unchecked")
    public <T> T create(ClassLoader classLoader, String langName, Class stringsClass)
    {
	requireNonNull(stringsClass, "stringsClass can't be null");
	return (T) java.lang.reflect.Proxy.newProxyInstance(classLoader, new Class[]{stringsClass}, this);
    }

    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
    {
	final String name = method.getName();
	if (name.length() > 1)
	    return Character.toUpperCase(name.charAt(0)) + name.substring(1);
	return name;
    }
}
