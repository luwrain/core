// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.controls.list.*;
import org.luwrain.cpanel.*;
import org.luwrain.io.json.HotKey;

import static java.util.Objects.*;
import static org.luwrain.popups.Popups.*;

final class HotKeys extends ListArea<HotKeys.Item> implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    private final List<Item> items;

    private HotKeys(ControlPanel controlPanel, List<Item> items, ListArea.Params<Item> params)
    {
	super(params);
	this.controlPanel = requireNonNull(controlPanel, "controlPanel can't be empty");
	this.luwrain = controlPanel.getCoreInterface();
	this.items = items;
	setListClickHandler((area, index, item)->editItem(item));
    }

    @Override public boolean saveSectionData()
    {
	return true;
    }

    private boolean onInsert()
    {
	final String cmd = "Command", scr = "Script";
	final String res = (String)fixedList(luwrain, "New hot key", new Object[]{cmd, scr});
	if (res == cmd)
	{
	    //	    getListModel().
	}
	luwrain.message(res);
	return true;
    }

    private boolean onDelete()
    {
		luwrain.message("Delete");
	return true;
    }

        private boolean editItem(Item item)
    {
	return false;
    }


    @Override public Action[] getAreaActions()
    {
	return new Action[]{
	    new Action("insert", "Insert", new InputEvent(InputEvent.Special.INSERT)),
	    	    new Action("delete", "Delete", new InputEvent(InputEvent.Special.DELETE))
	};
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
	if (event.getType() == SystemEvent.Type.REGULAR && event.getCode() == SystemEvent.Code.ACTION)
	{
	    if (ActionEvent.isAction(event, "insert"))
		return onInsert();
	    	    if (ActionEvent.isAction(event, "delete"))
		return onDelete();
	}

	return super.onSystemEvent(event);
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
	final var items = new ArrayList<>(loadItems(luwrain));
	final var params = new ListArea.Params<Item>();
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new ListUtils.DefaultAppearance<>(params.context, Suggestions.LIST_ITEM);
	params.name = luwrain.getString("static:CpHotKeys");
	params.model = new ListModel<>(items);
	return new HotKeys(controlPanel, items, params);
    }

    static final class Item implements Comparable
    {
	String command, title, script;
	InputEvent event;

	Item(Luwrain luwrain, HotKey hotKey)
	{
	    this.command = requireNonNullElse(hotKey.getCommand(), "");
	    this.script = hotKey.getScript();
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
