// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;

public final class ConsoleUtils
{
    static public class ArrayModel<E> implements ConsoleArea.Model<E>
    {
	public interface Source<E>
	{
	    E[] getItems();
	}
	protected final Source<E> source;
	public ArrayModel(Source<E> source)
	{
	    NullCheck.notNull(source, "source");
	    this.source = source;
	}
	@Override public int getItemCount()
	{
	    final Object[] o = source.getItems();
	    return o != null?o.length:0;
	}
	@Override public E getItem(int index)
	{
	    final E[] o = source.getItems();
	    if (o == null)
		throw new IllegalStateException("No items");
	    if (index < 0 || index >= o.length)
		throw new IllegalArgumentException("Illegal index: " + String.valueOf(index));
	    return o[index];
	}
    }

    static public class ListModel<E> implements ConsoleArea.Model<E>
    {
	protected final List<E> source;
	public ListModel(List<E> source)
	{
	    NullCheck.notNull(source, "source");
	    this.source = source;
	}
	@Override public int getItemCount()
	{
	    return source.size();
	}
	@Override public E getItem(int index)
	{
	    if (index < 0 || index >= source.size())
		throw new IllegalArgumentException("Illegal index: " + String.valueOf(index));
	    return source.get(index);
	}
    }
}
