// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.base;

import java.util.*;
import java.util.function.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import org.apache.logging.log4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.controls.edit.*;
import org.luwrain.script.core.*;
import org.luwrain.script.controls.*;

import static java.util.Objects.*;
import static org.luwrain.script.Hooks.*;

/**
 * The main class for working with area layouts in a LUWRAIN application.
 *
 * <p>
 * A {@code LayoutBase} represents a single screen or view of an application.
 * It manages a set of areas, their arrangement via {@link AreaLayout}, their
 * associated {@link Actions}, and optional properties handlers. The key
 * responsibility of this class is to <em>wrap</em> each area in a protective
 * shell that catches any exceptions thrown by the area, preventing application
 * crashes from destabilizing the entire LUWRAIN platform.
 * </p>
 *
 * <h2>Area Wrapping</h2>
 * <p>
 * Every area added to a layout through
 * {@link #getWrappingArea(Area, Actions)} or any of the
 * {@code setAreaLayout()} methods is automatically wrapped. The wrapper:
 * </p>
 * <ul>
 *   <li>Delegates all {@link Area} methods to the original area.</li>
 *   <li>Catches any {@link Throwable} thrown by the original area and reports
 *       it via {@link Luwrain#crash(Throwable)}.</li>
 *   <li>Interposes the application's input and system event handlers before
 *       forwarding events to the original area.</li>
 *   <li>Supports area-level {@link Actions}, a close handler, an OK handler,
 *       and a properties handler.</li>
 * </ul>
 *
 * <h2>Lifecycle</h2>
 * <ol>
 *   <li>Create a subclass of {@code LayoutBase}, typically passing an
 *       {@link AppBase} instance to the constructor.</li>
 *   <li>Create areas and configure them.</li>
 *   <li>Call one of the {@code setAreaLayout()} methods to define the layout
 *       arrangement.</li>
 *   <li>Return the layout from the app's {@link AppBase#onAppInit()} or switch
 *       to it later via {@link AppBase#setAreaLayout(LayoutBase)}.</li>
 * </ol>
 *
 * <h2>Actions</h2>
 * <p>
 * {@link Actions} provide a declarative way to associate named actions, keyboard
 * shortcuts, and handlers with a layout. Actions are exposed to the LUWRAIN
 * core via {@link Area#getAreaActions()} and can be invoked via system events.
 * </p>
 *
 * @see AppBase
 * @see AreaLayout
 * @see Area
 */
public class LayoutBase
{
    static private final Logger log = LogManager.getLogger();

    /**
     * Functional interface for handling a named action.
     *
     * <p>
     * Implementations should return {@code true} if the action was successfully
     * handled, or {@code false} if the action could not be performed (e.g. due
     * to current state being incompatible with the action).
     * </p>
     */
public interface ActionHandler
{
    /**
     * Called when the associated action is invoked.
     *
     * @return {@code true} if the action was handled, {@code false} otherwise
     */
    boolean onAction();
}

    /**
     * Functional interface for handling the PROPERTIES system event on a
     * specific area.
     *
     * <p>
     * Typically bound to ALT+ENTER, the properties handler is expected to
     * create and return a new {@link LayoutBase} that presents a properties
     * dialog or settings screen for the given area. If the handler returns
     * {@code null}, the event is not considered handled.
     * </p>
     */
    public interface PropertiesHandler
    {
	/**
	 * Called when the PROPERTIES event is triggered on an area.
	 *
	 * @param area The area for which properties are requested; never
	 *             {@code null}
	 * @return A new {@link LayoutBase} representing the properties screen,
	 *         or {@code null} to ignore the event
	 */
	LayoutBase onProperties(Area area);
    }

    /**
     * Functional interface that determines whether an action is currently
     * enabled.
     *
     * <p>
     * Used with {@link ActionInfo#ActionInfo(String, String, ActionHandler, ActionInfoCondition)}
     * to dynamically enable or disable actions based on the current application
     * state.
     * </p>
     */
    public interface ActionInfoCondition
    {
	/**
	 * Checks whether the associated action should be available.
	 *
	 * @return {@code true} if the action is currently enabled,
	 *         {@code false} if it should be hidden or disabled
	 */
	boolean isActionInfoEnabled();
    }

    /**
     * Describes a single action within a layout.
     *
     * <p>
     * An {@code ActionInfo} binds together:
     * </p>
     * <ul>
     *   <li><strong>name</strong> — the internal action identifier (used to
     *       match system {@code ACTION} events).</li>
     *   <li><strong>title</strong> — the human-readable action title,
     *       potentially localized.</li>
     *   <li><strong>inputEvent</strong> — an optional keyboard shortcut
     *       ({@code null} if the action has no direct key binding).</li>
     *   <li><strong>handler</strong> — the {@link ActionHandler} to invoke.</li>
     *   <li><strong>cond</strong> — an optional {@link ActionInfoCondition}
     *       that controls whether the action is enabled ({@code null} means
     *       always enabled).</li>
     * </ul>
     *
     * @see Actions
     * @see ActionHandler
     * @see ActionInfoCondition
     */
    public final class ActionInfo
    {
	final String name;
	final String title;
	final InputEvent inputEvent;
	final ActionHandler handler;
	final ActionInfoCondition cond;

