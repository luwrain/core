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
import java.util.jar.*;
import java.io.*;

import org.apache.logging.log4j.*;

import static java.util.Objects.*;
//import static org.luwrain.core.Base.*;
import static org.luwrain.core.NullCheck.*;

public final class ExtensionsManager implements AutoCloseable
{
    static private final Logger log = LogManager.getLogger();

        static final class Entry
    {
	final Extension ext;
	final Luwrain luwrain;
	final String id;
	final ExtensionObject[] extObjects;
	Entry(Extension ext, Luwrain luwrain)
	{
	    this.ext = requireNonNull(ext, "ext can't be null");
	    this.luwrain = requireNonNull(luwrain, "luwrain can't be null");
	    this.id = java.util.UUID.randomUUID().toString();
	    this.extObjects = ext.getExtObjects(luwrain);
	}
    }

    private final Base base;
    private final InterfaceManager interfaces;
    List<Entry> extensions = new ArrayList<>();

    ExtensionsManager(Base base, InterfaceManager interfaces)
    {
	this.base = requireNonNull(base, "base can't be null");
	this.interfaces = requireNonNull(interfaces, "interfaces can't be null");
    }

    void load(InterfaceRequest interfaceRequest, ClassLoader classLoader)
    {
	notNull(interfaceRequest, "interfaceRequest");
	notNull(classLoader, "classLoader");

	for(final Extension ext: ServiceLoader.load(Extension.class))
	{
		    final Luwrain iface = interfaceRequest.getInterfaceObj(ext);
	    final String message;
	    try {
		log.info("Initializing the " + ext.getClass().getName() + " extension");
		message = ext.init(iface);
	    }
	    catch (Throwable ex)
	    {
		log.error("Loading of extension " + ext.getClass().getName() + " failed on extension init", ex);
		interfaces.release(iface);
		continue;
	    }
	    if (message != null)
	    {
		log.error("Loading of extension " + ext.getClass().getName() + " failed. Message: " + message);
		interfaces.release(iface);
		continue;
	    }
	    extensions.add(new Entry(ext, iface));
	}

	final String[] extensionsList = getExtensionsList(classLoader);
	if (extensionsList == null || extensionsList.length == 0)
	    return;
	for(String s: extensionsList)
	{
	    if (s == null || s.trim().isEmpty())
		continue;
	    log.debug("loading " + s);
	    final Object o;
	    try {
		o = Class.forName(s, true, classLoader).getDeclaredConstructor().newInstance();
	    }
	    catch (Throwable e)
	    {
		log.error("Loading of extension " + s + " failed, e");
		continue;
	    }
	    if (!(o instanceof Extension))
	    {
		log.error("loading of extension " + s + " failed: this object isn't an instance of org.luwrain.core.Extension");
		continue;
	    }
	    final Extension ext = (Extension)o;
	    final Luwrain iface = interfaceRequest.getInterfaceObj(ext);
	    final String message;
	    try {
		message = ext.init(iface);
	    }
	    catch (Throwable ex)
	    {
		log.error("Loading of extension " + s + " failed on extension init", ex);
		interfaces.release(iface);
		continue;
	    }
	    if (message != null)
	    {
		log.error("Loading of extension " + s + " failed: " + message);
		interfaces.release(iface);
		continue;
	    }
	    extensions.add(new Entry(ext, iface));
	}
    }

    @Override public void close()
    {
	for(Entry e: extensions)
	{
	    try {
		e.ext.close();
	    }
	    catch (Throwable ex)
	    {
		log.error(ex);
	    }
	    interfaces.release(e.luwrain);
	}
	extensions = null;
    }

    public <E extends ExtensionObject> List<E> getLoadedExtObjects(Class<E> c)
    {
	final var res = new ArrayList<E>();
	for(final var e: extensions)
	    for(final var o: e.extObjects)
		if (c.isInstance(o))
		    res.add((E)o);
	return res;
    }

