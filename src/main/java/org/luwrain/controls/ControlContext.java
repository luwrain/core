// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.io.File;

import org.luwrain.core.*;

public interface ControlContext extends HookContainer
{
    void say(String text);
    void say(String text, Sounds sound);
    void sayStaticStr(org.luwrain.i18n.LangStatic id);
    void sayLetter(char letter);
    void onAreaNewContent(Area area);
    void onAreaNewName(Area area);
    void onAreaNewHotPoint(Area area);
int getAreaVisibleHeight(Area area);
    int getAreaVisibleWidth(Area area);
    void popup(Popup popupObj);
    void runUiSafely(Runnable runnable);
    String staticStr(org.luwrain.i18n.LangStatic id);
    String getStaticStr(String id);
    void playSound(Sounds sound);
    UniRefInfo getUniRefInfo(String uniRef);
    void silence();
    void setEventResponse(EventResponse eventResponse);
    Clipboard getClipboard();
    org.luwrain.i18n.I18n getI18n();
    int getScreenWidth();
    int getScreenHeight();
    void executeBkg(java.util.concurrent.FutureTask task);
    void onAreaNewBackgroundSound(Area area);
    String getSpeakableText(String text, Luwrain.SpeakableTextType type);
    void message(String text, Luwrain.MessageType messageType);
}
