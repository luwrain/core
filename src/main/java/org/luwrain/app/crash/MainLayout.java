// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.crash;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;

import static java.util.Objects.*;

final class MainLayout extends LayoutBase
{
    private final App app;
    private final SimpleArea simpleArea;

    MainLayout(App app)
    {
	requireNonNull(app, "app can't be null");
	this.app = app;
	this.simpleArea = new SimpleArea(new DefaultControlContext(app.getLuwrain()), app.getStrings().appName()){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    requireNonNull(event, "event can't be null");
		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    requireNonNull(event, "event can't be null");
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    requireNonNull(query, "query can't be null");
		    if (app.onAreaQuery(this, query))
			return true;
		    return super.onAreaQuery(query);
		}
		@Override public void announceLine(int index, String line)
		{
		    requireNonNull(line, "line can't be null");
		    defaultLineAnnouncement(context, index, app.getLuwrain().getSpeakableText(line, Luwrain.SpeakableTextType.PROGRAMMING));
		}
	    };
	fillText();
    }

    private void fillText()
    {
	if (app.ex instanceof InitResultException)
	{
	    final InitResultException ex = (InitResultException)app.ex;
	    if (ex.getInitResult().getType() == InitResult.Type.EXCEPTION)
	    {
		fillException(ex.getInitResult().getException());
		return;
	    }
	}
	if (app.ex instanceof CustomMessageException)
	{
	    final CustomMessageException c = (CustomMessageException)app.ex;
	    simpleArea.update((lines)->{
	    final String[] message = c.getCustomMessage();
	    for(String s: message)
		lines.add(s);
	    lines.add("");
		});
	    return;
	}
	fillException(app.ex);
    }

    private void fillException(Throwable t)
    {
		requireNonNull(t, "t can't be null");
	    	simpleArea.update((lines)->{
	if (t instanceof java.io.FileNotFoundException && t.getMessage() != null)
	{
	    lines.add("");
	    lines.add(app.getStrings().fileNotFound() + ": " + t.getMessage());
	}

	final String[] msg = app.getStrings().intro().split("\\n");
	lines.add("");
	for(String s: msg)
	    lines.add(s);
	lines.add("");
	if (app.srcApp != null)
	    lines.add(app.getStrings().app(app.srcApp.getClass().getName()));
	if (app.srcArea != null)
	    lines.add(app.getStrings().area(app.srcArea.getClass().getName()));
	if (app.srcApp != null || app.srcArea != null)
	    lines.add("");
	lines.add(app.getStrings().stackTrace());
	final StringWriter sw = new StringWriter();
	final PrintWriter pw = new PrintWriter(sw);
	t.printStackTrace(pw);
	pw.flush();
	sw.flush();
	final String[] trace = sw.toString().split("\n", -1);
	for(String s: trace)
	    lines.add(s);
		    });
    }

    AreaLayout getLayout()
    {
    	return new AreaLayout(simpleArea);
    }
}
