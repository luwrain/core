// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.base;

import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;

import static java.util.Objects.*;

/**
 * The abstract base class for a LUWRAIN application.
 *
 * <p>
 * Every LUWRAIN application should extend this class. It implements the
 * {@link Application} interface and provides:
 * </p>
 * <ul>
 *   <li><strong>Lifecycle management</strong> — initialization via
 *       {@link #onAppInit()}, shutdown via {@link #closeApp()} and
 *       {@link #onAppClose()}.</li>
 *   <li><strong>Automatic i18n strings retrieval</strong> — the type parameter
 *       {@code <S>} specifies the strings class, and strings are fetched
 *       automatically through {@code org.luwrain.i18n.I18n#getStrings(Class)}.</li>
 *   <li><strong>Event handling</strong> — default processing of input events
 *       ({@link #onInputEvent(Area, InputEvent)}, {@link #onInputEvent(Area, InputEvent, Runnable)}),
 *       system events ({@link #onSystemEvent(Area, SystemEvent)},
 *       {@link #onSystemEvent(Area, SystemEvent, LayoutBase.Actions)}), and
 *       area queries ({@link #onAreaQuery(Area, AreaQuery)}).</li>
 *   <li><strong>Background task support</strong> — running cancellable tasks
 *       via {@link #runTask(TaskId, TaskRunnable)} with automatic background
 *       sound indication while busy.</li>
 *   <li><strong>Layout management</strong> — the application holds an
 *       {@link AreaLayoutHelper} and exposes convenience methods like
 *       {@link #setAreaLayout(LayoutBase)}.</li>
 * </ul>
 *
 * <h2>ESCAPE and TAB Processing</h2>
 * <p>
 * The {@link #onInputEvent(Area, InputEvent, Runnable)} method handles the
 * ESCAPE key specially:
 * </p>
 * <ol>
 *   <li>If the application is busy (see {@link #isBusy()}), ESCAPE cancels the
 *       current task.</li>
 *   <li>If a closing callback ({@code Runnable}) is provided, it is invoked.</li>
 *   <li>Otherwise, {@link #onEscape()} is called, which subclasses may
 *       override.</li>
 * </ol>
 * <p>
 * TAB processing (enabled by default, see {@link #setTabProcessing(boolean)})
 * moves focus to the next area in the layout.
 * </p>
 *
 * <h2>Background Tasks</h2>
 * <p>
 * Use {@link #runTask(TaskId, TaskRunnable)} to execute work in the background.
 * While a task is running, {@link #isBusy()} returns {@code true} and the
 * background sound changes to {@link BkgSounds#FETCHING}. When the task
 * completes, the caller must invoke {@link #finishedTask(TaskId, Runnable)} to
 * run a UI-thread callback. If a task throws an exception,
 * {@link #onException(Throwable)} is called, which by default reports the
 * exception via {@link Luwrain#crash(Throwable)}.
 * </p>
 *
 * @param <S> The type of the i18n strings object for this application; the
 *            class of this type is passed to the constructor and used to fetch
 *            localized strings via {@code org.luwrain.i18n.I18n#getStrings(Class)}
 *
 * @see TaskCancelling
 * @see LayoutBase
 * @see Application
 * @see AreaLayoutHelper
 */
abstract public class AppBase<S> extends TaskCancelling implements Application
{
    private Luwrain luwrain = null;
    private S strings = null;
    final String stringsName;
    final Class<S> stringsClass;
    private final String helpSection;
    private AreaLayoutHelper layout = null;
    private String appName = "";
    private Area[] visibleAreas = new Area[0];
    private FutureTask task = null;
    private boolean tabProcessing = true;