	/**
	 * Creates a new action description with all fields specified.
	 *
	 * @param name The internal action name; must not be {@code null} or
	 *             empty
	 * @param title The human-readable action title; must not be
	 *              {@code null} or empty
	 * @param inputEvent The keyboard shortcut for this action, or
	 *                   {@code null} if none
	 * @param handler The action handler to invoke; must not be
	 *                {@code null}
	 * @param cond An optional condition controlling whether the action is
	 *             enabled; {@code null} means always enabled
	 * @throws NullPointerException if {@code name}, {@code title}, or
	 *                              {@code handler} is {@code null}
	 */
	public ActionInfo(String name, String title, InputEvent inputEvent, ActionHandler handler, ActionInfoCondition cond)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notEmpty(title, "title");
	    this.name = name;
	    this.title = title;
	    this.inputEvent = inputEvent;
	    this.handler = requireNonNull(handler, "handler can't be null");
	    this.cond = cond;
	}

	/**
	 * Creates a new action description without a condition (always enabled).
	 *
	 * @param name The internal action name; must not be {@code null} or
	 *             empty
	 * @param title The human-readable action title; must not be
	 *              {@code null} or empty
	 * @param inputEvent The keyboard shortcut for this action, or
	 *                   {@code null} if none
	 * @param handler The action handler to invoke; must not be {@code null}
	 * @throws NullPointerException if {@code name}, {@code title}, or
	 *                              {@code handler} is {@code null}
	 */
		public ActionInfo(String name, String title, InputEvent inputEvent, ActionHandler handler) { this(name, title, inputEvent, handler, null); }

	/**
	 * Creates a new action description without a keyboard shortcut but
	 * with an optional condition.
	 *
	 * @param name The internal action name; must not be {@code null} or
	 *             empty
	 * @param title The human-readable action title; must not be
	 *              {@code null} or empty
	 * @param handler The action handler to invoke; must not be {@code null}
	 * @param cond An optional condition controlling whether the action is
	 *             enabled; {@code null} means always enabled
	 * @throws NullPointerException if {@code name}, {@code title}, or
	 *                              {@code handler} is {@code null}
	 */
	public ActionInfo(String name, String title, ActionHandler handler, ActionInfoCondition cond) { this(name, title, null, handler, cond); }

