// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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
 * The main class for working with layouts in the LUWRAIN application. 
 * This class provides methods for creating and managing layouts, as well as handling input events and system events. 
 * It serves as a simplified interface for developing applications in LUWRAIN.
 */
public class LayoutBase
{
    static private final Logger log = LogManager.getLogger();

public interface ActionHandler
{
    boolean onAction();
}

    public interface PropertiesHandler
    {
	LayoutBase onProperties(Area area);
    }

    public interface ActionInfoCondition
    {
	boolean isActionInfoEnabled();
    }

    public final class ActionInfo
    {
	final String name;
	final String title;
	final InputEvent inputEvent;
	final ActionHandler handler;
	final ActionInfoCondition cond;
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
		public ActionInfo(String name, String title, InputEvent inputEvent, ActionHandler handler) { this(name, title, inputEvent, handler, null); }
	public ActionInfo(String name, String title, ActionHandler handler, ActionInfoCondition cond) { this(name, title, null, handler, cond); }
	public ActionInfo(String name, String title, ActionHandler handler) { this(name, title, null, handler, null); }
    }

    static public final class Actions
    {
	private final ActionInfo[] actions;
	public Actions(ActionInfo[] actions)
	{
	    NullCheck.notNullItems(actions, "actions");
	    this.actions = actions.clone();
	}
	public Actions()
	{
	    this(new ActionInfo[0]);
	}
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
	public boolean handle(String actionName)
	{
	    NullCheck.notEmpty(actionName, "actionName");
	    for(ActionInfo a: actions)
		if (a.name.equals(actionName))
		    return a.handler.onAction();
	    return false;
	}
	boolean onActionEvent(SystemEvent event)
	{
	    requireNonNull(event, "event can't be null");
	    for(ActionInfo a: actions)
		if (ActionEvent.isAction(event, a.name))
		    return a.handler.onAction();
	    return false;
	}
    }

    protected final AppBase app;
    protected LayoutControlContext controlContext = null;
    private final Map<Area, Area> areaWrappers = new HashMap<>();
    private final Map<Area, PropertiesHandler> propHandlers = new HashMap<>();
    private AreaLayout areaLayout = null;
    private ActionHandler closeHandler = null;
    private ActionHandler okHandler = null;

    protected LayoutBase(AppBase app)
    {
	this.app = app;
    }

    protected LayoutBase()
    {
	this(null);
    }

    public Actions actions(ActionInfo ... a)
    {
	return new Actions(a);
	    }

    public Actions actions(ActionInfo[] a1, ActionInfo ... a2)
    {
	final List<ActionInfo> res = new ArrayList<>();
	res.addAll(Arrays.asList(a2));
		res.addAll(Arrays.asList(a1));
	return new Actions(res.toArray(new ActionInfo[res.size()]));
    }

    public ActionInfo action(String name, String title, InputEvent inputEvent, ActionHandler handler)
    {
	return new ActionInfo(name, title, inputEvent, handler);
    }

    public ActionInfo action(String name, String title, ActionHandler handler)
    {
	return new ActionInfo(name, title, handler);
    }

    public ActionInfo action(String name, String title, InputEvent inputEvent, ActionHandler handler, ActionInfoCondition cond)
    {
	return new ActionInfo(name, title, inputEvent, handler, cond);
    }

    public ActionInfo action(String name, String title, ActionHandler handler, ActionInfoCondition cond)
    {
	return new ActionInfo(name, title, handler, cond);
    }

    public void setCloseHandler(ActionHandler closeHandler)
    {
	this.closeHandler = requireNonNull(closeHandler, "closeHandler can't be null");
    }

        public void setOkHandler(ActionHandler okHandler)
    {
	this.okHandler = requireNonNull(okHandler, "okHandler can't be null");
    }

        public Area getWrappingArea(Area area)
    {
	requireNonNull(area, "area can't be null");
	return getWrappingArea(area, null);
    }

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

    public void clearAreaWrappers()
    {
	areaWrappers.clear();
    }

    public void setAreaLayout(Area area, Actions actions)
    {
	requireNonNull(area, "area can't be null");
	this.areaLayout = new AreaLayout(getWrappingArea(area, actions));
    }

    public void setAreaLayout(AreaLayout.Type type, Area area1, Actions actions1, Area area2, Actions actions2)
    {
	requireNonNull(area1, "area1 can't be null");
	requireNonNull(area2, "area2 can't be null");
	this.areaLayout = new AreaLayout(type, getWrappingArea(area1, actions1), getWrappingArea(area2, actions2));
    }

