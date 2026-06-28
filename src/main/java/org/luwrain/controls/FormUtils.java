// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.popups.Popups;
import static java.util.Objects.*;

public class FormUtils
{
    static public class FixedListChoosing implements FormArea.ListChoosing
    {
	protected Luwrain luwrain;
	protected String name;
	protected Object[] items;
	protected Set<Popup.Flags> popupFlags;

	public FixedListChoosing(Luwrain luwrain, String name,
				 Object[] items, Set<Popup.Flags> popupFlags)
	{
	    requireNonNull(luwrain, "luwrain can't be null");
	    requireNonNull(name, "name can't be null");
	    NullCheck.notNullItems(items, "items");
	    requireNonNull(popupFlags, "popupFlags can't be null");
	    this.luwrain = luwrain;
	    this.name = name;
	    this.items = items;
	    this.popupFlags = popupFlags;
	}

	@Override public Object chooseFormListItem(Area area, String formListItem, Object currentSelected)
	{
	    return     Popups.fixedList(luwrain, name, items, popupFlags);
	}
    }
}
