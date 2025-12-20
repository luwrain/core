// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.nio.file.*;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.*;
import org.luwrain.core.listening.*;

import static java.util.Objects.*;

abstract class EventDispatching extends Areas
{
    protected final GlobalKeys globalKeys;
    protected Listening listening = null;
    private AtomicBoolean idleEventCancelling = null;

    protected EventDispatching(Config conf)
    {
	super(conf);
	this.globalKeys = new GlobalKeys(args, configs);
    }

    abstract protected void onBeforeEventProcessing();
    abstract protected void onAltX();

    @Override protected void onEvent(Event event)
    {
	try {
	    if (event instanceof RunnableEvent)
	    {
		unsafeAreaOperation(()->((RunnableEvent)event).runnable.run());
		return;
	    }
	    if (event instanceof CallableEvent)
	    {
		final CallableEvent callableEvent = (CallableEvent)event;
		unsafeAreaOperation(()->{
			try {
			    callableEvent.setResult(callableEvent.callable.call());
			}
			catch(Throwable e)
			{
			    throw new RuntimeException(e);
			}
		    });
		return;
	    }
	    if (event instanceof InputEvent)
	    {
onInputEvent(Keyboard.translate((InputEvent)event));
return;
	    }
	    if (event instanceof SystemEvent)
	    {
		final SystemEvent systemEvent = (SystemEvent)event;
		if (systemEvent.getType() == null)
		    return;
		switch(systemEvent.getType())
		{
		case REGULAR:
		    onSystemEvent(systemEvent);
		    return;
		case BROADCAST:
		    onBroadcastSystemEvent(systemEvent);
		    return;
		default:
		    return;
		}
	    }
	    return;
	}
	catch (Throwable e)
	{
	    Log.error(LOG_COMPONENT, "an exception of class " + e.getClass().getName() + " has been thrown while processing of event of class " + event.getClass().getName() + "::" + e.getMessage());
	    e.printStackTrace();
	    return;
	}
    }

    @Override public void announce(StopCondition stopCondition)
    {
	requireNonNull(stopCondition, "stopCondition can't be null");
	if (this.announcement != null && stopCondition.continueEventLoop() && listening == null)
	    switch(this.announcement)
	    {
	    case APP:
		announceActiveApp();
		break;
	    case AREA:
		announceActiveArea();
		break;
	    }
	this.announcement = null;
    }

