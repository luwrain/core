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

package org.luwrain.util;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

import static java.util.Objects.*;
import static org.luwrain.core.NullCheck.*;

public final class ResourceUtils
{
    static public List<String> readStringResourceAsList(Class c, String resourceName, String charset) throws IOException
    {
	requireNonNull(c, "c can't be null");
	requireNonNull(resourceName, "resourceName can't be null");
	requireNonNull(charset, "charset can't be null");
	if (resourceName.isEmpty())
	    throw new IllegalArgumentException("resourceName can't be empty");
	if (charset.isEmpty())
	    throw new IllegalArgumentException("charset can't be empty");
	final var res = new ArrayList<String>();
	try (final BufferedReader r = new BufferedReader(new InputStreamReader(c.getResourceAsStream(resourceName), charset))) {
	    {
		String line = r.readLine();
		while (line != null)
		{
		    res.add(line);
		    line = r.readLine();
		}
		return res;
	    }
	}
    }

    static public String readStringResource(Class c, String resourceName, String charset, String lineSeparator) throws IOException
    {
	notNull(c, "c");
	notEmpty(resourceName, "resourceName");
	notEmpty(charset, "charset");
	notEmpty(lineSeparator, "lineSeparator");
	final StringBuilder b = new StringBuilder();
	try (final BufferedReader r = new BufferedReader(new InputStreamReader(c.getResourceAsStream(resourceName), charset))) {
	    {
		String line = r.readLine();
		while (line != null)
		{
		    b.append(line).append(lineSeparator);
		    line = r.readLine();
		}
		return new String(b);
	    }
	}
    }

    static public String getStringResource(Class c, String resourceName) throws IOException
    {
	return readStringResource(c, resourceName, "UTF-8", System.lineSeparator());
    }
}
