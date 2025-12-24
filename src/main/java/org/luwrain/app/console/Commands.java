// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.console;

import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;

import org.luwrain.core.*;

final class Commands
{
    static private final Logger LOG = LogManager.getLogger();

    
    static final class Prop implements ConsoleCommand
    {
	private final Luwrain luwrain;
	Prop(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	}
	@Override public boolean onCommand(String text, App app)
	{
	    if (!Utils.firstWord(text).equals("prop"))
		return false;
	    final int pos = text.indexOf(" ");
	    if (pos < 0)
	    {
		LOG.trace("prop: no argument");
		return true;
	    }
	    final String arg = text.substring(pos).trim();
	    if (arg.isEmpty())
	    {
				LOG.trace("prop: no argument");
				return true;
	    }
	    final File fileValue = luwrain.getFileProperty(arg);
	    if (fileValue != null)
	    {
		LOG.trace("file: " + fileValue.toString() + " (" + fileValue.getAbsolutePath() + ")");
		return true;
	    }
	    final String value = luwrain.getProperty(arg);
	    if (!value.isEmpty())
		LOG.trace(value); else
		LOG.trace("empty");
	    return true;
	}
    }
}
