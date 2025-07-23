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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
//import org.luwrain.registry.*;

//import static org.luwrain.core.NullCheck.*;
//import static org.luwrain.core.Registry.*;

import static java.util.Objects.*;

final class HotKeys extends ListArea<HotKeys.Item> implements SectionArea
{
    private final ControlPanel controlPanel;

    HotKeys(ControlPanel controlPanel, ListArea.Params<Item> params)
    {
	super(params);
	this.controlPanel = requireNonNull(controlPanel, "controlPanel can't be empty");
	setListClickHandler((area, index, item)->editItem(item));
    }

        @Override public boolean saveSectionData()
    {
	return true;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    private boolean editItem(Item item)
    {
	return false;
    }

    static private Item[] loadItems(Luwrain luwrain)
    {
	final ArrayList<Item> res = new ArrayList<>();
	/*FIXME:newreg 
	for(String d: luwrain.getRegistry().getDirectories(Settings.GLOBAL_KEYS_PATH))
	{
	    res.add(new Item(luwrain, d));
	}
	*/
final Item[] toSort = res.toArray(new Item[res.size()]);
Arrays.sort(toSort);
return toSort;
    }

    static HotKeys create(ControlPanel controlPanel)
    {
	requireNonNull(controlPanel, "controlPanel can't be null");
	final var luwrain = controlPanel.getCoreInterface();
	final var params = new ListArea.Params<Item>();
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new ListUtils.DefaultAppearance<>(params.context, Suggestions.LIST_ITEM);
	params.name = "Общие горячие клавиши";//FIXME:
	params.model = new ListUtils.FixedModel<>(loadItems(luwrain));
	return new HotKeys(controlPanel, params);
    }

        static final class Item implements Comparable
    {
	final String command, title;
	final InputEvent[] events;
	Item(Luwrain luwrain, String command)
	{
	    	    this.command = command;
		    this.events = new InputEvent[0];
	    this.title = luwrain.i18n().getCommandTitle(command);
	}

	@Override public String toString()
	{
	    if (events.length == 0)
	    return title;
	    final StringBuilder b = new StringBuilder();
	    b.append(title).append(": ");
	    for(InputEvent e: events)
		b.append(hotKeyToString(e));
	    return new String(b);
	}

	@Override public int compareTo(Object o)
	{
	    if (o != null && o instanceof Item item)
	    return command.compareTo(item.command);
	    return 0;
	}

    static private String hotKeyToString(InputEvent event)
    {
	final var b = new StringBuilder();
	if (event.withControl())
	    b.append("Ctrl+");
	if (event.withAlt())
	    b.append("Alt+");
	if (event.withShift())
	    b.append("Shift+");
	if (!event.isSpecial())
	    b.append(Character.toString(Character.toUpperCase(event.getChar()))); else
	b.append(event.getSpecial().toString());
	return new String(b);
    }
}
}
