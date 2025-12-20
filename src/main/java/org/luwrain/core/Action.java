// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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
