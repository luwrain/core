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

public abstract class AbstractAppearance<E> implements ListArea.Appearance<E>
{
    @Override public String getScreenAppearance(E item, Set<Flags> flags)
    {
	requireNonNull(item, "item can't be null");
	requireNonNull(flags, "flags can't be null");
	return item.toString();
    }

	@Override public int getObservableLeftBound(E item)
    {
	return 0;
    }

    @Override public int getObservableRightBound(E item)
    {
	requireNonNull(item, "item can't be null");
	return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
    }
}
