// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.io.File;

import org.luwrain.core.*;

public class WrappingControlContext implements ControlContext
{
    protected ControlContext context;

    public WrappingControlContext(ControlContext context)
    {
	NullCheck.notNull(context, "context");
	this.context = context;
    }

    @Override public void say(String text)
    {
	context.say(text);
    }

    @Override public void say(String text, Sounds sound)
    {
	context.say(text, sound);
    }

    @Override public void sayStaticStr(org.luwrain.i18n.LangStatic id)
    {
	context.sayStaticStr(id);
    }

    @Override public void sayLetter(char letter)
    {
	context.sayLetter(letter);
    }

    @Override public void onAreaNewName(Area area)
    {
	context.onAreaNewName(area);
    }

    @Override public void onAreaNewContent(Area area)
    {
	context.onAreaNewContent(area);
    }

    @Override public void onAreaNewHotPoint(Area area)
    {
	context.onAreaNewHotPoint(area);
    }

    @Override public int getAreaVisibleWidth(Area area)
    {
	return context.getAreaVisibleWidth(area);
    }

    @Override public int getAreaVisibleHeight(Area area)
    {
	return context.getAreaVisibleHeight(area);
    }

    @Override public void popup(Popup popupObj)
    {
	context.popup(popupObj);
    }

    @Override public String staticStr(org.luwrain.i18n.LangStatic id)
    {
	return context.staticStr(id);
    }

    @Override public String getStaticStr(String id)
    {
	return context.getStaticStr(id);
    }

    @Override public void playSound(Sounds sound)
    {
	context.playSound(sound);
    }

    @Override public UniRefInfo getUniRefInfo(String uniRef)
    {
	return context.getUniRefInfo(uniRef);
    }

    @Override public void silence()
    {
	context.silence();
    }

    @Override public void setEventResponse(EventResponse eventResponse)
    {
	context.setEventResponse(eventResponse);
    }

    @Override public Clipboard getClipboard()
    {
	return context.getClipboard();
    }

    @Override public org.luwrain.i18n.I18n getI18n()
    {
	return context.getI18n();
    }

    @Override public int getScreenWidth()
    {
	return context.getScreenWidth();
    }

    @Override public int getScreenHeight()
    {
	return context.getScreenHeight();
    }

    @Override public void executeBkg(java.util.concurrent.FutureTask task)
    {
	context.executeBkg(task);
    }

    @Override public void onAreaNewBackgroundSound(Area area)
    {
	context.onAreaNewBackgroundSound(area);
    }

    @Override public     String getSpeakableText(String text, Luwrain.SpeakableTextType type)
    {
	return context.getSpeakableText(text, type);
    }

    @Override public     boolean runHooks(String hookName, HookRunner runner)
    {
	return context.runHooks(hookName, runner);
    }

    @Override public void message(String text, Luwrain.MessageType messageType)
    {
	context.message(text, messageType);
    }

    @Override public void runUiSafely(Runnable runnable)
    {
	context.runUiSafely(runnable);
    }
}
