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

import org.luwrain.core.events.InputEvent;

import static java.util.Objects.*;

public final class Action
{
    public final String name;
    public final String title;
    public final InputEvent inputEvent;

    public Action(String name, String title)
    {
	this(name, title, null);
	    }

    public Action(String name, String title, InputEvent event)
    {
	this.name = requireNonNull(name, "name can' tbe null");
	this.title = requireNonNull(title, "title can't be null");
	this.inputEvent = event;
	if (name.isEmpty())
	    throw new IllegalArgumentException("name can't be empty");
	if (title.isEmpty())
	    throw new IllegalArgumentException("title can't be empt ");
    }
}
