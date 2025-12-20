// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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
