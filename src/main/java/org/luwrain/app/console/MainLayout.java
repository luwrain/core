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
import org.luwrain.controls.ConsoleUtils.*;

import static org.luwrain.core.DefaultEventResponse.*;

final class MainLayout extends LayoutBase implements ConsoleArea.InputHandler
{
    static private final org.apache.logging.log4j.Logger LOG = LogManager.getLogger();

    private final App app;
    private final ConsoleArea<Entry> consoleArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.consoleArea = new ConsoleArea<Entry>(consoleParams(params -> {
	params.name = "LUWRAIN";
	params.model = new ListModel<Entry>(app.entries);
	params.appearance = new Appearance();
	params.inputHandler = this;
	params.inputPos = ConsoleArea.InputPos.BOTTOM;
	params.inputPrefix = "LUWRAIN>";
		})){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onInputEvent(this, event))
			return true;
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (app.onSystemEvent(this, event))
			return true;
		    return super.onSystemEvent(event);
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    if (app.onAreaQuery(this, query))
			return true;
		    return super.onAreaQuery(query);
		}
	    };
	setAreaLayout(consoleArea, null);
    }

    @Override public Result onConsoleInput(ConsoleArea area, String text)
    {
	if (text.trim().isEmpty())
	    return Result.REJECTED;
	for(ConsoleCommand c: app.getCommands())
	    if (c.onCommand(text, app))
	    {
		consoleArea.refresh();
		app.getLuwrain().playSound(Sounds.OK);
		return Result.CLEAR_INPUT;
	    }
	LOG.trace("Unknown command: " + Utils.firstWord(text));
	consoleArea.refresh();
	app.getLuwrain().playSound(Sounds.ERROR);
	return Result.CLEAR_INPUT;
    }

    private final class Appearance implements ConsoleArea.Appearance<Entry>
    {
	@Override public void announceItem(Entry entry)
	{
	    if (entry.ex != null)
		app.setEventResponse(text(Sounds.ATTENTION, app.getLuwrain().getSpeakableText(entry.message + ": " + entry.ex.getMessage() + " (" + entry.ex.getClass().getSimpleName() + ")", Luwrain.SpeakableTextType.PROGRAMMING))); else
	    app.setEventResponse(text(Sounds.LIST_ITEM, app.getLuwrain().getSpeakableText(entry.message, Luwrain.SpeakableTextType.PROGRAMMING)));
	}
	@Override public String getTextAppearance(Entry entry)
	{
	    if (entry.ex != null)
		return entry.message + ": " + entry.ex.getMessage() + " (" + entry.ex.getClass().getSimpleName() + ")"; else
		return entry.message;
	}
    }
}
