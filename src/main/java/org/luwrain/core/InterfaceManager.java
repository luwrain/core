// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

import static org.luwrain.core.NullCheck.*;

final class InterfaceManager
{
        private final Core core;
    private final List<Entry> entries = new ArrayList<>();
    final Luwrain systemObj;

    InterfaceManager(Base base)
    {
	notNull(base, "base");
		this.core = (Core)base;
	this.systemObj = new LuwrainImpl(this.core);
    }

    Luwrain requestNew(Application app)
    {
	notNull(app, "app");
	final Luwrain existing = findFor(app);
	if (existing != null)
	    return existing;
	final Luwrain luwrain = new LuwrainImpl(core);
	entries.add(new Entry(Entry.Type.APP, app, luwrain));
	return luwrain;
    }

    Luwrain requestNew(Extension ext)
    {
	notNull(ext, "ext");
	final Luwrain existing = findFor(ext);
	if (existing != null)
	    return existing;
	final Luwrain luwrain = new LuwrainImpl(core);
	entries.add(new Entry(Entry.Type.EXT, ext, luwrain));
	return luwrain;
    }

    private Luwrain findFor(Object obj)
    {
	notNull(obj, "obj");
	for(Entry e:entries)
	    if (e.obj == obj)
		return e.luwrain;
	return null;
    }

    Application findApp(Luwrain luwrain)
    {
	notNull(luwrain, "luwrain");
	for(Entry e: entries)
	    if (e.luwrain == luwrain &&
		e.type == Entry.Type.APP &&
		e.obj instanceof Application)
		return (Application)e.obj;
	return null;
    }

    Extension findExt(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	for(Entry e: entries)
	    if (e.luwrain == luwrain &&
		e.type == Entry.Type.EXT &&
		e.obj instanceof Extension)
		return (Extension)e.obj;
	return null;
    }

void release(Luwrain luwrain)
    {
	notNull(luwrain, "luwrain");
	for(int i = 0;i < entries.size();i++)
	    if (entries.get(i).luwrain == luwrain)
	    {
		entries.remove(i);
		return;
	    }
    }

    boolean forPopupsWithoutApp(Luwrain luwrain)
    {
	notNull(luwrain, "luwrain");
	return luwrain == systemObj || findExt(luwrain) != null;
    }

    static private final class Entry
    {
	enum Type {APP, EXT};
	final Type type;
	final Object obj;
	final Luwrain luwrain;
	Entry(Type type, Object obj, Luwrain luwrain)
	{
	    notNull(type, "type");
	    notNull(obj, "obj");
	    notNull(luwrain, "luwrain");
	    this.type = type;
	    this.obj = obj;
	    this.luwrain = luwrain;
	}
    }
}
