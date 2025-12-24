// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.console;

import java.util.*;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

public final class App extends AppBase<Strings> implements MonoApp
{
    static final ArrayList<Entry> events = new ArrayList<>();
    final List<Entry> entries = new ArrayList<>();
    private ConsoleCommand[] commands = new ConsoleCommand[0];
    private MainLayout mainLayout = null;

    public App()
    {
	super(Strings.class, "luwrain.console");
    }

    @Override protected AreaLayout onAppInit()
    {
	entries.addAll(events.parallelStream().filter(e->{
		    return e.logger.startsWith("org.luwrain");
		}).toList());
	this.mainLayout = new MainLayout(this);
	this.commands = new ConsoleCommand[]{
	    new Commands.Prop(getLuwrain()),
	};
	setAppName(getStrings().appName());
	return mainLayout.getAreaLayout();
    }

    ConsoleCommand[] getCommands()
    {
	return this.commands.clone();
    }

    @Override public boolean onEscape()
    {
	closeApp();
	return true;
    }

    @Override public MonoApp.Result onMonoAppSecondInstance(Application app)
    {
	NullCheck.notNull(app, "app");
	return MonoApp.Result.BRING_FOREGROUND;
    }
}