    /**
     * Functional interface for a runnable that represents a background task.
     *
     * <p>
     * Unlike {@link Runnable}, the {@link #run()} method of this interface
     * may throw a checked {@link Exception}. If an exception propagates out
     * of {@code run()}, it is caught by {@link AppBase#runTask(TaskId, TaskRunnable)}
     * and passed to {@link AppBase#onException(Throwable)}.
     * </p>
     */
    public interface TaskRunnable
    {
	/**
	 * Executes the background task. May throw any exception, which will
	 * be caught and reported by the framework.
	 *
	 * @throws Exception if the task fails for any reason
	 */
	void run() throws Exception;
    }

    /**
     * Creates a new application instance.
     *
     * @param stringsName The fully qualified class name of the strings
     *                    interface or class; used for i18n lookup.
     *                    Must not be {@code null} or empty
     * @param stringsClass The {@link Class} object for the strings type
     *                     {@code <S>}; must not be {@code null}
     * @param helpSection The help section identifier for this application's
     *                    documentation, or {@code null} if the application
     *                    does not provide context help
     * @throws NullPointerException if {@code stringsName} or
     *                              {@code stringsClass} is {@code null}
     */
    public AppBase(String stringsName, Class<S> stringsClass, String helpSection)
    {
	this.stringsName = requireNonNull(stringsName, "stringsName");
	this.stringsClass = requireNonNull(stringsClass, "stringsClass");
	this.helpSection = helpSection;
    }

    /**
     * Creates a new application instance without a help section.
     *
     * @param stringsName The fully qualified class name of the strings
     *                    interface or class; used for i18n lookup.
     *                    Must not be {@code null} or empty
     * @param stringsClass The {@link Class} object for the strings type
     *                     {@code <S>}; must not be {@code null}
     * @throws NullPointerException if {@code stringsName} or
     *                              {@code stringsClass} is {@code null}
     */
        public AppBase(String stringsName, Class<S> stringsClass)
    {
	this(stringsName, stringsClass, null);
    }

    /**
     * Creates a new application instance using the class name of the strings
     * type as the strings name.
     *
     * @param stringsClass The {@link Class} object for the strings type
     *                     {@code <S>}; must not be {@code null}. The fully
     *                     qualified class name is used as the strings name
     * @param helpSection The help section identifier, or {@code null}
     * @throws NullPointerException if {@code stringsClass} is {@code null}
     */
    public AppBase(Class<S> stringsClass, String helpSection)
    {
	this(stringsClass.getName(), stringsClass, helpSection);
    }

    /**
     * Creates a new application instance using the class name of the strings
     * type as the strings name, without a help section.
     *
     * @param stringsClass The {@link Class} object for the strings type
     *                     {@code <S>}; must not be {@code null}
     * @throws NullPointerException if {@code stringsClass} is {@code null}
     */
            public AppBase(Class<S> stringsClass)
    {
	this(stringsClass.getName(), stringsClass, null);
    }

    /**
     * Called during application initialization to construct the initial
     * area layout.
     *
     * <p>
     * This is the primary method that subclasses must implement. It should
     * create all areas, arrange them in an {@link AreaLayout} (typically via
     * a {@link LayoutBase} subclass), and return that layout. If this method
     * returns {@code null}, the application will fail to start with an
     * {@link InitResult} containing an exception.
     * </p>
     * <p>
     * This method is called from {@link #onLaunchApp(Luwrain)} on the UI
     * thread. It may access {@link #getLuwrain()}, {@link #getStrings()},
     * and other application facilities.
     * </p>
     *
     * @return The initial area layout for this application; must not be
     *         {@code null}
     * @throws Exception if initialization fails for any reason
     */
    abstract protected AreaLayout onAppInit() throws Exception;

