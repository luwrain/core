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
	cmd(Special.ENTER, Modifiers.CONTROL, "ok"),
	cmd(Special.ENTER, Modifiers.SHIFT, "properties"),
	cmd(Special.ESCAPE, Modifiers.CONTROL, "close"),

	cmd(Special.F2, "save"),
	cmd(Special.F3, "open"),
		cmd(Special.F10, Modifiers.SHIFT, "context-menu"),
		cmd('r', Modifiers.ALT, "run"),
	cmd('u', Modifiers.CONTROL, Modifiers.ALT, "copy-uri-area"),
        cmd('o', Modifiers.CONTROL, Modifiers.ALT, "copy-uri-hot-point"),
	cmd('u', Modifiers.CONTROL, "copy-url-area"),
        cmd('o', Modifiers.CONTROL, "copy-url-hot-point"),
	cmd('q', Modifiers.ALT, "quit"),

	//Text editing
	cmd(' ', Modifiers.CONTROL, "region-point"),
	cmd('c', Modifiers.CONTROL, "copy"),
	cmd('a', Modifiers.CONTROL, "copy-all"),
	cmd('x', Modifiers.CONTROL, "cut"),
	cmd('v', Modifiers.CONTROL, "paste"),
	cmd(Special.ALTERNATIVE_DELETE, "clear"),
	cmd(Special.DELETE, Modifiers.SHIFT, "clear-region"),

		//Basic apps
	cmd('j', Modifiers.SHIFT, Modifiers.ALT, "jobs"),
	cmd('p', Modifiers.SHIFT, Modifiers.ALT, "control-panel"),
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

    void load(OperatingSystem os)
    {
	keys.clear();
	if (args.stdGlobalKeys)
	{
	    keys.addAll(List.of(DEFAULT_KEYMAP));
	    keys.addAll(getOsDepKeys(os));
	    return ;
	}
	var conf = configs.load(Config.class);
	if (conf == null || conf.keys == null)
	{
	    conf = new Config();
	    conf.keys = new ArrayList<>(List.of(DEFAULT_KEYMAP));
	    conf.keys.addAll(getOsDepKeys(os));
	    configs.save(conf);
	}
	    keys.addAll(conf.keys);
    }

    List<HotKey> getOsDepKeys(OperatingSystem os)
    {
	if (os.getClass().getSimpleName().equals("Linux"))
	    return Arrays.asList(	cmd(Special.WINDOWS, "main-menu"));
	return Arrays.asList();
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


    static HotKey cmd(char ch, Modifiers modifier1, Modifiers modifier2, String cmdName)
    {
	requireNonNull(modifier1, "modifier1 can't be null");
		requireNonNull(modifier2, "modifier2 can't be null");
	final var key = new HotKey();
	key.setInputEvent(new InputEvent(ch, EnumSet.of(modifier1, modifier2)));
	key.setCommand(requireNonNull(cmdName, "cmdName can't be null"));
	return key;
    }





    static private final class Config
    {
	List<HotKey> keys;
    }
}
