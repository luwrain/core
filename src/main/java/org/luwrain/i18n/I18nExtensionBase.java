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
}