    /**
     * Called by the LUWRAIN core when the application is launched.
     *
     * <p>
     * This method stores the {@link Luwrain} reference, fetches the i18n
     * strings, calls {@link #onAppInit()} to build the initial layout, and
     * wraps it in an {@link AreaLayoutHelper} that keeps the core informed
     * of layout changes.
     * </p>
     *
     * @param luwrain The LUWRAIN core instance; must not be {@code null}
     * @return An {@link InitResult} indicating success or failure
     */
    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	this.luwrain = requireNonNull(luwrain, "luwrain can't be null");
	this.strings = luwrain.i18n().getStrings(stringsClass);
	final AreaLayout initialLayout;
	try {
	    initialLayout = onAppInit();
	    	if (initialLayout == null)
		    throw new Exception("The application is unable to initialize");
	}
	catch(Throwable e)
	{
	    return new InitResult(e);
	}
	this.layout = new AreaLayoutHelper(()->{
		this.setVisibleAreas(layout.getLayout().getAreas());
		luwrain.onNewAreaLayout();
	    }, initialLayout);
			this.setVisibleAreas(layout.getLayout().getAreas());
	return new InitResult();
    }

    /**
     * Requests the LUWRAIN core to close this application.
     *
     * <p>
     * This method triggers the application shutdown sequence. The core will
     * eventually call {@link #onAppClose()} to allow cleanup.
     * </p>
     */
    public void closeApp()
    {
	luwrain.closeApp();
    }

    /**
     * Called when the application is about to close.
     *
     * <p>
     * Subclasses may override this method to perform cleanup: saving state,
     * releasing resources, stopping background threads, etc. The default
     * implementation does nothing.
     * </p>
     */
    @Override public void onAppClose()
    {
    }

    /**
     * Returns the human-readable name of this application.
     *
     * <p>
     * This name is typically displayed to the user, e.g. in the application
     * list or in a window title. It should be set via
     * {@link #setAppName(String)} during initialization.
     * </p>
     *
     * @return The application name; never {@code null}, but may be empty
     *         if not set
     */
    @Override public String getAppName()
    {
	return this.appName;
    }

    /**
     * Sets the human-readable name of this application.
     *
     * @param appName The new application name; must not be {@code null} or
     *                empty
     * @throws NullPointerException if {@code appName} is {@code null}
     * @throws IllegalArgumentException if {@code appName} is empty
     */
    public void setAppName(String appName)
    {
	NullCheck.notEmpty(appName, "appName");
	this.appName = appName;
    }

    /**
     * Returns the current area layout of this application.
     *
     * @return The current {@link AreaLayout}; never {@code null} after
     *         successful initialization
     */
    @Override public AreaLayout getAreaLayout()
    {
	return this.layout.getLayout();
    }

    /**
     * Called when the currently running background task has been cancelled.
     *
     * <p>
     * Subclasses may override this method to react to task cancellation, e.g.
     * to update the UI, display a message, or clean up partial results. The
     * default implementation does nothing.
     * </p>
     *
     * @see #cancelTask()
     */
    public void onCancelledTask()
    {
    }

    /**
     * Called when the ESCAPE key is pressed and there is no busy task to
     * cancel and no closing callback.
     *
     * <p>
     * Subclasses may override this method to provide custom behaviour for the
     * ESCAPE key. A typical use case is navigating back: if the application
     * has a navigation stack, ESCAPE could pop the current screen.
     * </p>
     *
     * @return {@code true} if the event was handled, {@code false} to let the
     *         area process it
     */
    public boolean onEscape()
    {
	return false;
    }

    /**
     * Handles an input event originating from an area, with an optional
     * closing callback.
     *
     * <p>
     * This is the primary input event handler. It processes special keys
     * according to the following logic:
     * </p>
     * <ol>
     *   <li>If the event is not a special key or has modifiers, it returns
     *       {@code false} immediately.</li>
     *   <li><strong>ESCAPE:</strong> If the application is busy
     *       ({@link #isBusy()}), the current task is cancelled. Otherwise, if
     *       a {@code closing} callback is provided, it is invoked; if not,
     *       {@link #onEscape()} is called.</li>
     *   <li><strong>TAB:</strong> If {@code tabProcessing} is enabled and
     *       there is a next area in the layout, focus moves to it.</li>
     * </ol>
     *
     * @param area The area that received the input event; must not be
     *             {@code null}
     * @param event The input event; must not be {@code null}
     * @param closing An optional callback invoked when the area should be
     *                closed (typically bound to ESCAPE); may be {@code null}
     * @return {@code true} if the event was handled, {@code false} if it
     *         should be passed to the area's own handler
     * @throws NullPointerException if {@code area} or {@code event} is
     *                              {@code null}
     */
    public boolean onInputEvent(Area area, InputEvent event, Runnable closing)
    {
	requireNonNull(area, "area can't be null");
	requireNonNull(event, "event can't be null");
	if (!event.isSpecial() || event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case ESCAPE:
	    if (isBusy())
	    {
	    cancelTask();
	    return true;
	    }
	    if (closing != null)
	    {
		closing.run();
		return true;
	    }
	    return onEscape();
	case TAB:
	    if (tabProcessing)
	    {
		final Area nextArea = layout.getLayout().getNextArea(area);
		if (nextArea == null)
		    return false;
		luwrain.setActiveArea(nextArea);
		return true;
	    } else
		return false;
	}
	return false;
    }

    /**
     * Handles an input event originating from an area, without a closing
     * callback.
     *
     * <p>
     * Equivalent to calling {@link #onInputEvent(Area, InputEvent, Runnable)}
     * with {@code null} for the closing argument.
     * </p>
     *
     * @param area The area that received the input event; must not be
     *             {@code null}
     * @param event The input event; must not be {@code null}
     * @return {@code true} if the event was handled, {@code false} otherwise
     * @throws NullPointerException if {@code area} or {@code event} is
     *                              {@code null}
     */
        public boolean onInputEvent(Area area, InputEvent event)
    {
	requireNonNull(area, "area can't be null");
	requireNonNull(event, "event can't be null");
	return onInputEvent(area, event, null);
    }

    /**
     * Handles a system event.
     *
     * <p>
     * This implementation processes two system events:
     * </p>
     * <ul>
     *   <li><strong>{@link SystemEvent.Code#HELP HELP}:</strong> Opens the
     *       help section specified in the constructor, if any.</li>
     *   <li><strong>{@link SystemEvent.Code#CLOSE CLOSE}:</strong> Calls
     *       {@link #closeApp()} to close the application.</li>
     * </ul>
     * <p>
     * Only regular system events (type {@link SystemEvent.Type#REGULAR}) are
     * processed.
     * </p>
     *
     * @param area The area that received the system event; may be {@code null}
     *             for events not associated with a specific area
     * @param event The system event; must not be {@code null}
     * @return {@code true} if the event was handled, {@code false} otherwise
     * @throws NullPointerException if {@code event} is {@code null}
     */
    public boolean onSystemEvent(Area area, SystemEvent event)
    {
	requireNonNull(event, "event can't be null");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return false;
	switch(event.getCode())
	{
	case HELP:
	    if (helpSection == null || helpSection.isEmpty())
		return false;
	    return luwrain.openHelp(helpSection);
	case CLOSE:
	    closeApp();
	    return true;
	default:
	    return false;
	}
    }

    /**
     * Handles a system event, with support for area-level {@link LayoutBase.Actions}.
     *
     * <p>
     * This overload first checks for an {@link SystemEvent.Code#ACTION} event
     * and attempts to dispatch it through the provided {@code actions} object.
     * If the action is handled, it returns {@code true}. Otherwise, it falls
     * back to {@link #onSystemEvent(Area, SystemEvent)}.
     * </p>
     *
     * @param area The area that received the system event; may be {@code null}
     * @param event The system event; must not be {@code null}
     * @param actions The area-level actions to consult for ACTION events;
     *                must not be {@code null}
     * @return {@code true} if the event was handled, {@code false} otherwise
     * @throws NullPointerException if {@code event} or {@code actions} is
     *                              {@code null}
     */
    public boolean onSystemEvent(Area area, SystemEvent event, LayoutBase.Actions actions)
    {
	requireNonNull(event, "event can't be null");
	if (event.getType() == SystemEvent.Type.REGULAR)
	switch(event.getCode())
	{
	case ACTION:
	    if (actions.onActionEvent(event))
		return true;
	}
	return onSystemEvent(area, event);
    }

    /**
     * Handles an area query.
     *
     * <p>
     * This implementation processes {@link AreaQuery#BACKGROUND_SOUND}
     * queries: if the application is busy ({@link #isBusy()}), it answers
     * with {@link BkgSounds#FETCHING} to let the core know that a
     * "fetching/busy" background sound should be played. All other queries
     * are passed through to the area.
     * </p>
     *
     * @param area The area that received the query; must not be {@code null}
     * @param query The area query; must not be {@code null}
     * @return {@code true} if the query was answered, {@code false} if it
     *         should be passed to the area's own handler
     * @throws NullPointerException if {@code area} or {@code query} is
     *                              {@code null}
     */
    public boolean onAreaQuery(Area area, AreaQuery query)
    {
	requireNonNull(area, "area can't be null");
	requireNonNull(query, "query can't be null");
			    switch(query.getQueryCode())
		    {
		    case AreaQuery.BACKGROUND_SOUND:
			if (isBusy())
			{
			    ((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(BkgSounds.FETCHING));
			    return true;
			}
			return false;
		    default:
			return false;
		    }
    }

    /**
     * Updates the cached array of visible areas.
     *
     * <p>
     * This is called automatically when the layout changes. The cached array
     * is used to notify all visible areas when the background task state
     * changes (e.g. when a task starts or finishes).
     * </p>
     *
     * @param visibleAreas The new array of visible areas; must not contain
     *                     {@code null} elements
     * @throws NullPointerException if {@code visibleAreas} or any of its
     *                              elements is {@code null}
     */
    void setVisibleAreas(Area[] visibleAreas)
    {
	NullCheck.notNullItems(visibleAreas, "visibleAreas");
	this.visibleAreas = visibleAreas.clone();
    }

    /**
     * Submits a {@link FutureTask} for background execution.
     *
     * <p>
     * If the application is already busy ({@link #isBusy()}), this method
     * returns {@code false} and does nothing. Otherwise, the task is stored
     * and submitted to the LUWRAIN background executor, and all visible areas
     * are notified of the background sound change.
     * </p>
     *
     * @param task The task to execute; must not be {@code null}
     * @return {@code true} if the task was submitted, {@code false} if the
     *         application is already busy
     * @throws NullPointerException if {@code task} is {@code null}
     */
    private boolean runTask(FutureTask task)
    {
	requireNonNull(task, "task can't be null");
	if (isBusy())
	    return false;
	this.task = task;
	luwrain.executeBkg(this.task);
	for(Area a: visibleAreas)
	    luwrain.onAreaNewBackgroundSound(a);
	return true;
    }

    /**
     * Runs a background task with the specified identifier and body.
     *
     * <p>
     * The task body ({@link TaskRunnable#run()}) is executed on a background
     * thread. If it throws an exception, {@link #onException(Throwable)} is
     * called on the UI thread. In any case (success or failure),
     * {@link #finishedTask(TaskId, Runnable)} is called after the task
     * completes with a no-op continuation.
     * </p>
     *
     * @param taskId The unique identifier for this task, obtained from
     *               {@link TaskCancelling#newTaskId()}; must not be {@code null}
     * @param runnable The task body; must not be {@code null}
     * @return {@code true} if the task was submitted, {@code false} if the
     *         application is already busy
     * @throws NullPointerException if {@code taskId} or {@code runnable} is
     *                              {@code null}
     * @see #finishedTask(TaskId, Runnable)
     * @see #isBusy()
     */
    public boolean runTask(TaskId taskId, TaskRunnable runnable)
    {
	requireNonNull(taskId, "taskId can't be null");
	requireNonNull(runnable, "runnable can't be null");
	return runTask(new FutureTask<>(()->{
		    try {
			try {
			    runnable.run();
			}
			catch(Throwable e)
			{
			    finishedTask(taskId, ()->onException(e));
			}
		    }
		    finally {
			finishedTask(taskId, ()->{});
		    }
	}, null));
    }

    /**
     * Marks a background task as finished and schedules a callback on the UI
     * thread.
     *
     * <p>
     * This method should be called by the background task when it completes.
     * It verifies that the task ID is still current (via
     * {@link TaskCancelling#isRunningTaskId(TaskId)}) and, if so, schedules
     * the provided callback on the UI thread.
     * </p>
     * <p>
     * If the task ID is no longer current (because a new task has been started
     * or the current task has been cancelled), the callback is not invoked.
     * This prevents stale task completions from interfering with the current
     * application state.
     * </p>
     *
     * @param taskId The task identifier; must not be {@code null}
     * @param runnable The callback to execute on the UI thread; must not be
     *                 {@code null}
     * @throws NullPointerException if {@code taskId} or {@code runnable} is
     *                              {@code null}
     */
    public synchronized void finishedTask(TaskId taskId, Runnable runnable)
    {
	requireNonNull(taskId, "taskId can't be null");
	requireNonNull(runnable, "runnable can't be null");
	if (!isBusy() || !taskId.finish())
	    return;
	luwrain.runUiSafely(()->{
		if (!isRunningTaskId(taskId))
		    return;
		resetTask();
		runnable.run();
	    });
    }

    /**
     * Cancels the currently running background task.
     *
     * <p>
     * This method interrupts the underlying {@link FutureTask}, plays the
     * {@link Sounds#CLICK} sound, resets the internal task reference, and
     * calls {@link #onCancelledTask()} to allow subclasses to react.
     * </p>
     * <p>
     * If no task is currently running ({@link #isBusy()} returns
     * {@code false}), this method does nothing.
     * </p>
     */
    @Override public void cancelTask()
    {
	if (!isBusy())
	    return;
		super.cancelTask();
	task.cancel(true);
	luwrain.playSound(Sounds.CLICK);
	resetTask();
	onCancelledTask();
    }

    /**
     * Resets the internal task reference and notifies all visible areas of
     * the background sound change.
     *
     * <p>
     * After this method, {@link #isBusy()} will return {@code false} (since
     * {@code task} is set to {@code null}), and the fetching background sound
     * will stop.
     * </p>
     */
    void resetTask()
    {
	if (this.task == null)
	    return;
	this.task = null;
	for(Area a: visibleAreas)
	    luwrain.onAreaNewBackgroundSound(a);
    }

    /**
     * Checks whether the application is currently executing a background task.
     *
     * <p>
     * An application is considered busy if there is a non-{@code null} task
     * that has not yet completed.
     * </p>
     *
     * @return {@code true} if a background task is in progress,
     *         {@code false} otherwise
     */
    public final boolean isBusy()
    {
	return task != null && !task.isDone();
    }

    /**
     * Called when a background task throws an exception.
     *
     * <p>
     * The default implementation reports the exception via
     * {@link Luwrain#crash(Throwable)}, which displays an error dialog to the
     * user. Subclasses may override this method to provide custom error
     * handling.
     * </p>
     *
     * @param e The exception thrown by the background task; must not be
     *          {@code null}
     */
    public void onException(Throwable e)
    {
	 luwrain.crash(e);
	 }

    /**
     * Returns the {@link AreaLayoutHelper} managing this application's
     * layout.
     *
     * <p>
     * Subclasses rarely need to access this directly; use
     * {@link #setAreaLayout(LayoutBase)} to change the layout.
     * </p>
     *
     * @return The layout helper; never {@code null} after initialization
     */
    protected AreaLayoutHelper getLayout()
    {
	return this.layout;
    }

    /**
     * Replaces the current area layout with the layout from the specified
     * {@link LayoutBase}.
     *
     * <p>
     * This is the primary way to change what the user sees: create a new
     * {@link LayoutBase} instance, configure its areas, and call this method
     * to switch to it.
     * </p>
     *
     * @param layout The new layout; must not be {@code null} and must have
     *               its area layout set via {@link LayoutBase#setAreaLayout}
     * @throws NullPointerException if {@code layout} is {@code null}
     */
    public void setAreaLayout(LayoutBase layout)
    {
	requireNonNull(layout, "layout can't be null");
	getLayout().setBasicLayout(layout.getAreaLayout());
    }

    /**
     * Returns the LUWRAIN core instance associated with this application.
     *
     * @return The {@link Luwrain} instance; never {@code null} after
     *         {@link #onLaunchApp(Luwrain)} has been called
     */
    public Luwrain getLuwrain()
    {
	return this.luwrain;
    }

    /**
     * Returns the localized strings object for this application.
     *
     * @return The strings object of type {@code <S>}; never {@code null} after
     *         initialization
     */
    public S getStrings()
    {
	return this.strings;
    }

    /**
     * Reports a crash to the LUWRAIN core, displaying an error dialog to the
     * user.
     *
     * @param t The throwable representing the crash; must not be {@code null}
     * @throws NullPointerException if {@code t} is {@code null}
     */
    public void crash(Throwable t)
    {
	requireNonNull(t, "t can't be null");
	luwrain.crash(t);
    }

    /**
     * Returns the {@code org.luwrain.i18n.I18n} instance for accessing
     * localized resources.
     *
     * @return The i18n instance; never {@code null}
     */
        public org.luwrain.i18n.I18n getI18n()
    {
	return luwrain.i18n();
    }

    /**
     * Sets the event response that will be delivered to the user after the
     * current event processing is complete.
     *
     * <p>
     * The event response may include spoken text, sounds, braille output,
     * and other feedback. This method delegates directly to
     * {@link Luwrain#setEventResponse(EventResponse)}.
     * </p>
     *
     * @param resp The event response to set; must not be {@code null}
     * @throws NullPointerException if {@code resp} is {@code null}
     */
    public void setEventResponse(EventResponse resp)
    {
	requireNonNull(resp, "resp can't be null");
	luwrain.setEventResponse(resp);
    }

    /**
     * Displays a message to the user with the specified type.
     *
     * <p>
     * The message is spoken and, if appropriate, shown on the braille display.
     * The type determines how the message is presented (e.g. as a regular
     * notification, an error, etc.).
     * </p>
     *
     * @param text The message text; must not be {@code null} or empty
     * @param type The message type; must not be {@code null}
     * @throws NullPointerException if {@code text} or {@code type} is
     *                              {@code null}
     */
    public void message(String text, Luwrain.MessageType type)
    {
	requireNonNull(text, "text can't be null");
	requireNonNull(type, "type can't be null");
	luwrain.message(text, type);
    }

    /**
     * Displays a message to the user with the default message type.
     *
     * @param text The message text; must not be {@code null} or empty
     * @throws NullPointerException if {@code text} is {@code null}
     */
    public void message(String text)
    {
	requireNonNull(text, "text can't be null");
	luwrain.message(text);
    }

    /**
     * Returns whether TAB processing is enabled.
     *
     * <p>
     * When enabled (the default), pressing TAB moves focus to the next area
     * in the current layout.
     * </p>
     *
     * @return {@code true} if TAB processing is enabled, {@code false}
     *         otherwise
     */
    protected boolean getTabProcessing()
    {
	return this.tabProcessing;
    }

    /**
     * Enables or disables TAB processing.
     *
     * <p>
     * When disabled, TAB events are not intercepted by the application and
     * are passed through to areas, which may handle them differently (e.g.
     * in a text editor where TAB inserts a tab character).
     * </p>
     *
     * @param tabProcessing {@code true} to enable TAB processing (default),
     *                      {@code false} to disable it
     */
    protected void setTabProcessing(boolean tabProcessing)
    {
	this.tabProcessing = tabProcessing;
    }
}
