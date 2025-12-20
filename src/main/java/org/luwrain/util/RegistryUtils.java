// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

//LWR_API 2.0

package org.luwrain.util;

import java.util.*;

import org.luwrain.core.*;

public final class RegistryUtils
{
    static public String[] getStringArray(Registry registry, String path)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notEmpty(path, "path");
	final String[] ids = registry.getValues(path);
	if (ids.length == 0)
	    return new String[0];
	int maxId = -1;
	for(String i: ids)
	    try 
	    {
		if (registry.getTypeOf(Registry.join(path, i)) != Registry.STRING)
		    continue;
		final int v = Integer.parseInt(i);
		if (v < 0)
		    continue;
		if (maxId < v)
		    maxId = v;
	    }
	    catch (NumberFormatException e)
	    {
	    }
	if (maxId == -1)
	    return new String[0];
	final String[] res = new String[maxId + 1];
	for(String i: ids)
	    try 
	    {
		final String fullPath = Registry.join(path, i);
		if (registry.getTypeOf(fullPath) != Registry.STRING)
		    continue;
		final int index = Integer.parseInt(i);
		if (index < 0)
		    continue;
		final String value = registry.getString(fullPath);
		res[index] = value;
	    }
	    catch(NumberFormatException e)
	    {
	    }
	return res;
    }

    static public void setStringArray(Registry registry, String path, String[] items)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notEmpty(path, "path");
	NullCheck.notNullItems(items, "items");
	registry.addDirectory(path);
	for(int i = 0;i < items.length;i++)
	{
	    String index = String.valueOf(i);
	    while(index.length() < 3)
		index = "0" + index;
	    final String fullPath = Registry.join(path, index);
	    registry.deleteValue(fullPath);
	    registry.setString(fullPath, items[i]);
	}
    }
}
