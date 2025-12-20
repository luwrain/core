// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

final class CommandManager
{
    private final Map<String, Entry> commands = new HashMap<>();

    boolean add(Luwrain luwrain, Command command)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(command, "command");
	final String name = command.getName();
	if (name == null || name.trim().isEmpty())
	    return false;
	if (commands.containsKey(name))
	    return false;
	commands.put(name, new Entry(luwrain, name, command));
	return true;
    }

    boolean run(String name)
    {
	NullCheck.notEmpty(name, "name");
	if (!commands.containsKey(name))
	    return false;
	final Entry entry = commands.get(name);
	try {
	    entry.command.onCommand(entry.luwrain);
	}
	catch(Throwable e)
	{
	    entry.luwrain.crash(e);
	}
	return true;
    }

    String[] getCommandNames()
    {
	final List<String> res = new ArrayList<>();
	for(Map.Entry<String, Entry> e: commands.entrySet())
	    res.add(e.getKey());
	String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }

    void deleteByInstance(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final List<String> deleting = new ArrayList<>();
		for(Map.Entry<String, Entry> e: commands.entrySet())
		    if (e.getValue().luwrain == luwrain)
			deleting.add(e.getKey());
		for(String s: deleting)
		    commands.remove(s);
    }

    static private final class Entry 
    {
	final Luwrain luwrain;
	final String name;
	final Command command;
	Entry(Luwrain luwrain, String name, Command command)
	{
	    NullCheck.notNull(luwrain, "luwrain ");
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(command, "command");
	    this.luwrain = luwrain;
	    this.name = name;
	    this.command = command;
	}
    }
}
