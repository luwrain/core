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
import org.luwrain.controls.edit.*;
import org.luwrain.script.core.*;

import static java.util.Objects.*;

public final class EditUpdateObj implements ProxyObject
{
    static private String[] KEYS = new String[]{
	"hotPoint",
	"lines",
    };
    static private final Set<String> KEYS_SET = new HashSet<>(Arrays.asList(KEYS));
    static private final ProxyArray KEYS_ARRAY = ProxyArray.fromArray((Object[])KEYS);

    protected final MutableLinesArray lines;
    protected final HotPointObj hotPoint;

    public EditUpdateObj(MutableLines lines, HotPoint hotPoint)
    {
	this.lines = new MutableLinesArray(requireNonNull(lines, "lines can't be null"));
	this.hotPoint = new HotPointObj(requireNonNull(hotPoint));
    }

    @Override public Object getMember(String name)
    {
	requireNonNull(name, "name can't be null");
	switch(name)
	{
	case "lines":
	    return this.lines;
	case "hotPoint":
	    return this.hotPoint;
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name) { return KEYS_SET.contains(name); }
    @Override public Object getMemberKeys() { return KEYS_ARRAY; }
    @Override public void putMember(String name, Value value) { throw new RuntimeException("The edit object doesn't support updating of its variables"); }
}
