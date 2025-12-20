// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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
