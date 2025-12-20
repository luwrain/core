// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.popups.Popups;

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
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(name, "name");
	    NullCheck.notNullItems(items, "items");
	    NullCheck.notNull(popupFlags, "popupFlags");
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
