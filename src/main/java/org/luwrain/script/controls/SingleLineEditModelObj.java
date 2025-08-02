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
import org.luwrain.controls.*;

import static java.util.Objects.*;

public final class SingleLineEditModelObj implements ProxyObject
{
    static private String[] KEYS = new String[]{
	"hotPoint",
	"line",
    };
    static private final Set<String> KEYS_SET = new HashSet<>(Arrays.asList(KEYS));
    static private final ProxyArray KEYS_ARRAY = ProxyArray.fromArray((Object[])KEYS);

    protected final SingleLineEdit.Model model;

    public SingleLineEditModelObj(SingleLineEdit.Model model)
    {
	this.model = requireNonNull(model, "model can't be null");
    }

    @Override public Object getMember(String name)
    {
	requireNonNull(name, "name can't be null");
	switch(name)
	{
	case "line":
	    return model.getLine();
	case "hotPoint":
	    return Integer.valueOf(model.getHotPointX());
	default:
	    return null;
	}
    }

    @Override public void putMember(String name, Value value)
    {
	throw new RuntimeException("The edit object doesn't support updating of its variables");
    }

    @Override public boolean hasMember(String name) { return KEYS_SET.contains(name); }
    @Override public Object getMemberKeys() { return KEYS_ARRAY; }

}
