// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.i18n;

import java.lang.reflect.*;
import static java.util.Objects.*;

public final class EmptyStringsObj implements InvocationHandler
{
    @SuppressWarnings("unchecked")
    public <T> T create(ClassLoader classLoader, Class stringsClass)
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
