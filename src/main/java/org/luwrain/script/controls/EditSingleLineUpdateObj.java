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

package org.luwrain.script.controls;

import java.util.*;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;
import org.luwrain.core.*;

import static java.util.Objects.*;
import static org.luwrain.script.ScriptUtils.*;

public final class EditSingleLineUpdateObj implements ProxyObject
{
    static private String[] KEYS = new String[]{
	"hotPoint",
	"line",
    };
    static private final Set<String> KEYS_SET = new HashSet<>(Arrays.asList(KEYS));
    static private final ProxyArray KEYS_ARRAY = ProxyArray.fromArray((Object[])KEYS);

    protected final MutableLines lines;
    protected final HotPointControl hotPoint;

    public EditSingleLineUpdateObj(MutableLines lines, HotPointControl hotPoint)
    {
	this.lines = requireNonNull(lines, "lines can't be null");
		this.hotPoint = requireNonNull(hotPoint, "hotPoint can't be null");
    }

    @Override public Object getMember(String name)
    {
	requireNonNull(name, "name can't be null");
	switch(name)
	{
	case "line":
	    return lines.getLine(hotPoint.getHotPointY());
	case "hotPoint":
	    return Integer.valueOf(hotPoint.getHotPointX());
	default:
	    return null;
	}
    }

    @Override public void putMember(String name, Value value)
    {
	requireNonNull(name, "name can't be null");
	switch(name)
	{
	case "hotPoint": {
	    final var intValue = asInt(value);
	    if (intValue < 0)
		throw new IllegalArgumentException("Value of a hot point can't be negative");
	    hotPoint.setHotPointX(intValue);
	    return;
	}
	case "line":
	    lines.setLine(hotPoint.getHotPointY(), requireNonNullElse(asString(value), ""));
	    return;
	default:
	    throw new IllegalArgumentException("No such property: " + name);
	}
    }

    @Override public boolean hasMember(String name) { return KEYS_SET.contains(name); }
    @Override public Object getMemberKeys() { return KEYS_ARRAY; }

}