    private void onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	onBeforeEventProcessing();
	if (systemHotKey(event))
	    return;
	final Area activeArea = tiles.getActiveArea();
	if (activeArea == null)
	{
	    noAppsMessage();
	    return;
	}
	unsafeAreaOperation(()->{
		final Action[] actions = activeArea.getAreaActions();
		if (actions != null)
		    for(Action a: actions)
		    {
			final InputEvent actionEvent = a.inputEvent;
			if (actionEvent == null || !actionEvent.equals(event))
			    continue;
			if (activeArea.onSystemEvent(new ActionEvent(a)))
			    return;
			break;
		    }
		if (!activeArea.onInputEvent(event))
		    soundManager.playIcon(Sounds.INACCESSIBLE);
		installIdleEvent();
	    });
    }

    private void installIdleEvent()
    {
	final AtomicBoolean cancelling = new AtomicBoolean(false);
	synchronized(this){
	    if (this.idleEventCancelling != null)
		this.idleEventCancelling.set(true);
	    this.idleEventCancelling = cancelling;
	};
	new Thread(()->{
		try {
		    Thread.sleep(1000);
		}
		catch(InterruptedException e)
		{
		    return;
		}
		if (cancelling.get())
		    return;
		enqueueEvent(new SystemEvent(SystemEvent.Code.IDLE));
	}).start();
    }

    private boolean systemHotKey(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	final String commandName = globalKeys.getCommandName(event);
	if (commandName != null)
	{
	    if (!commands.run(commandName))
		message(i18n.getStaticStr("NoCommand"), Luwrain.MessageType.ERROR);
	    return true;
	}
	if (event.isSpecial())
	{
	    final InputEvent.Special code = event.getSpecial();
	    if (code == InputEvent.Special.CONTROL)
	    {
		speech.silence();
		soundManager.cancelIcon();
		soundManager.stopStartingMode();
		return true;
	    }
	    if (code == InputEvent.Special.SHIFT ||
		code == InputEvent.Special.CONTROL ||
		code == InputEvent.Special.LEFT_ALT ||
		code == InputEvent.Special.RIGHT_ALT)
		return true;
	}
	if (!event.isSpecial() &&
	    InputEvent.getKeyboardLayout().onSameButton(event.getChar(), 'x') &&
	    event.withAltOnly())
	{
	    onAltX();
	    return true;
	}
	return false;
    }

    private void onSystemEvent(SystemEvent event)
    {
		final Area activeArea = tiles.getActiveArea();
		if (activeArea == null)
		{
			    noAppsMessage();
			    return;
		}
	final AtomicBoolean processed = new AtomicBoolean();
	unsafeAreaOperation(()->processed.set(activeArea.onSystemEvent(event)));
	if (processed.get())
	    return;
	    if (event.getCode() != SystemEvent.Code.IDLE)
		soundManager.playIcon(Sounds.INACCESSIBLE);
    }

    private void onBroadcastSystemEvent(SystemEvent event)
    {
	apps.sendBroadcastEvent(event);
    }

    private void announceActiveApp()
    {
	final Application app = apps.getActiveApp();
	if (app == null)
	{
	    noAppsMessage();
	    return;
	}
	final var name = new AtomicReference<String>();
	final var layout = new AtomicReference<AreaLayout>();
	unsafeAreaOperation(()->{
		name.set(app.getAppName());
		layout.set(app.getAreaLayout());
	    });
	if (name.get() == null || name.get().isEmpty())
	    name.set(app.getClass().getName());
Sounds sound = Sounds.AREA_LAYOUT;
if (layout.get() != null)
    switch(layout.get().layoutType)
    {
    case LEFT_RIGHT:
    case TOP_BOTTOM:
	sound = Sounds.AREA_LAYOUT_DOUBLE;
	break;
    case LEFT_RIGHT_BOTTOM:
    case LEFT_TOP_BOTTOM:
	sound = Sounds.AREA_LAYOUT_TRIPLE;
	break;
    }
	soundManager.playIcon(sound);
	speech.speak(name.get(), 0, 0);
    }

    void announceActiveArea()
    {
	final Area activeArea = tiles.getActiveArea();
	if (activeArea == null)
	{
	    noAppsMessage();
	    return;
	}
	final AtomicReference<Object> res = new AtomicReference<>();
	unsafeAreaOperation(()->res.set(Boolean.valueOf(activeArea.onSystemEvent(new SystemEvent(SystemEvent.Code.INTRODUCE)))));
	if (res.get() != null && ((Boolean)res.get()).booleanValue())
	    return;
	speech.silence();
	soundManager.playIcon(activeArea instanceof Popup?Sounds.POPUP:Sounds.INTRO_REGULAR);
	unsafeAreaOperation(()->{
		final String value = activeArea.getAreaName();
		if (value != null)
		    res.set(value);
	    });
	final String name;
	if (res != null)
	{
	    final String value = res.get().toString();
	    if (value != null && !value.trim().isEmpty())
		name = value; else
		name = activeArea.getClass().getName();
	} else
	    name = activeArea.getClass().getName();
	speech.speak(name, 0, 0);
    }

    static class RunnableEvent extends Event
    {
	final Runnable runnable;
	RunnableEvent(Runnable runnable)
	{
	    NullCheck.notNull(runnable, "runnable");
	    this.runnable = runnable;
	}
    }

    static class CallableEvent extends Event
    {
	final Callable callable;
	private Object result = null;
	CallableEvent(Callable callable)
	{
	    NullCheck.notNull(callable, "callable");
	    this.callable = callable;
	}
	void setResult(Object result)
	{
	    this.result = result;
	}
	Object getResult()
	{
	    return result;
	}
    }
}
