// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.NullCheck;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

import static java.util.Objects.*;

final class SectionsTreeModelSource implements CachedTreeModelSource
{
    private Base base;
    private HashMap<Element, TreeItem> treeItems;

    SectionsTreeModelSource(Base base, HashMap<Element, TreeItem> treeItems)
    {
	requireNonNull(base, "base can't be null");
	requireNonNull(treeItems, "treeItems can't be null");
	this.base = base;
	this.treeItems = treeItems;
    }

void     setTreeItems(HashMap<Element, TreeItem> treeItems)
    {
	requireNonNull(treeItems, "treeItems can't be null");
	this.treeItems = treeItems;
    }

    @Override public Object getRoot()
    {
	return findSect(StandardElements.ROOT);
    }

    @Override public Object[] getChildObjs(Object obj)
    {
	requireNonNull(obj, "obj can't be null");
	final Element el = ((Section)obj).getElement();
	final TreeItem item = treeItems.get(el);
	base.addOnDemandElements(item);
	if (item == null || item.children.isEmpty())
	    return new Section[0];
	final LinkedList<Section> res = new LinkedList<Section>();
	for(Element c: item.children)
	{
	    final Section sect = findSect(c);
	    if (sect != null)
		res.add(sect);
	}
	return res.toArray(new Section[res.size()]);
    }

    private Section findSect(Element el)
    {
	final TreeItem item = treeItems.get(el);
	if (el == null)
	    return null;
	if (item.sect != null)
	    return item.sect;
	if (item.factory == null)
	    return null;
	item.sect = item.factory.createSection(el);
	return item.sect;
    }
}
