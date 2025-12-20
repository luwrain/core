// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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
