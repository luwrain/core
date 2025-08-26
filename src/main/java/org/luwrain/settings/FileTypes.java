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
import lombok.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.controls.list.*;
import org.luwrain.cpanel.*;

import static java.util.Objects.*;
import static java.util.stream.Collectors.*;

final class FileTypes extends ListArea<FileTypes.Item> implements SectionArea
{
    private final ControlPanel controlPanel;

    FileTypes(ControlPanel controlPanel, ListArea.Params<Item> params)
    {
	super(params);
	this.controlPanel = controlPanel;
	setListClickHandler((area, index, item)->editItem(item));
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

    @Override public boolean saveSectionData()
    {
	return true;
    }

    private boolean editItem(Item item)
    {
	return false;
    }

    static private List<Item> loadItems(Luwrain luwrain)
    {
	final var res = new ArrayList<Item>();
	final var conf = luwrain.loadConf(org.luwrain.io.json.FileTypes.class);
	if (conf == null || conf.getTypes() == null)
	    return res;
	final var m = conf.getTypes().entrySet().stream()
	.filter(e -> e.getValue() != null && !requireNonNullElse(e.getValue().getName(), "").isEmpty())
	.collect(toMap(
		       e -> e.getValue().getName(),
		       e -> new ArrayList<>(List.of( e.getKey() )),
		       (e1, e2) -> { e1.addAll(e2); return e1; }
		       ));
	return m.entrySet().stream()
	.map(e -> new Item(e.getKey(), e.getValue()) )
	.toList();
    }

    static FileTypes create(ControlPanel controlPanel)
    {
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final var params = new ListArea.Params<Item>();
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new ListUtils.DefaultAppearance<>(params.context, Suggestions.LIST_ITEM);
	params.name = luwrain.getString("static:CpFileTypes");
	final var items = loadItems(luwrain);
	params.model = new ListModel(items);
	return new FileTypes(controlPanel, params);
    }

    @AllArgsConstructor
        static final class Item implements Comparable
    {
	final String name;
	final List<String> ext;

	@Override public String toString()
	{
	    return name;
	    	}

	@Override public int compareTo(Object o)
	{
	    if (o != null && o instanceof Item item)
	    return name.compareTo(item.name);
	    return 0;
	}
    }
}
