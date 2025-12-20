// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

public final class AreaLayoutSwitch
{
    private Luwrain luwrain;
    private final ArrayList<AreaLayout> layouts = new ArrayList<>();
    private int currentIndex = 0;

    public AreaLayoutSwitch(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	NullCheck.notNull(luwrain, "luwrain");
    }

    public int getCurrentIndex()
    {
	return currentIndex;
    }

    public void add(AreaLayout layout)
    {
	NullCheck.notNull(layout, "layout");
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
