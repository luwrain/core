// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
//import org.luwrain.util.*;

import static org.luwrain.util.TextUtils.*;

final class PersonalInfo extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    //    private final Registry registry;
    private final Settings.PersonalInfo sett;

    PersonalInfo(ControlPanel controlPanel)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()), controlPanel.getCoreInterface().i18n().getStaticStr("CpPersonalInfoSection"));
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	//	this.registry = luwrain.getRegistry();
	this.sett = null;//FIXME:newreg Settings.createPersonalInfo(luwrain.getRegistry());
fillForm();
    }

    private void fillForm()
    {
	addEdit("name", luwrain.i18n().getStaticStr("CpPersonalInfoFullName"), sett.getFullName(""), null, true);
	addEdit("address", luwrain.i18n().getStaticStr("CpPersonalInfoMailAddress"), sett.getDefaultMailAddress(""), null, true);
	activateMultilineEdit(luwrain.i18n().getStaticStr("CpPersonalInfoSignature"), splitLines(sett.getSignature("")), true);
    }

    @Override public boolean saveSectionData()
    {
	final Luwrain luwrain = controlPanel.getCoreInterface();
	//	final Registry registry = luwrain.getRegistry();
	sett.setFullName(getEnteredText("name"));
	sett.setDefaultMailAddress(getEnteredText("address"));
	sett.setSignature(getMultilineEditText("\n"));
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
}
