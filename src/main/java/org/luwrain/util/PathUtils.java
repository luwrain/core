// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

//LWR_API 1.0

package org.luwrain.util;

import java.io.*;

import org.luwrain.core.*;

public final class PathUtils
{
    static public String escapeBash(String s)
    {
	boolean inApos = false;
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < s.length();i++)
	{
	    final char c = s.charAt(i);
	    if (!Character.isDigit(c) && !Character.isLetter(c) && c != '-' && c != '_')
		inApos = true;
	    if (c == '\'')
	    {
		b.append("\'\\\'\'");
		inApos = true;
		continue;
	    }
	    b.append(c);
	}
	if (inApos)
	    return "\'" + new String(b) + "\'";
	return new String(b);
    }
}
