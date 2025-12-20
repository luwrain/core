// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls.console;

import java.util.*;

//import org.luwrain.core.*;
import org.luwrain.controls.*;

import static java.util.Objects.*;

public class ListModel<E> implements ConsoleArea.Model<E>
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
	if (index < 0 || index >= source.size())
	    throw new IllegalArgumentException("Illegal index: " + String.valueOf(index));
	return source.get(index);
    }
}
