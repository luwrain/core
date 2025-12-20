// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package  org.luwrain.core;

//https://javadoc.io/static/com.h2database/h2-mvstore/1.4.193/org/h2/mvstore/MVMap.html

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

    public synchronized <C> void update(Class<C> configClass, ConfigUpdate<C> func)
{
    requireNonNull(configClass, "configClass can't be null");
    requireNonNull(func, "func can't be null");
C conf = load(configClass);
try {
if (conf == null)
    conf = configClass.newInstance();
}
catch(InstantiationException | IllegalAccessException ex)
{
    throw new RuntimeException(ex);
}
    func.update(conf);
    save(conf);
}

    @Override public synchronized void close()
    {
		log.trace("Closing configs session in " + configsDir.getAbsolutePath());
	store.close();
    }
}
