// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.events;

import org.luwrain.core.*;

public class ListeningFinishedEvent extends SystemEvent
{
    protected final Object extraInfo;

    public ListeningFinishedEvent(Object extraInfo)
    {
	super(Code.LISTENING_FINISHED);
	this.extraInfo = extraInfo;
    }

	public Object getExtraInfo()
	{
	    return extraInfo;
	}
}
