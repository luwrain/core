// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.crash;

import org.luwrain.core.*;

public class CustomMessageException extends Exception
{
    protected final String[] message;

    public CustomMessageException(String[] message)
    {
	super("CustomMessageException can't be used as a real exception");
	NullCheck.notNullItems(message, "message");
	this.message = message.clone();
    }

    public final String[] getCustomMessage()
    {
	return this.message.clone();
    }
}
