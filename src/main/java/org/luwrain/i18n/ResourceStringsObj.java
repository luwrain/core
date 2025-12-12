// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.i18n;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;

import static java.util.Objects.*;

public final class ResourceStringsObj implements InvocationHandler
{
        static private final String CHARSET = "UTF-8";

    private final ClassLoader classLoader;
    private final Properties props;

    public ResourceStringsObj(ClassLoader classLoader, Class cl, String resName) throws IOException
    {
	requireNonNull(resName, "resName can't be null");
	if (resName.isEmpty())
	    throw new IllegalArgumentException("resName can't be empty");
	this.classLoader = requireNonNull(classLoader, "classLoader can't be null");
	this.props = new Properties();
	try (final BufferedReader r = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(resName), CHARSET))) {
	    props.load(r);
	}
    }

    @SuppressWarnings("unchecked")
    public <T> T create(String langName, Class cl)
    {
	requireNonNull(langName, "langName can't be null");
	requireNonNull(cl, "cl can't be null");
	if (langName.isEmpty())
	    throw new IllegalArgumentException("langName can't be empty");
	return (T) java.lang.reflect.Proxy.newProxyInstance(classLoader, new Class[]{cl}, this);
    }

    @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
	{
	    String name = method.getName();
	    if (name.length() > 1 && Character.isLowerCase(name.charAt(0)))
		name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
	    final String value = props.getProperty(name);
	    if (value == null)
	    {
		final Object[] a;
		if (args != null)
		{
		    a = new Object[args.length];
		    for(int i = 0;i < args.length;i++)
		    {
			if (args[i] == null)
			    a[i] = null; else
			    if (args[i] instanceof String || args[i] instanceof Number || args[i] instanceof Boolean)
				a[i] = args[i]; else
				a[i] = args[i].toString();
		    }
		} else
		    a = new Object[0];
		return "#No value: " + name + "#";
	    }
	    if (value.indexOf("$") < 0)
		return value.trim();
	    final var b = new StringBuilder();
	    for(int i = 0;i < value.length();++i)
		if (value.charAt(i) != '$' || i + 1 >= value.length() ||
		    value.charAt(i + 1) < '0' || value.charAt(i + 1) > '9')
		    b.append(value.charAt(i)); else
		    b.append(args[value.charAt(++i) - '1'].toString());
	    return new String(b).toString();
	}
    }
}    
