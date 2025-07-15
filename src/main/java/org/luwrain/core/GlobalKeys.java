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

package org.luwrain.core;

import java.util.*;
import org.luwrain.core.events.*;
import org.luwrain.io.json.*;

import static java.util.Objects.*;
import static org.luwrain.core.events.InputEvent.*;

final class GlobalKeys
{
    static final HotKey[] DEFAULT_KEYMAP = {
	    //Basic
    cmd('q', Modifiers.ALT, "quit"),


	cmd(Special.ESCAPE, "close"),
	cmd(Special.F2, "save"),
	cmd(Special.F3, "open"),

	//text editing
	cmd('c', Modifiers.CONTROL, "copy"),
		cmd('a', Modifiers.CONTROL, "copy-all"),
	cmd(Special.ALTERNATIVE_DELETE, "clear"),
	cmd(Special.DELETE, Modifiers.SHIFT, "clear-region")
    };

    private final Args args;
    private final List<HotKey> keys = new ArrayList<>();
    private final Configs configs;

    GlobalKeys(Args args, Configs configs)
    {
	this.args = args;
	this.configs = requireNonNull(configs, "configs");
    }

    String getCommandName(InputEvent event )
    {
	requireNonNull(event, "event can't be null");
	for(var key: keys)
	    if (key.getInputEvent().equals(event))
		return key.getCommand();
	return null;
    }

    void load()
    {
	keys.clear();
	if (args.stdGlobalKeys)
	{
	    keys.addAll(List.of(DEFAULT_KEYMAP));
	    return ;
	}
	var conf = configs.load(Config.class);
	if (conf != null && conf.keys != null)
	    keys.addAll(conf.keys);
    }

    static HotKey cmd(Special special, String cmdName)
    {
	final var key = new HotKey();
	key.setInputEvent(new InputEvent(requireNonNull(special, "special can't be null")));
	key.setCommand(requireNonNull(cmdName, "cmdName can't be null"));
	return key;
    }

    static HotKey cmd(Special special, Modifiers modifier, String cmdName)
    {
	requireNonNull(special, "special can't be null");
	requireNonNull(modifier, "modifier can't be null");
	final var key = new HotKey();
	key.setInputEvent(new InputEvent(special, EnumSet.of(modifier)));
	key.setCommand(requireNonNull(cmdName, "cmdName can't be null"));
	return key;
    }

        static HotKey cmd(char ch, Modifiers modifier, String cmdName)
    {
	requireNonNull(modifier, "modifier can't be null");
	final var key = new HotKey();
	key.setInputEvent(new InputEvent(ch, EnumSet.of(modifier)));
	key.setCommand(requireNonNull(cmdName, "cmdName can't be null"));
	return key;
    }



    static private final class Config
    {
	List<HotKey> keys;
    }
}
