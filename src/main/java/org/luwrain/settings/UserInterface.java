/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.io.json.*;

import static java.util.Objects.*;

final class UserInterface extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;

    UserInterface(ControlPanel controlPanel)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()),
	      controlPanel.getCoreInterface().i18n().getStaticStr("CpUiGeneral"));
	//	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = requireNonNull(controlPanel, "controlPanel can't be null");
	this.luwrain = controlPanel.getCoreInterface();
	//	this.sett = null;//FIXME:newreg Settings.createUserInterface(luwrain.getRegistry());
	fillForm();
    }

    private void fillForm()
    {
	final var conf = requireNonNullElse(luwrain.loadConf(CommonSettings.class), new CommonSettings());
	addEdit("desktop-title", luwrain.i18n().getStaticStr("CpUiDesktopTitle"), requireNonNullElse(conf.getDesktopTitle(), ""));
	addEdit("window-title", luwrain.i18n().getStaticStr("CpUiWindowTitle"), requireNonNullElse(conf.getWindowTitle(), ""));
	addEdit("desktop-escape-command", luwrain.i18n().getStaticStr("CpUiDesktopEscapeCommand"), requireNonNullElse(conf.getDesktopEscapeCommand(), ""));
    }

    @Override public boolean saveSectionData()
    {
	luwrain.updateConf(CommonSettings.class, conf -> {
	conf.setDesktopTitle(getEnteredText("desktop-title"));
	conf.setWindowTitle(getEnteredText("window-title"));
	conf.setDesktopEscapeCommand(getEnteredText("desktop-escape-command"));
	    });
	return true;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    static UserInterface create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	return new UserInterface(controlPanel);
    }
}
