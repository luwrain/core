// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.controls.list.*;
import org.luwrain.cpanel.*;
import org.luwrain.io.json.HotKey;

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

    static private List<Item> loadItems(Luwrain luwrain)
    {
	final var conf = luwrain.loadConf(org.luwrain.io.json.HotKeys.class);
	if (conf == null || conf.getHotKeys() == null)
	    return new ArrayList<>();
	return conf.getHotKeys().stream()
	.map( e -> new Item(luwrain, e) )
	    .toList();
    }

    static HotKeys create(ControlPanel controlPanel)
    {
	requireNonNull(controlPanel, "controlPanel can't be null");
	final var luwrain = controlPanel.getCoreInterface();
	final var params = new ListArea.Params<Item>();
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new ListUtils.DefaultAppearance<>(params.context, Suggestions.LIST_ITEM);
	params.name = luwrain.getString("static:CpHotKeys");
	params.model = new ListModel<>(loadItems(luwrain));
	return new HotKeys(controlPanel, params);
    }

    static final class Item implements Comparable
    {
	final String command, title;
	final InputEvent event;

	Item(Luwrain luwrain, HotKey hotKey)
	{
	    this.command = requireNonNullElse(hotKey.getCommand(), "");
	    this.event = hotKey.getInputEvent();
	    this.title = luwrain.i18n().getCommandTitle(command);
	}

	@Override public String toString()
	{
	    final var b = new StringBuilder();
	    b.append(title).append(": ");
		b.append(hotKeyToString(event));
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
