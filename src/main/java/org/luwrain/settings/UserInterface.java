/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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
static private final String
    HINTS_SOUNDS = "hints-sounds",
    HINTS_TEXT = "hints-text";

    private final ControlPanel controlPanel;

    UserInterface(ControlPanel controlPanel)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()),
	      controlPanel.getCoreInterface().i18n().getStaticStr("CpUiGeneral"));
	this.controlPanel = requireNonNull(controlPanel, "controlPanel can't be null");
	fillForm();
    }

    private void fillForm()
    {
	final var luwrain = controlPanel.getCoreInterface();
	final var conf = requireNonNullElse(luwrain.loadConf(CommonSettings.class), new CommonSettings());
	addCheckbox(HINTS_SOUNDS, luwrain.getString("STATIC:CpUiHintsSounds"), conf.isHintsSounds());
		addCheckbox(HINTS_TEXT, luwrain.getString("STATIC:CpUiHintsText"), conf.isHintsText());
	addEdit("desktop-title", luwrain.i18n().getStaticStr("CpUiDesktopTitle"), requireNonNullElse(conf.getDesktopTitle(), ""));
	addEdit("window-title", luwrain.getString("STATIC:CpUiWindowTitle"), requireNonNullElse(conf.getWindowTitle(), ""));
	addEdit("desktop-escape-command", luwrain.getString("STATIC:CpUiDesktopEscapeCommand"), requireNonNullElse(conf.getDesktopEscapeCommand(), ""));
    }

    @Override public boolean saveSectionData()
    {
		final var luwrain = controlPanel.getCoreInterface();
	luwrain.updateConf(CommonSettings.class, conf -> {
		conf.setHintsSounds(getCheckboxState(HINTS_SOUNDS));
				conf.setHintsText(getCheckboxState(HINTS_TEXT));
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
