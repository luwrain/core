// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

import static java.util.Objects.*;

public final class AreaLayoutSwitch
{
    private Luwrain luwrain;
    private final ArrayList<AreaLayout> layouts = new ArrayList<>();
    private int currentIndex = 0;

    public AreaLayoutSwitch(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	requireNonNull(luwrain, "luwrain can't be null");
    }

    public int getCurrentIndex()
    {
	return currentIndex;
    }

    public void add(AreaLayout layout)
    {
	requireNonNull(layout, "layout can't be null");
	layouts.add(layout);
    }

    public boolean show(int index)
    {
	if (index < 0 || index >= layouts.size())
	    return false;
	currentIndex = index;
	luwrain.onNewAreaLayout();
	return true;
    }

    public AreaLayout getCurrentLayout()
    {
	if (currentIndex >= layouts.size())
	    return null;
	return layouts.get(currentIndex);
    }
}
