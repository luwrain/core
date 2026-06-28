// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.io.File;
import org.luwrain.core.*;
import static java.util.Objects.*;

public class DefaultControlContext implements ControlContext
{
    protected final Luwrain luwrain;

    public DefaultControlContext(Luwrain luwrain)
    {
	this.luwrain = luwrain;
    }

    @Override public void say(String text)
    {
	luwrain.speak(text);
    }

    @Override public void say(String text, Sounds sound)
    {
	requireNonNull(text, "text can't be null");
	requireNonNull(sound, "sound can't be null");
	luwrain.speak(text, sound);
    }

    @Override public void sayStaticStr(org.luwrain.i18n.LangStatic id)
    {
	requireNonNull(id, "id can't be null");
	say(staticStr(id));
    }

    @Override public void sayLetter(char letter)
    {
	luwrain.speakLetter(letter);
    }

    @Override public void onAreaNewName(Area area)
    {
	luwrain.onAreaNewName(area);
    }

    @Override public void onAreaNewContent(Area area)
    {
	luwrain.onAreaNewContent(area);
    }

    @Override public void onAreaNewHotPoint(Area area)
    {
	luwrain.onAreaNewHotPoint(area);
    }

    @Override public int getAreaVisibleWidth(Area area)
    {
	return luwrain.getAreaVisibleWidth(area);
    }

    @Override public int getAreaVisibleHeight(Area area)
    {
	return luwrain.getAreaVisibleHeight(area);
    }

    @Override public void popup(Popup popupObj)
    {
	luwrain.popup(popupObj);
    }

    @Override public String staticStr(org.luwrain.i18n.LangStatic id)
    {
	requireNonNull(id, "id can't be null");
	return luwrain.i18n().staticStr(id);
    }

    @Override public String getStaticStr(String id)
    {
	requireNonNull(id, "id can't be null");
	return luwrain.i18n().getStaticStr(id);
    }

    @Override public void playSound(Sounds sound)
    {
	requireNonNull(sound, "sound can't be null");
	luwrain.playSound(sound);
    }

    @Override public UniRefInfo getUniRefInfo(String uniRef)
    {
	return luwrain.getUniRefInfo(uniRef);
    }

    @Override public void silence()
    {
	luwrain.silence();
    }

    @Override public void setEventResponse(EventResponse eventResponse)
    {
	requireNonNull(eventResponse, "eventResponse can't be null");
	luwrain.setEventResponse(eventResponse);
    }

    @Override public Clipboard getClipboard()
    {
	return luwrain.getClipboard();
    }

    @Override public org.luwrain.i18n.I18n getI18n()
    {
	return luwrain.i18n();
    }

    @Override public int getScreenWidth()
    {
	return luwrain.getScreenWidth();
    }

    @Override public int getScreenHeight()
    {
	return luwrain.getScreenHeight();
    }

    @Override public void executeBkg(java.util.concurrent.FutureTask task)
    {
	luwrain.executeBkg(task);
    }

    @Override public void onAreaNewBackgroundSound(Area area)
    {
	requireNonNull(area, "area can't be null");
	luwrain.onAreaNewBackgroundSound(area);
    }

    @Override public     String getSpeakableText(String text, Luwrain.SpeakableTextType type)
    {
	requireNonNull(text, "text can't be null");
	requireNonNull(type, "type can't be null");
	return luwrain.getSpeakableText(text, type);
    }

    @Override public     boolean runHooks(String hookName, Luwrain.HookRunner runner)
    {
	NullCheck.notEmpty(hookName, "hookName");
	return luwrain.runHooks(hookName, runner);
    }

    @Override public void message(String text, Luwrain.MessageType messageType)
    {
	requireNonNull(text, "text can't be null");
	requireNonNull(messageType, "messageType can't be null");
	luwrain.message(text, messageType);
    }

    @Override public void runUiSafely(Runnable runnable)
    {
	requireNonNull(runnable, "runnable can't be null");
	luwrain.runUiSafely(runnable);
    }
}