    //From any thread
    public boolean runHooks(String hookName, Luwrain.HookRunner runner)
    {
	notEmpty(hookName, "hookName");
	notNull(runner, "runner");
	for(Entry e: extensions)
	    if (e.ext instanceof HookContainer && !((HookContainer)e.ext).runHooks(hookName, runner))
		return false;
	return true;
    }

    private String[] getExtensionsListFromManifest(ClassLoader classLoader)
    {
	NullCheck.notNull(classLoader, "classLoader");
	final List<String> res = new ArrayList<>();
	try {
	    Enumeration<java.net.URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
	    while (resources.hasMoreElements())
	    {                                                                                                         
		try {
		    Manifest manifest = new Manifest(resources.nextElement().openStream());
		    Attributes attr = manifest.getAttributes("org/luwrain");
		    if (attr == null)
			continue;
		    final String value = attr.getValue("Extensions");
		    if (value != null)
			res.add(value);
		}
		catch (IOException e)
		{                                                                                                                 
		    e.printStackTrace();
		}
	    }
	}
	catch (IOException ee)
	{
	    ee.printStackTrace();
	}
	return res.toArray(new String[res.size()]);
    }

    private String[] getExtensionsList(ClassLoader classLoader)
    {
	requireNonNull(classLoader, "classLoader can't be null");
	/*
	final String[] cmdlineExtList = cmdLine.getArgs(EXTENSIONS_LIST_PREFIX);
	if(cmdlineExtList.length > 0)
	{
	    for(String s: cmdlineExtList)
		return s.split(":",-1);
	    return new String[0];
	}
	*/
	return getExtensionsListFromManifest(classLoader);
    }

    List<ScriptFile> getScriptFiles(String componentName)
    {
	notEmpty(componentName, "componentName");
	final String dataDir = base.conf.getDataDir().getAbsolutePath();

	//Common JavaScript extensions
	final List<ScriptFile> res = new ArrayList<>();
	final File jsDir = base.conf.getJsDir();
	if (jsDir.exists() && jsDir.isDirectory())
	{
	    final File[] files = jsDir.listFiles();
	    if (files != null)
		for(File f: files)
		{
		    if (f == null || !f.exists() || f.isDirectory())
			continue;
		    if (!f.getName().toUpperCase().endsWith(".JS"))
			continue;
		    final String name = f.getName();
		    final int pos = name.indexOf("-");
		    if (pos < 1 || pos >= name.length() - 4 || !name.substring(0, pos).toUpperCase().equals(componentName.toUpperCase()))
			continue;
		    res.add(new ScriptFile(componentName, f.getAbsolutePath(), dataDir));
		}
	}

	//JavaScript extensions from packs
	final File[] packs = base.getInstalledPacksDirs();
	for(File pack: packs)
	{
	    final File packDataDir = new File(pack, "data");
	    if (packDataDir.exists() && !packDataDir.isDirectory())
	    {
		log.warn("The pack contains '" + packDataDir.getAbsolutePath() + "' exists and it isn't a directory");
		continue;
	    }
	    if (!packDataDir.exists() && !packDataDir.mkdir())
	    {
		log.error("Unable to create '" + packDataDir.getAbsolutePath() + "', skipping the pack");
		continue;
	    }
	    final File jsExtDir = new File(pack, "js");
	    if (!jsExtDir.exists() || !jsExtDir.isDirectory())
		continue;
	    final File[] files = jsExtDir.listFiles();
	    if (files == null)
		continue;
	    for(File f: files)
	    {
		if (f == null || !f.exists() || f.isDirectory())
		    continue;
		if (!f.getName().toUpperCase().endsWith(".JS"))
		    continue;
		final String name = f.getName();
		final int pos = name.indexOf("-");
		if (pos < 1 || pos >= name.length() - 4 || !name.substring(0, pos).toUpperCase().equals(componentName.toUpperCase()))
		    continue;
		res.add(new ScriptFile(componentName, f.getAbsolutePath(), packDataDir.getAbsolutePath()));
	    }
	}
	return res;
    }

    interface InterfaceRequest 
    {
	Luwrain getInterfaceObj(Extension ext);
    }

}
