// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.base;

import java.util.*;
import java.util.concurrent.*;

import org.luwrain.core.*;

public class TaskCancelling
{
    static public final class TaskId
    {
	private final long id;
	private boolean finished = false;
	public TaskId(long id)
	{
	    if (id < 0)
		throw new IllegalArgumentException("id (" + String.valueOf(id) + ") may not be negative");
	    this.id = id;
	}
	protected long getId()
	{
	    return this.id;
	}
	protected boolean finish()
	{
	    if (finished)
		return false;
	    finished = true;
	    return true;
	}
    }

    private volatile long id = 0;
    private volatile boolean cancelled = false;

    synchronized public TaskId newTaskId()
    {
	this.id++;
	this.cancelled = false;
	return new TaskId(this.id);
    }

    synchronized public void cancelTask()
    {
	this.cancelled = true;
    }

    synchronized public boolean isRunningTaskId(TaskId taskId)
    {
	NullCheck.notNull(taskId, "taskId");
	return this.id == taskId.getId() && !this.cancelled;
    }
}