	/**
	 * Creates a new action description with no keyboard shortcut and no
	 * condition.
	 *
	 * @param name The internal action name; must not be {@code null} or
	 *             empty
	 * @param title The human-readable action title; must not be
	 *              {@code null} or empty
	 * @param handler The action handler to invoke; must not be {@code null}
	 * @throws NullPointerException if {@code name}, {@code title}, or
	 *                              {@code handler} is {@code null}
	 */
	public ActionInfo(String name, String title, ActionHandler handler) { this(name, title, null, handler, null); }
    }

    /**
     * An immutable collection of {@link ActionInfo} objects representing the
     * actions available in a layout or on a specific area.
     *
     * <p>
     * This class provides methods to:
     * </p>
     * <ul>
     *   <li>Convert the collection to an array of
     *       {@link org.luwrain.core.Action} objects for consumption by the
     *       LUWRAIN core via {@link #getAreaActions()}.</li>
     *   <li>Dispatch an action by name via {@link #handle(String)}.</li>
     *   <li>Process system ACTION events via
     *       {@link #onActionEvent(SystemEvent)}.</li>
     * </ul>
     * <p>
     * Actions whose {@link ActionInfoCondition} returns {@code false} are
     * excluded from the array returned by {@link #getAreaActions()}, but
     * can still be invoked by name via {@link #handle(String)}.
     * </p>
     */
    static public final class Actions
    {
	private final ActionInfo[] actions;

	/**
	 * Creates a new {@code Actions} collection from the specified array.
	 *
	 * <p>
	 * The array is defensively copied; subsequent modifications to the
	 * original array will not affect this collection.
	 * </p>
	 *
	 * @param actions The array of action descriptors; must not be
	 *                {@code null} and must not contain {@code null}
	 *                elements
	 * @throws NullPointerException if {@code actions} or any of its
	 *                              elements is {@code null}
	 */
	public Actions(ActionInfo[] actions)
	{
	    NullCheck.notNullItems(actions, "actions");
	    this.actions = actions.clone();
	}

	/**
	 * Creates an empty {@code Actions} collection.
	 */
	public Actions()
	{
	    this(new ActionInfo[0]);
	}

	/**
	 * Returns the current action descriptors as an array of
	 * {@link org.luwrain.core.Action} objects suitable for the LUWRAIN
	 * core.
	 *
	 * <p>
	 * Actions with a condition that returns {@code false} are excluded
	 * from the result. Each included action includes its name, title, and
	 * optional keyboard shortcut.
	 * </p>
	 *
	 * @return A non-{@code null} array of actions; may be empty
	 */
	public org.luwrain.core.Action[] getAreaActions()
	{
	    final List<org.luwrain.core.Action> res = new ArrayList<>();
	    for(ActionInfo a: actions)
		if (a.cond == null || a.cond.isActionInfoEnabled())
		{
		    if (a.inputEvent != null)
			res.add(new org.luwrain.core.Action(a.name, a.title, a.inputEvent)); else
			res.add(new org.luwrain.core.Action(a.name, a.title));
		}
	    return res.toArray(new org.luwrain.core.Action[res.size()]);
	}

	/**
	 * Dispatches an action by its internal name.
	 *
	 * <p>
	 * Iterates through the registered actions and invokes the handler of
	 * the first action whose name matches the specified name.
	 * </p>
	 *
	 * @param actionName The internal action name to dispatch; must not be
	 *                   {@code null} or empty
	 * @return {@code true} if a matching action was found and its handler
	 *         returned {@code true}, {@code false} if no matching action
	 *         was found
	 * @throws NullPointerException if {@code actionName} is {@code null}
	 */
	public boolean handle(String actionName)
	{
	    NullCheck.notEmpty(actionName, "actionName");
	    for(ActionInfo a: actions)
		if (a.name.equals(actionName))
		    return a.handler.onAction();
	    return false;
	}

	/**
	 * Processes a system {@code ACTION} event.
	 *
	 * <p>
	 * Iterates through registered actions and invokes the handler of the
	 * first action whose name matches the action name encoded in the event
	 * (see {@link ActionEvent#isAction(SystemEvent, String)}).
	 * </p>
	 *
	 * @param event The system event; must not be {@code null}
	 * @return {@code true} if a matching action handled the event,
	 *         {@code false} otherwise
	 * @throws NullPointerException if {@code event} is {@code null}
	 */
	boolean onActionEvent(SystemEvent event)
	{
	    requireNonNull(event, "event can't be null");
	    for(ActionInfo a: actions)
		if (ActionEvent.isAction(event, a.name))
		    return a.handler.onAction();
	    return false;
	}
    }

    /**
     * The application instance that owns this layout.
     *
     * <p>
     * May be {@code null} if this layout was created using the no-argument
     * constructor. In that case, all methods that require an application
     * reference (e.g. {@link #getLuwrain()}, {@link #getControlContext()})
     * will throw {@link IllegalStateException}.
     * </p>
     */
    protected final AppBase app;

    /**
     * The control context for this layout, lazily initialized.
     *
     * <p>
     * The control context is a {@link LayoutControlContext} wrapping a
     * {@link DefaultControlContext} and provides area-aware operation for
     * all standard LUWRAIN controls.
     * </p>
     */
    protected LayoutControlContext controlContext = null;

    private final Map<Area, Area> areaWrappers = new HashMap<>();

    /**
     * Maps original areas to their {@link PropertiesHandler}, if any.
     *
     * <p>
     * When a PROPERTIES system event (ALT+ENTER) is received on a wrapped
     * area, the associated handler is consulted to produce a properties layout.
     * </p>
     */
    private final Map<Area, PropertiesHandler> propHandlers = new HashMap<>();

    private AreaLayout areaLayout = null;

    /**
     * An optional handler invoked when the CLOSE system event is triggered
     * on a wrapped area.
     *
     * <p>
     * Set via {@link #setCloseHandler(ActionHandler)}. If {@code null}, no
     * special close handling is performed.
     * </p>
     */
    private ActionHandler closeHandler = null;

    /**
     * An optional handler invoked when the OK system event is triggered on
     * a wrapped area.
     *
     * <p>
     * Set via {@link #setOkHandler(ActionHandler)}. If {@code null}, the
     * event is forwarded to the original area.
     * </p>
     */
    private ActionHandler okHandler = null;

    /**
     * Creates a new layout associated with the specified application.
     *
     * <p>
     * This is the recommended constructor. The {@code app} reference is
     * required for most operations, including access to the LUWRAIN core,
     * control context, and area wrapping.
     * </p>
     *
     * @param app The application instance that owns this layout; must not
     *            be {@code null}
     * @throws NullPointerException if {@code app} is {@code null}
     */
    protected LayoutBase(AppBase app)
    {
	this.app = app;
    }

    /**
     * Creates a new layout without an application reference.
     *
     * <p>
     * <strong>Warning:</strong> Most methods of this class will throw
     * {@link IllegalStateException} when called on a layout created with
     * this constructor. Use {@link #LayoutBase(AppBase)} instead whenever
     * possible.
     * </p>
     */
    protected LayoutBase()
    {
	this(null);
    }

    /**
     * Creates an {@link Actions} collection from an array of action
     * descriptors.
     *
     * @param a The action descriptors; must not be {@code null} and must
     *          not contain {@code null} elements
     * @return A new {@code Actions} instance
     * @throws NullPointerException if {@code a} or any of its elements is
     *                              {@code null}
     */
    public Actions actions(ActionInfo ... a)
    {
	return new Actions(a);
	    }

    /**
     * Creates an {@link Actions} collection by concatenating two arrays of
     * action descriptors.
     *
     * <p>
     * The resulting collection contains all actions from {@code a2} followed
     * by all actions from {@code a1}.
     * </p>
     *
     * @param a1 The first set of action descriptors; must not be
     *           {@code null} and must not contain {@code null} elements
     * @param a2 The second set of action descriptors; must not be
     *           {@code null} and must not contain {@code null} elements
     * @return A new {@code Actions} instance containing all specified actions
     * @throws NullPointerException if any argument or element is {@code null}
     */
    public Actions actions(ActionInfo[] a1, ActionInfo ... a2)
    {
	final List<ActionInfo> res = new ArrayList<>();
	res.addAll(Arrays.asList(a2));
		res.addAll(Arrays.asList(a1));
	return new Actions(res.toArray(new ActionInfo[res.size()]));
    }

    /**
     * Creates an {@link ActionInfo} with a keyboard shortcut.
     *
     * @param name The internal action name; must not be {@code null} or empty
     * @param title The human-readable action title; must not be {@code null}
     *              or empty
     * @param inputEvent The keyboard shortcut for this action, or {@code null}
     * @param handler The action handler; must not be {@code null}
     * @return A new {@code ActionInfo} instance
     * @throws NullPointerException if {@code name}, {@code title}, or
     *                              {@code handler} is {@code null}
     */
    public ActionInfo action(String name, String title, InputEvent inputEvent, ActionHandler handler)
    {
	return new ActionInfo(name, title, inputEvent, handler);
    }

    /**
     * Creates an {@link ActionInfo} without a keyboard shortcut.
     *
     * @param name The internal action name; must not be {@code null} or empty
     * @param title The human-readable action title; must not be {@code null}
     *              or empty
     * @param handler The action handler; must not be {@code null}
     * @return A new {@code ActionInfo} instance
     * @throws NullPointerException if {@code name}, {@code title}, or
     *                              {@code handler} is {@code null}
     */
    public ActionInfo action(String name, String title, ActionHandler handler)
    {
	return new ActionInfo(name, title, handler);
    }

    /**
     * Creates an {@link ActionInfo} with a keyboard shortcut and an
     * enabling condition.
     *
     * @param name The internal action name; must not be {@code null} or empty
     * @param title The human-readable action title; must not be {@code null}
     *              or empty
     * @param inputEvent The keyboard shortcut for this action, or {@code null}
     * @param handler The action handler; must not be {@code null}
     * @param cond The enabling condition; {@code null} means always enabled
     * @return A new {@code ActionInfo} instance
     * @throws NullPointerException if {@code name}, {@code title}, or
     *                              {@code handler} is {@code null}
     */
    public ActionInfo action(String name, String title, InputEvent inputEvent, ActionHandler handler, ActionInfoCondition cond)
    {
	return new ActionInfo(name, title, inputEvent, handler, cond);
    }

    /**
     * Creates an {@link ActionInfo} with an enabling condition but without
     * a keyboard shortcut.
     *
     * @param name The internal action name; must not be {@code null} or empty
     * @param title The human-readable action title; must not be {@code null}
     *              or empty
     * @param handler The action handler; must not be {@code null}
     * @param cond The enabling condition; {@code null} means always enabled
     * @return A new {@code ActionInfo} instance
     * @throws NullPointerException if {@code name}, {@code title}, or
     *                              {@code handler} is {@code null}
     */
    public ActionInfo action(String name, String title, ActionHandler handler, ActionInfoCondition cond)
    {
	return new ActionInfo(name, title, handler, cond);
    }

    /**
     * Sets the close handler for this layout.
     *
     * <p>
     * When set, this handler is invoked whenever the CLOSE system event is
     * triggered on any wrapped area of this layout, before the event is
     * forwarded to the original area.
     * </p>
     *
     * @param closeHandler The handler to invoke on close; must not be
     *                     {@code null}
     * @throws NullPointerException if {@code closeHandler} is {@code null}
     */
    public void setCloseHandler(ActionHandler closeHandler)
    {
	this.closeHandler = requireNonNull(closeHandler, "closeHandler can't be null");
    }

    /**
     * Sets the OK handler for this layout.
     *
     * <p>
     * When set, this handler is invoked whenever the OK system event is
     * triggered on any wrapped area of this layout. If the handler returns
     * {@code true}, the event is considered handled and is not forwarded to
     * the original area.
     * </p>
     *
     * @param okHandler The handler to invoke on OK; must not be {@code null}
     * @throws NullPointerException if {@code okHandler} is {@code null}
     */
        public void setOkHandler(ActionHandler okHandler)
    {
	this.okHandler = requireNonNull(okHandler, "okHandler can't be null");
    }

    /**
     * Wraps an area in a protective shell, without area-level actions.
     *
     * <p>
     * Equivalent to {@link #getWrappingArea(Area, Actions)} with
     * {@code null} for the actions argument. The resulting wrapper:
     * </p>
     * <ul>
     *   <li>Delegates all {@link Area} methods to the original area.</li>
     *   <li>Catches any exceptions thrown by the original area and reports
     *       them via {@link Luwrain#crash(Throwable)}.</li>
     *   <li>Interposes the application's event handlers (input, system, and
     *       query) before forwarding to the original area.</li>
     *   <li>Supports the close handler, OK handler, and properties handler
     *       set on this layout.</li>
     * </ul>
     *
     * @param area The area to wrap; must not be {@code null}
     * @return A new area that wraps the original
     * @throws NullPointerException if {@code area} is {@code null}
     * @throws IllegalStateException if this layout was created without an
     *                               application reference
     */
        public Area getWrappingArea(Area area)
    {
	requireNonNull(area, "area can't be null");
	return getWrappingArea(area, null);
    }

    /**
     * Wraps an area in a protective shell, with optional area-level actions.
     *
     * <p>
     * This is the central method for integrating areas into a layout. Every
     * area passed to any {@code setAreaLayout()} method is automatically
     * passed through this method. The wrapper provides the following
     * guarantees:
     * </p>
     * <ul>
     *   <li><strong>Exception isolation:</strong> Every method of the
     *       {@link Area} interface is wrapped in a {@code try-catch} block.
     *       Any thrown {@link Throwable} is reported via
     *       {@link Luwrain#crash(Throwable)} and a safe default value is
     *       returned.</li>
     *   <li><strong>Event interposition:</strong> Input events go through
     *       {@link AppBase#onInputEvent(Area, InputEvent, Runnable)} and
     *       system events go through
     *       {@link AppBase#onSystemEvent(Area, SystemEvent, LayoutBase.Actions)}
     *       (or the non-actions overload) before reaching the original area.
     *       The app-level handler is given priority and can consume the event
     *       before the area sees it.</li>
     *   <li><strong>OK handling:</strong> If an {@link #setOkHandler(ActionHandler) OK handler}
     *       is set, OK system events are intercepted and handled by it.</li>
     *   <li><strong>Properties handling:</strong> If a
     *       {@link #setPropertiesHandler(Area, PropertiesHandler) properties handler}
     *       is registered for the original area, PROPERTIES system events
     *       trigger it and switch to the returned layout.</li>
     *   <li><strong>Area actions:</strong> If {@code actions} is non-{@code null},
     *       its {@link Actions#getAreaActions()} result is used instead of
     *       the original area's {@code getAreaActions()}.</li>
     * </ul>
     *
     * @param area The area to wrap; must not be {@code null}
     * @param actions Optional area-level actions; may be {@code null} to use
     *                the original area's actions
     * @return A new area that wraps the original
     * @throws NullPointerException if {@code area} is {@code null}
     * @throws IllegalStateException if this layout was created without an
     *                               application reference
     */
    public Area getWrappingArea(Area area, Actions actions)
    {
	requireNonNull(area, "area can't be null");
	if (app == null)
	    throw new IllegalStateException("No app instance, provide it using the corresponding constructor");
	final Area res = new Area(){
		@Override public int getLineCount()
		{
		    try {
			return area.getLineCount();
		    }
		    catch(Throwable e)
		    {
			getLuwrain().crash(e);
			return 1;
		    }
		}
		@Override public String getLine(int index)
		{
		    try {
			return area.getLine(index);
		    }
		    catch(Throwable e)
		    {
			getLuwrain().crash(e);
			return e.getClass().getName() + ": " + e.getMessage();
		    }
		}
		@Override public int getHotPointX()
		{
		    try {
			return area.getHotPointX();
		    }
		    catch(Throwable e)
		    {
			getLuwrain().crash(e);
			return 0;
		    }
		}
		@Override public int getHotPointY()
		{
		    try {
			return area.getHotPointY();
		    }
		    catch(Throwable e)
		    {
			getLuwrain().crash(e);
			return 0;
		    }
		}
		@Override public String getAreaName()
		{
		    try {
			return area.getAreaName();
		    }
		    catch(Throwable e)
		    {
			getLuwrain().crash(e);
			return e.getClass().getName() + ": " + e.getMessage();
		    }
		}
		@Override public boolean onInputEvent(InputEvent event)
		{
		    if (closeHandler != null)
		    {
			if (app.onInputEvent(this, event, ()->{closeHandler.onAction(); }))
			    return true;
		    } else
		    {
			if (app.onInputEvent(this, event))
			    return true;
		    }
		    try {
			return area.onInputEvent(event);
		    }
		    catch(Throwable e)
		    {
			getLuwrain().crash(e);
			return true;
		    }
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    if (actions != null)
		    {
			if (app.onSystemEvent(this, event, actions))
			    return true;
		    } else
		    {
			if (app.onSystemEvent(this, event))
			    return true;
		    }
		    try {
			if (event.getType() == SystemEvent.Type.REGULAR && event.getCode() == SystemEvent.Code.OK && okHandler != null)
			    return okHandler.onAction();
			if (app != null && event.getType() == SystemEvent.Type.REGULAR &&
			    event.getCode() == SystemEvent.Code.PROPERTIES && propHandlers.containsKey(area) &&
			    propHandlers.get(area) != null)
						{
						    final var propLayout = propHandlers.get(area).onProperties(area);
						    if (propLayout == null)
							return false;
						    app.setAreaLayout(propLayout);
						    app.getLuwrain().announceActiveArea();
			    return true;
						}
			return area.onSystemEvent(event);
		    }
		    catch(Throwable e)
		    {
			getLuwrain().crash(e);
			return true;
		    }
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    if (app.onAreaQuery(this, query))
			return true;
		    try {
			return area.onAreaQuery(query);
		    }
		    catch(Throwable e)
		    {
			getLuwrain().crash(e);
			return false;
		    }
		}
		@Override public Action[] getAreaActions()
		{
		    try {
			return actions != null?actions.getAreaActions():area.getAreaActions();
		    }
		    catch(Throwable e)
		    {
			getLuwrain().crash(e);
			return new Action[0];
		    }
		}
	    };
	areaWrappers.put(area, res);
	return res;
    }

    /**
     * Clears the internal mapping of original areas to their wrappers.
     *
     * <p>
     * This is typically called when the layout is being discarded and
     * replaced. After calling this method, previously created wrappers
     * remain functional but new wrappers will need to be created.
     * </p>
     */
    public void clearAreaWrappers()
    {
	areaWrappers.clear();
    }

    /**
     * Sets the area layout to a single area.
     *
     * <p>
     * The area is automatically wrapped via {@link #getWrappingArea(Area, Actions)}.
     * This is the simplest layout configuration: one area filling the entire
     * screen.
     * </p>
     *
     * @param area The area to display; must not be {@code null}
     * @param actions Optional area-level actions; may be {@code null}
     * @throws NullPointerException if {@code area} is {@code null}
     */
    public void setAreaLayout(Area area, Actions actions)
    {
	requireNonNull(area, "area can't be null");
	this.areaLayout = new AreaLayout(getWrappingArea(area, actions));
    }

    /**
     * Sets the area layout to two areas arranged according to the specified
     * type.
     *
     * <p>
     * Both areas are automatically wrapped. See {@link AreaLayout.Type} for
     * the available arrangement options (e.g. horizontal split, vertical
     * split).
     * </p>
     *
     * @param type The layout arrangement type; must not be {@code null}
     * @param area1 The first area; must not be {@code null}
     * @param actions1 Optional actions for the first area; may be {@code null}
     * @param area2 The second area; must not be {@code null}
     * @param actions2 Optional actions for the second area; may be {@code null}
     * @throws NullPointerException if {@code type}, {@code area1}, or
     *                              {@code area2} is {@code null}
     */
    public void setAreaLayout(AreaLayout.Type type, Area area1, Actions actions1, Area area2, Actions actions2)
    {
	requireNonNull(area1, "area1 can't be null");
	requireNonNull(area2, "area2 can't be null");
	this.areaLayout = new AreaLayout(type, getWrappingArea(area1, actions1), getWrappingArea(area2, actions2));
    }

    /**
     * Sets the area layout to three areas arranged according to the specified
     * type.
     *
     * <p>
     * All three areas are automatically wrapped.
     * </p>
     *
     * @param type The layout arrangement type; must not be {@code null}
     * @param area1 The first area; must not be {@code null}
     * @param actions1 Optional actions for the first area; may be {@code null}
     * @param area2 The second area; must not be {@code null}
     * @param actions2 Optional actions for the second area; may be {@code null}
     * @param area3 The third area; must not be {@code null}
     * @param actions3 Optional actions for the third area; may be {@code null}
     * @throws NullPointerException if {@code type}, {@code area1},
     *                              {@code area2}, or {@code area3} is
     *                              {@code null}
     */
    public void setAreaLayout(AreaLayout.Type type, Area area1, Actions actions1, Area area2, Actions actions2, Area area3, Actions actions3)
    {
	requireNonNull(area1, "area1 can't be null");
	requireNonNull(area2, "area2 can't be null");
		requireNonNull(area3, "area3 can't be null");
		this.areaLayout = new AreaLayout(type, getWrappingArea(area1, actions1), getWrappingArea(area2, actions2), getWrappingArea(area3, actions3));
    }

    /**
     * Returns the current area layout.
     *
     * @return The current {@link AreaLayout}; never {@code null}
     * @throws IllegalStateException if no layout has been set via
     *                               {@code setAreaLayout()}
     */
    public AreaLayout getAreaLayout()
    {
	if (this.areaLayout == null)
	    throw new IllegalStateException("No area layout, use setAreaLayout() to set it");
	return this.areaLayout;
    }

    /**
     * Returns the control context for this layout.
     *
     * <p>
     * The control context is lazily created on first access. It wraps a
     * {@link DefaultControlContext} and translates area references to their
     * wrapped equivalents, ensuring that all control operations benefit from
     * the exception isolation provided by area wrappers.
     * </p>
     *
     * @return The {@link ControlContext} for this layout; never {@code null}
     * @throws IllegalStateException if this layout was created without an
     *                               application reference
     */
    public ControlContext getControlContext()
    {
	if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
	if (this.controlContext == null)
	    this.controlContext = new LayoutControlContext(new DefaultControlContext(app.getLuwrain()));
	return this.controlContext;
    }

    /**
     * Returns the LUWRAIN core instance.
     *
     * @return The {@link Luwrain} instance; never {@code null}
     * @throws IllegalStateException if this layout was created without an
     *                               application reference
     */
    public Luwrain getLuwrain()
    {
		if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
		return app.getLuwrain();
    }

    /**
     * Sets the active (focused) area, automatically resolving the wrapper if
     * the specified area has been wrapped.
     *
     * @param area The area to activate; must not be {@code null}
     * @throws NullPointerException if {@code area} is {@code null}
     * @throws IllegalStateException if this layout was created without an
     *                               application reference
     */
    public void setActiveArea(Area area)
    {
	requireNonNull(area, "area can't be null");
		if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
		final Area a = areaWrappers.get(area);
		app.getLuwrain().setActiveArea(a != null?a:area);
    }

    /**
     * Returns the visible width (in characters) of the given area.
     *
     * @param area The area whose visible width to query; must not be
     *             {@code null}
     * @return The visible width in characters
     * @throws NullPointerException if {@code area} is {@code null}
     * @throws IllegalStateException if this layout was created without an
     *                               application reference
     */
    public int getAreaVisibleWidth(Area area)
    {
	requireNonNull(area, "area can't be null");
			if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
		final Area a = areaWrappers.get(area);
		return app.getLuwrain().getAreaVisibleWidth(a != null?a:area);
    }

    /**
     * Returns the visible height (in lines) of the given area.
     *
     * @param area The area whose visible height to query; must not be
     *             {@code null}
     * @return The visible height in lines
     * @throws NullPointerException if {@code area} is {@code null}
     * @throws IllegalStateException if this layout was created without an
     *                               application reference
     */
        public int getAreaVisibleHeight(Area area)
    {
	requireNonNull(area, "area can't be null");
			if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
		final Area a = areaWrappers.get(area);
		return app.getLuwrain().getAreaVisibleHeight(a != null?a:area);
    }

    /**
     * Registers a properties handler for the specified area.
     *
     * <p>
     * When the user presses ALT+ENTER (the PROPERTIES system event) on a
     * wrapped version of the given area, the handler's
     * {@link PropertiesHandler#onProperties(Area)} method is called. If it
     * returns a non-{@code null} layout, the application switches to that
     * layout and announces the active area.
     * </p>
     *
     * @param area The area for which to register the handler; must not be
     *             {@code null}
     * @param handler The properties handler; must not be {@code null}
     * @throws NullPointerException if {@code area} or {@code handler} is
     *                              {@code null}
     */
    public void setPropertiesHandler(Area area, PropertiesHandler handler)
    {
	requireNonNull(area, "area can't be null");
	requireNonNull(handler, "handler can't be null");
	propHandlers.put(area, handler);
    }

    /**
     * Returns an {@link ActionHandler} that, when invoked, switches the
     * application back to this layout and announces the active area.
     *
     * <p>
     * This is typically used as the handler for a "Back" or "Return" action
     * in a secondary layout, allowing the user to navigate back to the
     * previous screen.
     * </p>
     *
     * @return A new {@link ActionHandler} that restores this layout
     * @throws IllegalStateException if this layout was created without an
     *                               application reference
     */
    public ActionHandler getReturnAction()
    {
	if (app == null)
	    throw new IllegalStateException("No app instance, provide it using the corresponding constructor");
	return () -> {
	    app.setAreaLayout(this);
	    app.getLuwrain().announceActiveArea();
	    return true;
	};
    }

    /**
     * Functional interface for configuring a {@link ListArea.Params} instance
     * before the list area is constructed.
     *
     * @param <E> The type of list items
     */
    public interface ListParams<E> { void setListParams(ListArea.Params<E> params); }

    /**
     * Creates and configures a {@link ListArea.Params} object.
     *
     * <p>
     * The supplied {@link ListParams} callback is invoked to configure the
     * parameters. The control context and default appearance are
     * pre-populated.
     * </p>
     *
     * @param <E> The type of list items
     * @param l The callback to configure the parameters; must not be
     *          {@code null}
     * @return A configured {@link ListArea.Params} instance
     * @throws NullPointerException if {@code l} is {@code null}
     */
    public <E> ListArea.Params<E> listParams(ListParams<E> l)
    {
	requireNonNull(l, "l can't be null");
	final ListArea.Params<E> params = new ListArea.Params<>();
	params.context = getControlContext();
	params.appearance = new ListUtils.DefaultAppearance<E>(getControlContext());
	l.setListParams(params);
	return params;
    }

    /**
     * Functional interface for configuring an {@link EditArea.Params} instance
     * before the edit area is constructed.
     */
        public interface EditParams { void setEditParams(EditArea.Params params); }

    /**
     * Creates and configures an {@link EditArea.Params} object.
     *
     * <p>
     * The supplied {@link EditParams} callback is invoked to configure the
     * parameters. The control context is pre-populated.
     * </p>
     *
     * @param l The callback to configure the parameters; must not be
     *          {@code null}
     * @return A configured {@link EditArea.Params} instance
     * @throws NullPointerException if {@code l} is {@code null}
     */
    public EditArea.Params editParams(EditParams l)
    {
	requireNonNull(l, "l can't be null");
	final var params = new EditArea.Params(getControlContext());
	//	params.inputEventListeners = new ArrayList<>();
	//	params.inputEventListeners.add(createEditAreaInputEventHook());
	l.setEditParams(params);
	if (params.inputEventListeners != null)
	    log.trace(String.valueOf(params.inputEventListeners.size()) + " edit input listeners");
	return params;
    }

    /**
     * Functional interface for configuring a {@link ConsoleArea.Params}
     * instance before the console area is constructed.
     *
     * @param <E> The type of console items
     */
    public interface ConsoleParams<E> { void setConsoleParams(ConsoleArea.Params<E> params); }

    /**
     * Creates and configures a {@link ConsoleArea.Params} object.
     *
     * <p>
     * The supplied {@link ConsoleParams} callback is invoked to configure
     * the parameters. The control context is pre-populated.
     * </p>
     *
     * @param <E> The type of console items
     * @param l The callback to configure the parameters; must not be
     *          {@code null}
     * @return A configured {@link ConsoleArea.Params} instance
     * @throws NullPointerException if {@code l} is {@code null}
     */
    public <E> ConsoleArea.Params<E> consoleParams(ConsoleParams<E> l)
    {
	requireNonNull(l, "l can't be null");
	final ConsoleArea.Params<E> params = new ConsoleArea.Params<E>();
	params.context = getControlContext();
	l.setConsoleParams(params);
	return params;
    }

    /**
     * Functional interface for configuring a {@link TreeArea.Params} instance
     * before the tree area is constructed.
     *
     * @param <E> The type of tree items
     */
        public interface TreeParams<E> { void setTreeParams(TreeArea.Params params); }

    /**
     * Creates and configures a {@link TreeArea.Params} object.
     *
     * <p>
     * The supplied {@link TreeParams} callback is invoked to configure the
     * parameters. The control context is pre-populated.
     * </p>
     *
     * @param <E> The type of tree items
     * @param l The callback to configure the parameters; must not be
     *          {@code null}
     * @return A configured {@link TreeArea.Params} instance
     * @throws NullPointerException if {@code l} is {@code null}
     */
    public <E> TreeArea.Params treeParams(TreeParams<E> l)
    {
	requireNonNull(l, "l can't be null");
	final TreeArea.Params params = new TreeArea.Params();
	l.setTreeParams(params);
	params.context = getControlContext();
	return params;
    }

    /**
     * Returns a new {@link org.luwrain.script.Hooks} instance for interacting
     * with the LUWRAIN scripting engine from this layout.
     *
     * @return A new {@code Hooks} instance; never {@code null}
     */
    public org.luwrain.script.Hooks getHooks()
    {
	return new org.luwrain.script.Hooks();
    }

    /*
    public EditArea.InputEventListener createEditAreaInputEventHook()
    {
	return (edit, event)->edit.update((lines, hotPoint)->chainOfResponsibilityNoExc(getLuwrain(), EDIT_INPUT, new Object[]{
		    new EditAreaObj(edit, lines),
		    new InputEventObj(event)
		}));
    }
    */

    /**
     * A {@link WrappingControlContext} subclass that resolves original areas
     * to their wrapped equivalents.
     *
     * <p>
     * Every method that receives an area parameter first translates it
     * through the {@link LayoutBase#areaWrappers} map. If a wrapping exists,
     * the wrapped area is used; otherwise the original area is passed through.
     * This ensures that control operations (like announcing content changes)
     * always operate on the wrapped areas and thus benefit from exception
     * isolation.
     * </p>
     * <p>
     * The {@link #runHooks(String, HookRunner)} method delegates to
     * {@link Luwrain#runHooks(String, HookRunner)} to allow scripts to
     * intercept control operations.
     * </p>
     */
    protected final class LayoutControlContext extends WrappingControlContext
    {
	/**
	 * Creates a new layout control context wrapping the given base
	 * context.
	 *
	 * @param context The base control context to delegate to; must not
	 *                be {@code null}
	 */
	public LayoutControlContext(ControlContext context)
	{
	    super(context);
	}

	/**
	 * Notifies the base context that the wrapped area has new content.
	 *
	 * @param area The original area (will be resolved to its wrapper)
	 */
	    @Override public void onAreaNewContent(Area area)
	{
	    super.onAreaNewContent(getArea(area));
	}

	/**
	 * Notifies the base context that the wrapped area has a new name.
	 *
	 * @param area The original area (will be resolved to its wrapper)
	 */
    @Override public void onAreaNewName(Area area)
	{
	    super.onAreaNewName(getArea(area));
	}

	/**
	 * Notifies the base context that the wrapped area has a new hot point.
	 *
	 * @param area The original area (will be resolved to its wrapper)
	 */
    @Override public void onAreaNewHotPoint(Area area)
	{
super.onAreaNewHotPoint(getArea(area));
	}

	/**
	 * Returns the visible height of the wrapped area.
	 *
	 * @param area The original area (will be resolved to its wrapper)
	 * @return The visible height in lines
	 */
@Override public int getAreaVisibleHeight(Area area)
	{
	    return super.getAreaVisibleHeight(getArea(area));
	}

	/**
	 * Returns the visible width of the wrapped area.
	 *
	 * @param area The original area (will be resolved to its wrapper)
	 * @return The visible width in characters
	 */
    @Override public int getAreaVisibleWidth(Area area)
	{
	    return getAreaVisibleWidth(getArea(area));
	}

	/**
	 * Notifies the base context that the wrapped area has a new
	 * background sound.
	 *
	 * @param area The original area (will be resolved to its wrapper)
	 */
	    @Override public void onAreaNewBackgroundSound(Area area)
	{
	    super.onAreaNewBackgroundSound(getArea(area));
	}

	/**
	 * Runs the specified hook by delegating to the LUWRAIN core's hook
	 * infrastructure.
	 *
	 * <p>
	 * This allows scripts and extensions to intercept and modify control
	 * operations.
	 * </p>
	 *
	 * @param hookName The name of the hook to run; must not be
	 *                 {@code null}
	 * @param runner The hook runner that executes the hook logic; must
	 *               not be {@code null}
	 * @return {@code true} if the hook was handled, {@code false}
	 *         otherwise
	 */
	    @Override public boolean runHooks(String hookName, HookRunner runner)
	{
	    return getLuwrain().runHooks(hookName, runner);
	}

	/**
	 * Resolves an original area to its wrapped equivalent.
	 *
	 * <p>
	 * If no wrapping exists for the given area, it is returned unchanged.
	 * </p>
	 *
	 * @param area The area to resolve; must not be {@code null}
	 * @return The wrapped area if one exists, otherwise the original area
	 */
	private Area getArea(Area area)
	{
	    requireNonNull(area, "area can't be null");
	    final Area res = areaWrappers.get(area);
	    return res != null?res:area;
	}
    }
}
