// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.controls.edit.*;
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
    private ScriptArea scriptArea;

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
	if (res == null)
	    return true;
	final HotKey hotKey = new HotKey();
	if (res == cmd)
	{
	    hotKey.setCommand("");
	    hotKey.setScript(null);
	    hotKey.setInputEvent(null);
	}
		if (res == scr)
	{
	    	    hotKey.setScript("");
	    hotKey.setCommand(null);
	    hotKey.setInputEvent(null);
	}
		items.add(new Item(luwrain, hotKey));
		refresh();
	return true;
    }

    private boolean onDelete()
    {
		luwrain.message("Delete");
	return true;
    }

        private boolean editItem(Item item)
    {
	final String
	editCommand = luwrain.getString("static:CpHotKeysEditCommand"),
	editHotKey = luwrain.getString("static:CpHotKeysEditHotKey"),
	editScript = luwrain.getString("static:CpHotKeysEditScript");
	if (item.script == null)
	{
	    final var res = fixedList(luwrain, luwrain.getString("static:CpHotKeysWhatEditCommand"), new String[]{ editCommand, editHotKey});
	    if (res == null)
		return true;
	} else
	{
	    	    final var res = fixedList(luwrain, luwrain.getString("static:CpHotKeysWhatEditScript"), new String[]{ editScript, editHotKey});
		    if (res == null)
			return true;
		    if (res == editScript)
				openScriptArea(item);
	}
	return false;
    }

    private void openScriptArea(Item item)
    {
	final var params = new EditArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.appearance = new DefaultEditAreaAppearance(params.context);
	scriptArea = new ScriptArea(params, item);
	controlPanel.openAdditionalSectionArea(this, scriptArea);
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
	if (controlPanel.onInputEvent(this, event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	if (controlPanel.onSystemEvent(this, event))
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

final class ScriptArea extends EditArea implements AdditionalSectionArea
{
    final Item item;
    ScriptArea(EditArea.Params params, Item item)
    {
	super(params);
	this.item = requireNonNull(item, "item can't be null");
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	if (controlPanel.onInputEvent(this, event))
	    return true;
	return super.onInputEvent(event);
    }
    
    @Override public boolean onSystemEvent(SystemEvent event)
    {
	if (controlPanel.onSystemEvent(this, event))
	    return true;
	if (event.getType() == SystemEvent.Type.REGULAR)
	    switch(event.getCode())
	    {
	    case OK:
	    controlPanel.closeAdditionalSectionArea(HotKeys.this);
		return true;
	    }
	return super.onSystemEvent(event);
    }
}

    static final class Item implements Comparable
    {
	String command, title, script;
	InputEvent event;

	Item(Luwrain luwrain, HotKey hotKey)
	{
	    this.command = hotKey.getCommand();
	    this.script = hotKey.getScript();
	    this.event = hotKey.getInputEvent();
	    if (command != null && !command.isEmpty())
	    this.title = luwrain.i18n().getCommandTitle(command); else
		this.title = "FIXME: Command not assigned";
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
	    if (event == null)
		return "";
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
