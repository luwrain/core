// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import static java.util.Objects.*;

public class SingleArgShortcut implements Shortcut
{
    protected final String shortcutName;
    protected final Class appClass;

    public SingleArgShortcut(String shortcutName, Class appClass)
    {
	requireNonNull(shortcutName, "shortcutName can't be null");
	requireNonNull(appClass, "appClass can't be null");
	if (shortcutName.isEmpty())
	    throw new IllegalArgumentException("shortcutName can't be empty");
	this.shortcutName = shortcutName;
	this.appClass = appClass;
    }

        @Override public String getExtObjName()
    {
	return shortcutName;
    }

        @SuppressWarnings("unchecked")
    @Override public Application[] prepareApp(String[] args)
    {
	requireNonNull(args, "args can't be null");
	if (args.length < 1)
	    throw new IllegalArgumentException("args must have at least one element");
	if (args[0] == null)
	    throw new NullPointerException("args[0] can't be null");
	try {
	    final Object o = appClass.getDeclaredConstructor(String.class).newInstance(args[0]);
	    if (o == null || !(o instanceof Application))
		return new Application[0];
	    return new Application[]{(Application)o};
	}
	catch(Exception e)
	{
	    return new Application[0];
	}
    }

        @Override public Set<Flags> getShortcutFlags()
    {
	return EnumSet.noneOf(Flags.class);
    }

        @Override public String[] getFileExtensions()
    {
	return new String[0];
    }
}
