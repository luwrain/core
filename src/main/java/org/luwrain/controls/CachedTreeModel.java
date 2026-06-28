// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.NullCheck;
import static java.util.Objects.*;

public class CachedTreeModel implements TreeArea.Model
{
    static protected class CacheItem
    {
	private final Object parent;
	private Object[] objs = new Object[0];

	CacheItem(Object parent)
	{
	    requireNonNull(parent, "parent can't be null");
	    this.parent = parent;
	}
    }

    private final CachedTreeModelSource source;
    private  final List<CacheItem> cache = new ArrayList<>();

    public CachedTreeModel(CachedTreeModelSource source)
    {
	requireNonNull(source, "source can't be null");
	this.source = source;
    }

    @Override public Object getRoot()
    {
	return source.getRoot();
    }

    @Override public void beginChildEnumeration(Object node)
    {
	requireNonNull(node, "node can't be null");
	CacheItem newItem = null;
	for(CacheItem c: cache)
	    if (c.parent.equals(node))
		newItem = c;
	if (newItem == null)
	{
	    newItem = new CacheItem(node);
	    cache.add(newItem);
	}
	final Object[] objs = source.getChildObjs(node);
	if (objs == null || objs.length < 1)
	{
	    newItem.objs = new Object[0];
	    return;
	}
	newItem.objs = objs;
    }

    @Override public int getChildCount(Object parent)
    {
	if (parent == null)
	    return 0;
	for(CacheItem c: cache)
	    if (c.parent.equals(parent))
		return c.objs.length;
	return 0;
    }

    @Override public Object getChild(Object parent, int index)
    {
	if (parent == null)
	    return null;
	for(CacheItem c: cache)
	    if (c.parent.equals(parent))
		return c.objs[index];
	return null;
    }

    @Override public void endChildEnumeration(Object node)
    {
	//FIXME:
    }
}
