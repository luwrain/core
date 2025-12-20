// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

import static org.luwrain.core.Base.*;
import static org.luwrain.core.NullCheck.*;

final class EventQueue
{
    static private final int
	MAX_LEN_LIMIT = 1024;

    static private final String LOG_COMPONENT = Base.LOG_COMPONENT;
    private final LinkedList<Event> events = new LinkedList<>();

    synchronized void putEvent(Event e)
    {
	notNull(e, "e");
	if (events.size() >= MAX_LEN_LIMIT)
	{
	    warn("exceeding max number of unprocessed  events in the events queue (" + MAX_LEN_LIMIT + ")");
	    return;
	}
	    events.addLast(e);
	    notify();
    }

    synchronized Event pickEvent()
    {
	try {
	    while(events.isEmpty())
	    wait();
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	}
	final Event e = events.pollFirst();
	return e;
	    }
}
