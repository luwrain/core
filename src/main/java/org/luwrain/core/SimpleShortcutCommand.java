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

import static java.util.Objects.*;

public class SimpleShortcutCommand implements Command
{
    protected final String cmdName;
    protected final String shortcutName;

    public SimpleShortcutCommand(String cmdName, String shortcutName)
    {
	this.cmdName = requireNonNull(cmdName, "cmdName can't be null");
	this.shortcutName = requireNonNull(shortcutName, "shortcutName can't be null");
	if (cmdName.isEmpty())
	    throw new IllegalArgumentException("cmdName can't be empty");
	if (shortcutName.isEmpty())
	    throw new IllegalArgumentException("shortcutName can't be empty");
    }

    public SimpleShortcutCommand(String name)
    {
	this(name, name);
    }

    @Override public String getName()
    {
	return cmdName;
    }

    @Override public void onCommand(Luwrain luwrain)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	luwrain.launchApp(shortcutName);
    }
}
