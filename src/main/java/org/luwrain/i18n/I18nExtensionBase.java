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

import java.net.*;
import java.io.*;
import java.util.*;

import org.luwrain.core.*;

import static java.util.Objects.*;
import static org.luwrain.util.ResourceUtils.*;

public class I18nExtensionBase extends EmptyExtension
{
    static public final String COMMAND_PREFIX = "command.";
    static public final String STATIC_PREFIX = "static.";
    static public final String STRINGS_PREFIX = "strings.";
    static public final String CHARS_PREFIX = "chars.";

    protected ClassLoader classLoader = null;
    protected Luwrain luwrain = null;
    protected final String langName;

    protected I18nExtensionBase(String langName)
    {
	requireNonNull(langName, "langName can't be null");
	this.langName = langName;
    }

    protected void init(ClassLoader classLoader, Luwrain luwrain)
    {
	this.classLoader = requireNonNull(classLoader, "classLoader");
	this.luwrain = requireNonNull(luwrain, "luwrain");
    }

    protected Map<String, String> readStaticStrings() throws IOException
    {
	return readResource("static.txt");
    }

        protected Map<String, String> readChars() throws IOException
    {
	return readResource("chars.txt");
    }

    protected void loadCommands(I18nExtension ext) throws IOException
    {
final var res = readResource("commands.txt");
for(var e: res.entrySet())
    ext.addCommandTitle(langName, e.getKey(), e.getValue());
    }

    protected Map<String, String> readResource(String resName) throws IOException
    {
	final var res = new HashMap<String, String>();
	final var lines = readStringResourceAsList(getClass(), resName, "UTF-8");
	for(var l: lines)
	    if (!l.trim().isEmpty() && l.trim().charAt(0) != '#')
	    {
		final var pos = l.indexOf("=");
		if (pos < 0)
		    continue;
		final String
		key = l.substring(0, pos).trim(),
		value = l.substring(pos + 1).trim();
		if (!key.isEmpty() && !value.isEmpty())
		    res.put(key, value);
	    }
	return res;
    }

    protected void loadProperties(String resourcePath, I18nExtension ext) throws IOException
    {
	NullCheck.notEmpty(resourcePath, "resourcePath");
	NullCheck.notNull(ext, "ext");
	final Properties props = new Properties();
	final URL url = classLoader.getResource(resourcePath);
	if (url == null)
	{
	    Log.error(langName, "No resource " + resourcePath);
	    return;
	}
	props.load(new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")));
	final Enumeration e = props.propertyNames();
	while(e.hasMoreElements())
	{
	    final String k = (String)e.nextElement();
	    final String v = props.getProperty(k);
	    if (v == null)
	    {
		Log.warning(langName, "key \'" + k + "\' in resource file " + resourcePath+ " doesn\'t have value");
		continue;
	    }
	    processPropItem(k, v, ext, resourcePath);
	}
    }

    protected void processPropItem(String k, String v, I18nExtension ext, String resourcePath)
    {
	NullCheck.notEmpty(k, "k");
	NullCheck.notNull(v, "v");
	NullCheck.notNull(ext, "ext");
	NullCheck.notNull(resourcePath, "resourcePath");

	//strings
	if (k.trim().startsWith(STRINGS_PREFIX))
	{
	    final String c = k.trim().substring(STRINGS_PREFIX.length());
	    if (c.trim().isEmpty())
	    {
		Log.warning(langName, "the illegal key \'" + k + "\' in resource file " + resourcePath);
		return;
	    }
	    if (!addProxyByClassName(c.trim(), v.trim(), resourcePath, ext))
		Log.debug(langName, "unable to create proxy strings object \'" + c + "\' for interface " + v.trim());
	    return;
	}
    }

    protected boolean addProxyByClass(String name, Class stringsClass, String propertiesResourceName, I18nExtension ext)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(stringsClass, "stringsClass");
	NullCheck.notEmpty(propertiesResourceName, "propertiesResourceName");
	NullCheck.notNull(ext, "ext");
	final Object strings;
	try {
	    strings = PropertiesProxy.create(luwrain, langName, classLoader.getResource(propertiesResourceName), name + ".", stringsClass);
	}
	catch(java.io.IOException e)
	{
	    e.printStackTrace();
	    return false;
	}
	ext.addStrings(langName, "luwrain." + name, strings);
	return true;
    }

    protected boolean addProxyByClassName(String name, String className, String propertiesResourceName, I18nExtension ext)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(className, "className");
	NullCheck.notEmpty(propertiesResourceName, "propertiesResourceName");
	NullCheck.notNull(ext, "ext");
	final Class cl;
	try {
	    cl = Class.forName(className, true, classLoader);
	}
	catch (Throwable e)
	{
	    Log.debug(langName, "unable to find the class " + className + ": " + e.getClass().getName() + ": " + e.getMessage());
	    return false;
	}
	return addProxyByClass(name, cl, propertiesResourceName, ext);
    }
}
