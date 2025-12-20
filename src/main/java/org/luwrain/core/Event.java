// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public class Event
{
    private transient volatile boolean processed = false;
    private transient final Object syncObj = new Object();

    /**
     * Signals that the processing of this event is finished. Do not touch
     * this method as it is designed for invocation by 
     * {@code org.luwrain.core.Environment} class only which controls the event
     * loop. Once this method is called, all threads freezed on {@code waitForBeProcessed()}
     * method continue the execution.
     */
    final void markAsProcessed()
    {
	if (processed)
	    return;
	processed = true;
	synchronized (syncObj) 
	{
	    syncObj.notifyAll();
	}
    }

    /**
     * Freezes current thread until this event be processed. This method
     * guarantees that the execution will continue after the core completely
     * finishes processing of the event. This method may not be used in the
     * same thread as the main thread of the core (there are no any
     * corresponding checks). The improper use of this method will hang the
     * system infinitely.
     *
     * @throws InterruptedException if the thread should terminate
     */
    public final void waitForBeProcessed() throws InterruptedException
    {
	if (processed)
	    return;
	synchronized (syncObj) {
	    while (!processed)
		syncObj.wait();
	}
    }
}
