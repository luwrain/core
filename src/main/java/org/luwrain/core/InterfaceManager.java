// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import static java.util.Objects.*;

final class InterfaceManager
{
        private final Core core;
    private final List<Entry> entries = new ArrayList<>();
    final Luwrain systemObj;

    InterfaceManager(Base base)
    {
	requireNonNull(base, "base can't be null");
		this.core = (Core)base;
	this.systemObj = new LuwrainImpl(this.core);
    }

    Luwrain requestNew(Application app)
    {
	requireNonNull(app, "app can't be null");
	final Luwrain existing = findFor(app);
	if (existing != null)
	    return existing;
	final Luwrain luwrain = new LuwrainImpl(core);
	entries.add(new Entry(Entry.Type.APP, app, luwrain));
	return luwrain;
    }

    Luwrain requestNew(Extension ext)
    {
	requireNonNull(ext, "ext can't be null");
	final Luwrain existing = findFor(ext);
	if (existing != null)
	    return existing;
	final Luwrain luwrain = new LuwrainImpl(core);
	entries.add(new Entry(Entry.Type.EXT, ext, luwrain));
	return luwrain;
    }

    private Luwrain findFor(Object obj)
    {
	requireNonNull(obj, "obj can't be null");
	for(Entry e:entries)
	    if (e.obj == obj)
		return e.luwrain;
	return null;
    }

    Application findApp(Luwrain luwrain)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	for(Entry e: entries)
	    if (e.luwrain == luwrain &&
		e.type == Entry.Type.APP &&
		e.obj instanceof Application)
		return (Application)e.obj;
	return null;
    }

    Extension findExt(Luwrain luwrain)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	for(Entry e: entries)
	    if (e.luwrain == luwrain &&
		e.type == Entry.Type.EXT &&
		e.obj instanceof Extension)
		return (Extension)e.obj;
	return null;
    }

void release(Luwrain luwrain)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	for(int i = 0;i < entries.size();i++)
	    if (entries.get(i).luwrain == luwrain)
	    {
		entries.remove(i);
		return;
	    }
    }

    boolean forPopupsWithoutApp(Luwrain luwrain)
    {
	requireNonNull(luwrain, "luwrain can't be null");
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
	    requireNonNull(type, "type can't be null");
	    requireNonNull(obj, "obj can't be null");
	    requireNonNull(luwrain, "luwrain can't be null");
	    this.type = type;
	    this.obj = obj;
	    this.luwrain = luwrain;
	}
    }
}
