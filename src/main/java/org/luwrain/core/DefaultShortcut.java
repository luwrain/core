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

package org.luwrain.core;

import java.util.*;
import org.apache.logging.log4j.*;

import static java.util.Objects.*;

public class DefaultShortcut implements Shortcut
{
    static private final Logger log = LogManager.getLogger();

    protected final String shortcutName;
    protected final Class appClass;
    protected final Set<Flags> flags;
    protected final String[] fileExtensions;

    public DefaultShortcut(String shortcutName, Class appClass, Set<Flags> flags, String[] fileExtensions)
    {
	requireNonNull(shortcutName, "shortcutName can't be null");
requireNonNull(appClass, "appClass can't be null");
if (shortcutName.isEmpty())
    throw new IllegalArgumentException("shortcutName can't be empty");
	this.shortcutName = shortcutName;
	this.appClass = appClass;
	this.flags = flags;
	this.fileExtensions = fileExtensions.clone();
    }

    public DefaultShortcut(String shortcutName, Class appClass)
    {
	requireNonNull(shortcutName, "shortcutName can't be null");
requireNonNull(appClass, "appClass can't be null");
if (shortcutName.isEmpty())
    throw new IllegalArgumentException("shortcutName can't be empty");
	this.shortcutName = shortcutName;
	this.appClass = appClass;
	this.flags = EnumSet.noneOf(Flags.class);
	this.fileExtensions = new String[0];
    }

        @Override public String getExtObjName()
    {
	return shortcutName;
    }

    @SuppressWarnings("unchecked")
    @Override public Application[] prepareApp(String[] args)
    {
requireNonNull(args, "args can't be null");
for(int i = 0;i < args.length;i++)
    if (args[i] == null)
	throw new NullPointerException("args[" + i + "] can't be null");
	try {
	    final Object o = appClass.getDeclaredConstructor().newInstance();
	    if (o == null || !(o instanceof Application))
	    {
		log.error("Unable to create new instance of the class " + appClass.getName() + " for the shortcut '" + shortcutName + "': the result is null or is not an instance of org.luwrain.core.Application");
		return new Application[0];
	    }
	    return new Application[]{(Application)o};
	}
	catch(Exception e)
	{
	    log.error("Unable to create new instance of the class " + appClass.getName() + " for the shortcut '" + shortcutName, e);
	    return new Application[0];
	}
    }

    @Override public Set<Flags> getShortcutFlags()
    {
	return flags;
    }

    @Override public String[] getFileExtensions()
    {
	return fileExtensions.clone();
    }
}
