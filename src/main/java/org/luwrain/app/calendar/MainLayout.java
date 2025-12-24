// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.calendar;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.core.JobsManager.Entry;

final class MainLayout extends LayoutBase
{
    private final App app;
    final CalendarArea calendarArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.calendarArea = new CalendarArea(getControlContext(), Calendar.getInstance());
	setAreaLayout(calendarArea, null);
    }

}
