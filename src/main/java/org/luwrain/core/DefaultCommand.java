// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public abstract class DefaultCommand implements Command
{
    protected final String name;

        protected abstract void onCommandImpl(Luwrain luwrain) throws Throwable;

        public DefaultCommand(String name)
    {
	if (name == null)
	    throw new IllegalArgumentException("name can't be null");
	if (name.isBlank())
	    throw new IllegalArgumentException("name can't be empty");
	this.name = name.trim();
    }

    @Override public String getName()
    {
	return name;
    }


@Override public void onCommand(Luwrain luwrain)
    {
	try {
	onCommandImpl(luwrain);
	}
	catch(Throwable e)
	{
	    luwrain.crash(e);
	}
    }
}
