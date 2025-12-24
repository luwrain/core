// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.jobs;

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
    final ListArea<Entry> jobsArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.jobsArea = new ListArea<Entry>(listParams(p ->{
		    		    p.name = app.getStrings().appName();
		    p.model = new ListUtils.ListModel<Entry>(app.jobs.entries);
		    p.appearance = new ListUtils.AbstractAppearance<Entry>(){
	@Override public void announceItem(Entry entry, Set<Flags> flags)
	{
	    final Sounds sound;
	    if (entry.getStatus() == Job.Status.FINISHED)
		sound = entry.isFinishedSuccessfully()?Sounds.SELECTED:Sounds.ATTENTION; else
				sound = Sounds.LIST_ITEM;
	    app.setEventResponse(DefaultEventResponse.listItem(sound, entry.getInstanceName(), null));
	}
			};

		}));
	final var jobsActions = actions(
					    action("stop", app.getStrings().actionStop(), new InputEvent(InputEvent.Special.F5), this::actStop)
					    );
	setAreaLayout(jobsArea, jobsActions);
    }

    private boolean actStop()
    {
	final Object o = jobsArea.selected();
	if (o == null || !(o instanceof Entry))
	    return false;
	final Entry e = (Entry)o;
	if (e.getStatus() == Job.Status.FINISHED)
	    return false;
	e.stop();
	app.getLuwrain().playSound(Sounds.OK);
	return true;
    }
}
