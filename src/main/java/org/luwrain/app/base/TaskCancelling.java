// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.base;

import java.util.*;
import java.util.concurrent.*;
import org.luwrain.core.*;
import static java.util.Objects.*;

/**
 * Provides a thread-safe mechanism for managing cancellable background tasks
 * with built-in protection against race conditions between task completion and
 * cancellation.
 *
 * <p>
 * When an application launches a background task and the user triggers another
 * action that starts a new task (potentially cancelling the previous one), the
 * completion handler of the old task may still fire after the new task has
 * already begun. This class solves that problem by assigning a monotonically
 * increasing unique identifier to each task. Completion handlers verify that
 * their task ID is still current before executing any side effects.
 * </p>
 *
 * <h2>Usage</h2>
 * <p>
 * This class is intended to be used as a superclass for
 * {@link org.luwrain.app.base.AppBase AppBase}. Subclasses call
 * {@link #newTaskId()} when starting a new background operation and
 * {@link #isRunningTaskId(TaskId)} when the operation finishes to check
 * whether the result is still relevant.
 * </p>
 *
 * <pre>{@code
 * TaskId id = newTaskId();
 * executor.execute(() -> {
 *     try {
 *         // perform background work
 *     } finally {
 *         synchronized (this) {
 *             if (isRunningTaskId(id)) {
 *                 // apply results
 *             }
 *         }
 *     }
 * });
 * }</pre>
 *
 * @see AppBase
 */
public class TaskCancelling
{
    /**
     * Unique identifier for a single background task instance.
     *
     * <p>
     * Each task ID is associated with a particular {@code TaskCancelling}
     * instance and encodes a monotonically increasing number. The
     * {@link #finish()} method ensures that the completion handler for a given
     * ID is invoked at most once, guarding against double-finalization.
     * </p>
     */
    static public final class TaskId
    {
	private final long id;
	private boolean finished = false;

	/**
	 * Creates a new task identifier with the specified numeric value.
	 *
	 * @param id A non-negative integer that uniquely identifies this task
	 *           within the owning {@link TaskCancelling} instance
	 * @throws IllegalArgumentException if {@code id} is negative
	 */
	public TaskId(long id)
	{
	    if (id < 0)
		throw new IllegalArgumentException("id (" + String.valueOf(id) + ") may not be negative");
	    this.id = id;
	}

	/**
	 * Returns the numeric identifier of this task.
	 *
	 * @return The task's unique numeric ID
	 */
	protected long getId()
	{
	    return this.id;
	}

	/**
	 * Marks this task as finished. If the task has already been marked
	 * finished, this method returns {@code false} and has no effect.
	 *
	 * <p>
	 * This is a one-way transition: once a task is finished, it cannot be
	 * unfinished. This provides protection against double-completion of the
	 * same task.
	 * </p>
	 *
	 * @return {@code true} if the task was successfully marked as finished,
	 *         {@code false} if it was already finished
	 */
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

    /**
     * Creates a new unique task identifier and resets the cancellation flag.
     *
     * <p>
     * This method increments the internal counter and returns a {@link TaskId}
     * representing the new current task. It also clears the internal
     * cancellation flag so that the new task starts in a non-cancelled state.
     * </p>
     *
     * @return A new {@link TaskId} instance for the upcoming background task
     */
    synchronized public TaskId newTaskId()
    {
	this.id++;
	this.cancelled = false;
	return new TaskId(this.id);
    }

    /**
     * Signals that the currently running task should be cancelled.
     *
     * <p>
     * This method sets the internal cancellation flag to {@code true}. Tasks
     * should periodically check {@link #isRunningTaskId(TaskId)} to see
     * whether they have been cancelled and should abort their work.
     * </p>
     */
    synchronized public void cancelTask()
    {
	this.cancelled = true;
    }

    /**
     * Checks whether the specified task ID is still the current, non-cancelled
     * task.
     *
     * <p>
     * A task ID is considered running if both conditions hold:
     * </p>
     * <ol>
     *   <li>Its numeric ID matches the latest ID produced by
     *       {@link #newTaskId()} (meaning no newer task has been started).</li>
     *   <li>The cancellation flag is {@code false} (meaning
     *       {@link #cancelTask()} has not been called since the task was
     *       started).</li>
     * </ol>
     *
     * @param taskId The task ID to check; must not be {@code null}
     * @return {@code true} if this task is still the current active task and
     *         has not been cancelled, {@code false} otherwise
     * @throws NullPointerException if {@code taskId} is {@code null}
     */
    synchronized public boolean isRunningTaskId(TaskId taskId)
    {
	requireNonNull(taskId, "taskId can't be null");
	return this.id == taskId.getId() && !this.cancelled;
    }
}
