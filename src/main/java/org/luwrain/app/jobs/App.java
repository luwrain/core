// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.jobs;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

import static java.util.Objects.*;

public final class App extends AppBase<Strings> implements MonoApp
{
    final JobsManager jobs;
    private MainLayout mainLayout = null;

    public App(JobsManager jobs)
    {
    super(Strings.class);
    this.jobs = requireNonNull(jobs, "jobs can't be null");
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
