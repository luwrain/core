// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.calendar;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

import static java.util.Objects.*;

public final class App extends AppBase<Strings> implements MonoApp
{
    private MainLayout mainLayout = null;

    public App()
    {
    super(Strings.class);
    }

@Override protected AreaLayout onAppInit()
{
    this.mainLayout = new MainLayout(this);
    setAppName(getStrings().appName());
return this.mainLayout.getAreaLayout();
}

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

        @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	return MonoApp.Result.BRING_FOREGROUND;
    }
}
