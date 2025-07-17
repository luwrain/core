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

package org.luwrain.controls.console;

//import java.util.*;

//import org.luwrain.core.*;
import org.luwrain.controls.*;

import static java.util.Objects.*;

public class ArrayModel<E> implements ConsoleArea.Model<E>
    {
	public interface Source<E>
	{
	    E[] getItems();
	}
	protected final Source<E> source;
	public ArrayModel(Source<E> source)
	{
	    this.source = requireNonNull(source, "source can't be null");
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
