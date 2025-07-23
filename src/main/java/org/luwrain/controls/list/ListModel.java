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

package org.luwrain.controls.list;

import java.util.*;

import org.luwrain.controls.*;

import static java.util.Objects.*;

public class ListModel<E> implements ListArea.Model<E>
{
    protected final List<E> source;
    public ListModel(List<E> source)
    {
	this.source = requireNonNull(source, "source can't be null");
    }

    @Override public int getItemCount()
    {
	return source.size();
    }

    @Override public E getItem(int index)
    {
	return source.get(index);
    }

    @Override public void refresh()
    {
    }
    }
