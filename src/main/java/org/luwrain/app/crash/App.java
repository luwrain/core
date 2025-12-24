// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.crash;

import java.util.*;

import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.app.base.*;

import static java.util.Objects.*;

public final class App extends AppBase<Strings>
{
    final Application srcApp;
    final Area srcArea;
    final Throwable ex;
    private MainLayout mainLayout = null;

    public App(Throwable ex, Application srcApp, Area srcArea)
    {
	super(Strings.class, "luwrain.crash");
	requireNonNull(ex, "ex can't be null");
	this.ex = ex;
	this.srcApp = srcApp;
	this.srcArea = srcArea;
    }

    @Override public AreaLayout onAppInit()
    {
	this.mainLayout = new MainLayout(this);
	setAppName(getStrings().appName());
	return mainLayout.getLayout();
    }

        @Override public boolean onInputEvent(Area area, InputEvent event)
    {
	NullCheck.notNull(area, "area");
	if (super.onInputEvent(area, event))
	    return true;
	if (event.isSpecial())
	    switch(event.getSpecial())
	    {
	    case ESCAPE:
		closeApp();
		return true;
	    }
	return false;
    }
}
