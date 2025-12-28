// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import org.apache.logging.log4j.*;

import static java.util.Objects.*;
import static org.luwrain.core.NullCheck.*;

final class ObjRegistry
{
    static private final Logger log = LogManager.getLogger();
    static private final class Entry<E> 
    {
	final Extension ext;
	final String name;
	final E obj;
	Entry(Extension ext, String name, E obj)
	{
	    notEmpty(name, "name");
	    notNull(obj, "obj");
	    this.ext = ext;
	    this.name = name;
	    this.obj = obj;
	}
    }

    private Map<String, Entry<Shortcut>> shortcuts = new HashMap<>();
    private Map<String, Entry<Worker>> workers = new HashMap<>();
        private Map<String, Entry<PropertiesProvider>> propsProviders = new HashMap<>();

    boolean add(Extension ext, ExtensionObject obj)
    {
	//ext can be null
	notNull(obj, "obj");
	final String name = obj.getExtObjName();
	if (name == null || name.trim().isEmpty())
	    return false;
	boolean res = false;

	if (obj instanceof Shortcut)
	{
	    final Shortcut shortcut = (Shortcut)obj;
	    if (!shortcuts.containsKey(name))
	    {
		shortcuts.put(name, new Entry<>(ext, name, shortcut));
		res = true;
	    }
	}

								if (obj instanceof Worker)
	{
	    final Worker worker = (Worker)obj;
	    if (!workers.containsKey(name))
	    {
		workers.put(name, new Entry<>(ext, name, worker));
		res = true;
	    }
	}

																if (obj instanceof PropertiesProvider)
	{
	    final PropertiesProvider provider = (PropertiesProvider)obj;
	    if (!propsProviders.containsKey(name))
	    {
		propsProviders.put(name, new Entry<>(ext, name, provider));
		res = true;
	    }
	}

																if (!res)
	    log.warn("Failed to add an extension object of class " + obj.getClass().getName() + " with name \'" + name + "\'");
	return res;
    }

    void deleteByExt(Extension ext)
    {
	notNull(ext, "ext");
	removeEntriesByExt(shortcuts, ext);
	removeEntriesByExt(workers, ext);
    }

    Shortcut getShortcut(String name)
    {
	NullCheck.notEmpty(name, "name");
	if (!shortcuts.containsKey(name))
	    return null;
	return shortcuts.get(name).obj;
    }

    String[] getShortcutNames()
    {
	final List<String> res = new ArrayList<>();
	for(Map.Entry<String, Entry<Shortcut>> e: shortcuts.entrySet())
	    res.add(e.getKey());
	final String[] str = res.toArray(new String[res.size()]);
	Arrays.sort(str);
	return str;
    }

            Worker[] getWorkers()
    {
	final List<Worker> res = new ArrayList<>();
	for(Map.Entry<String, Entry<Worker>> e: workers.entrySet())
	    res.add(e.getValue().obj);
	return res.toArray(new Worker[res.size()]);
    }

                PropertiesProvider[] getPropertiesProviders()
    {
	final List<PropertiesProvider> res = new ArrayList<>();
	for(Map.Entry<String, Entry<PropertiesProvider>> e: propsProviders.entrySet())
	    res.add(e.getValue().obj);
	return res.toArray(new PropertiesProvider[res.size()]);
    }

    /*
                    TextEditingExtension[] getTextEditingExtensions()
    {
	final List<TextEditingExtension> res = new ArrayList();
	for(Map.Entry<String, Entry<TextEditingExtension>> e: textEditingExts.entrySet())
	    res.add(e.getValue().obj);
	return res.toArray(new TextEditingExtension[res.size()]);
    }
    */


    static void issueResultingMessage(Luwrain luwrain, int exitCode, String[] lines)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNullItems(lines, "lines");
	final StringBuilder b = new StringBuilder();
	if (lines.length >= 1)
	{
	    b.append(lines[0]);
	    for(int i = 1;i < lines.length;++i)
		b.append(" " + lines[i]);
	}
	final String text = new String(b).trim();
	if (!text.isEmpty())
	    luwrain.message(text, exitCode == 0?Luwrain.MessageType.DONE:Luwrain.MessageType.ERROR); else
	    if (exitCode == 0)
		luwrain.message(luwrain.i18n().getStaticStr("OsCommandFinishedSuccessfully"), Luwrain.MessageType.DONE); else
		luwrain.message(luwrain.i18n().getStaticStr("OsCommandFailed"), Luwrain.MessageType.ERROR);
    }

    //    void takeObjects(Extension ext, Luwrain luwrain)
    void takeObjects(Extension ext, ExtensionObject[] extObjects)
    {
	//	notNull(ext, "ext");
	//	notNull(luwrain, "luwrain");
	notNullItems(extObjects, "extObjects");
	//	for(ExtensionObject s: ext.getExtObjects(luwrain))
	for(final ExtensionObject obj: extObjects)
			if (!add(ext, obj))
			    log.warn("The extension object \'" + obj.getExtObjName() + "\' of the extension " + ext.getClass().getName() + " has been refused by  the object registry");
    }

    static private <E> void removeEntriesByExt(Map<String, Entry<E>> map, Extension ext)
    {
	requireNonNull(map, "map can't be null");
	requireNonNull(ext, "ext can't be null");
	final List<String> deleting = new ArrayList<>();
	for(Map.Entry<String, org.luwrain.core.ObjRegistry.Entry<E>> e: map.entrySet())
	if (e.getValue().ext == ext)
	    deleting.add(e.getKey());
    for(String s: deleting)
	map.remove(s);
    }
}
