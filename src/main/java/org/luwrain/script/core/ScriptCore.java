/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.script.core;

import java.util.*;
import java.io.*;

import org.graalvm.polyglot.*;

import org.luwrain.core.*;
import org.luwrain.util.*;

import static org.luwrain.core.NullCheck.*;

public final class ScriptCore implements HookContainer, AutoCloseable
{
    static private final String
	LOG_COMPONENT = "script";

    private final Bindings bindings;
    private final Luwrain luwrain;
    private final List<Module> modules = new ArrayList<>();

    public ScriptCore(Luwrain luwrain, Bindings bindings)
    {
	notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.bindings = bindings;
    }

    public ScriptCore(Luwrain luwrain)
    {
	this(luwrain, null);
    }

    @Override public void close()
    {
	for(Module m: modules)
	    m.close();
    }

    public void load (Reader reader) throws IOException
    {
	notNull(reader, "reader");
	final String lineSep = System.lineSeparator();
	final StringBuilder b = new StringBuilder();
	final BufferedReader r = new BufferedReader(reader);
	String line = r.readLine();
	while (line != null)
	{
	    b.append(line).append(lineSep);
	    line = r.readLine();
	}
	final Module m = new Module(luwrain, bindings);
	m.eval(new String(b));
	modules.add(m);
    }

    public void load (File file) throws IOException
    {
	notNull(file, "file");
	final Module m = new Module(luwrain, bindings);
	m.eval(FileUtils.readTextFileSingleString(file, "UTF-8"));
	modules.add(m);
    }

    public void load (ScriptFile scriptFile) throws IOException
    {
	notNull(scriptFile, "scriptFile");
	Log.debug(LOG_COMPONENT, "loading " + scriptFile.toString());
	load(scriptFile.asFile());
    }

    @Override public boolean runHooks(String hookName, Luwrain.HookRunner runner)
    {
	notEmpty(hookName, "hookName");
	try {
	    for(Module m: modules)
		if (m.luwrainObj.hooks.containsKey(hookName))
		    for(Value v: m.luwrainObj.hooks.get(hookName))
		    {
			final Luwrain.HookResult res;
			synchronized(m.luwrainObj.syncObj) {
			    res = runner.runHook((args)->v.execute(args));
			}
			if (res == null)
			    return false;
			if (res == Luwrain.HookResult.BREAK)
			    return false;
		    }
	}
	catch(Throwable e)
	{
	    Log.error(LOG_COMPONENT, "running of the hook '" + hookName + "' failed: " + e.getClass().getName() + ": " + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public ExtensionObject[] getExtObjects()
    {
	final List<ExtensionObject> res = new ArrayList<>();
	for(Module m: modules)
	    res.addAll(m.luwrainObj.extObjs);
	return res.toArray(new ExtensionObject[res.size()]);
    }

        public Command[] getCommands()
    {
	final List<Command> res = new ArrayList<>();
	for(Module m: modules)
	    res.addAll(m.luwrainObj.commands);
	return res.toArray(new Command[res.size()]);
    }
}
