// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import static java.util.Objects.*;

public final class CmdLine
{
    private final String[] cmdLine;

    public CmdLine(String[] cmdLine)
    {
	NullCheck.notNullItems(cmdLine, "cmdLine");
	this.cmdLine = cmdLine.clone();
    }

    public boolean used(String option)
    {
	requireNonNull(option, "option can't be null");
	for(String s: cmdLine)
	    if (s.equals(option))
		return true;
	return false;
    }

    public String getFirstArg(String prefix)
    {
	NullCheck.notEmpty(prefix, "prefix");
	for(String s: cmdLine)
	{
	    if (s.length() < prefix.length() || !s.startsWith(prefix))
		continue;
	    return s.substring(prefix.length());
	}
	return null;
    }

    public String[] getArgs(String prefix)
    {
	requireNonNull(prefix, "prefix can't be null");
		final List<String> res = new ArrayList<>();
	for(String s: cmdLine)
	{
	    if (s.length() < prefix.length() || !s.startsWith(prefix))
		continue;
	    res.add(s.substring(prefix.length()));
	}
	return res.toArray(new String[res.size()]);
    }
}