    public void setAreaLayout(AreaLayout.Type type, Area area1, Actions actions1, Area area2, Actions actions2, Area area3, Actions actions3)
    {
	requireNonNull(area1, "area1 can't be null");
	requireNonNull(area2, "area2 can't be null");
		requireNonNull(area3, "area3 can't be null");
		this.areaLayout = new AreaLayout(type, getWrappingArea(area1, actions1), getWrappingArea(area2, actions2), getWrappingArea(area3, actions3));
    }

    public AreaLayout getAreaLayout()
    {
	if (this.areaLayout == null)
	    throw new IllegalStateException("No area layout, use setAreaLayout() to set it");
	return this.areaLayout;
    }

    public ControlContext getControlContext()
    {
	if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
	if (this.controlContext == null)
	    this.controlContext = new LayoutControlContext(new DefaultControlContext(app.getLuwrain()));
	return this.controlContext;
    }

    public Luwrain getLuwrain()
    {
		if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
		return app.getLuwrain();
    }

    public void setActiveArea(Area area)
    {
	requireNonNull(area, "area can't be null");
		if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
		final Area a = areaWrappers.get(area);
		app.getLuwrain().setActiveArea(a != null?a:area);
    }

    public int getAreaVisibleWidth(Area area)
    {
	requireNonNull(area, "area can't be null");
			if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
		final Area a = areaWrappers.get(area);
		return app.getLuwrain().getAreaVisibleWidth(a != null?a:area);
    }

        public int getAreaVisibleHeight(Area area)
    {
	requireNonNull(area, "area can't be null");
			if (app == null)
	    throw new IllegalStateException("No app instance, provide it with the corresponding constructor");
		final Area a = areaWrappers.get(area);
		return app.getLuwrain().getAreaVisibleHeight(a != null?a:area);
    }

    public void setPropertiesHandler(Area area, PropertiesHandler handler)
    {
	requireNonNull(area, "area can't be null");
	requireNonNull(handler, "handler can't be null");
	propHandlers.put(area, handler);
    }

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

    public interface ListParams<E> { void setListParams(ListArea.Params<E> params); }
    public <E> ListArea.Params<E> listParams(ListParams<E> l)
    {
	requireNonNull(l, "l can't be null");
	final ListArea.Params<E> params = new ListArea.Params<>();
	params.context = getControlContext();
	params.appearance = new ListUtils.DefaultAppearance<E>(getControlContext());
	l.setListParams(params);
	return params;
    }

        public interface EditParams { void setEditParams(EditArea.Params params); }
    public EditArea.Params editParams(EditParams l)
    {
	requireNonNull(l, "l can't be null");
	final EditArea.Params params = new EditArea.Params(getControlContext());
	//	params.inputEventListeners = new ArrayList<>();
	//	params.inputEventListeners.add(createEditAreaInputEventHook());
	l.setEditParams(params);
	if (params.inputEventListeners != null)
	    log.debug(String.valueOf(params.inputEventListeners.size()) + " edit input listeners");
	return params;
    }

    public interface ConsoleParams<E> { void setConsoleParams(ConsoleArea.Params<E> params); }
    public <E> ConsoleArea.Params<E> consoleParams(ConsoleParams<E> l)
    {
	NullCheck.notNull(l, "l");
	final ConsoleArea.Params<E> params = new ConsoleArea.Params<E>();
	params.context = getControlContext();
	l.setConsoleParams(params);
	return params;
    }

        public interface TreeParams<E> { void setTreeParams(TreeArea.Params params); }
    public <E> TreeArea.Params treeParams(TreeParams<E> l)
    {
	requireNonNull(l, "l can't be null");
	final TreeArea.Params params = new TreeArea.Params();
	l.setTreeParams(params);
	params.context = getControlContext();
	return params;
    }

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

    protected final class LayoutControlContext extends WrappingControlContext
    {
	public LayoutControlContext(ControlContext context)
	{
	    super(context);
	}
	    @Override public void onAreaNewContent(Area area)
	{
	    super.onAreaNewContent(getArea(area));
	}
    @Override public void onAreaNewName(Area area)
	{
	    super.onAreaNewName(getArea(area));
	}
    @Override public void onAreaNewHotPoint(Area area)
	{
super.onAreaNewHotPoint(getArea(area));
	}
@Override public int getAreaVisibleHeight(Area area)
	{
	    return super.getAreaVisibleHeight(getArea(area));
	}
    @Override public int getAreaVisibleWidth(Area area)
	{
	    return getAreaVisibleWidth(getArea(area));
	}
	    @Override public void onAreaNewBackgroundSound(Area area)
	{
	    super.onAreaNewBackgroundSound(getArea(area));
	}
	    @Override public boolean runHooks(String hookName, HookRunner runner)
	{
	    return getLuwrain().runHooks(hookName, runner);
	}
	private Area getArea(Area area)
	{
	    requireNonNull(area, "area can't be null");
	    final Area res = areaWrappers.get(area);
	    return res != null?res:area;
	}
    }
}
