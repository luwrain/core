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

package  org.luwrain.core;

import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;

import org.h2.mvstore.*;
import com.google.gson.*;

import static java.util.Objects.*;
import static java.nio.file.Files.*;

public final class Configs implements AutoCloseable
{
    static private final Logger log = LogManager.getLogger();

    private final File configsDir;
    private final Gson gson = new Gson();
    private final MVStore store;
    private final MVMap<String, String> mvMap;

    Configs(File configsDir) throws IOException
    {
	createDirectories(configsDir.toPath());
	log.trace("Opening configs session in " + configsDir.getAbsolutePath());
	this.configsDir = requireNonNull(configsDir, "configsDir can't be null");
	this.store = MVStore.open(new File(configsDir, "configs.mvdb").getAbsolutePath()); 
this.mvMap = store.openMap("main");
    }

    public synchronized <E> E load(Class<E> cl)
    {
	requireNonNull(cl, "cl can't be null");
	final String str = mvMap.get(cl.getName());
	if (str == null || str.isEmpty())
	    	    return null;
		return gson.fromJson(str, cl);
    }

    public synchronized <E> void save(E obj)
    {
	requireNonNull(obj, "obj can't be null");
	mvMap.put(obj.getClass().getName(), gson.toJson(obj));
        }

    @Override public synchronized void close()
    {
		log.trace("Closing configs session in " + configsDir.getAbsolutePath());
	store.close();
    }
}
