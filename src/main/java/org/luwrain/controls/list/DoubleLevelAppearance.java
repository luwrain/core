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

import org.luwrain.core.*;
import org.luwrain.controls.*;

import static org.luwrain.core.DefaultEventResponse.*;
import static java.util.Objects.*;

abstract public class DoubleLevelAppearance<E> implements ListArea.Appearance<E>
{
    protected final ControlContext context;
    public DoubleLevelAppearance(ControlContext context)
    {
	this.context = requireNonNull(context, "context can't be null");;
    }

    abstract public boolean isSectionItem(E item);

    public void announceNonSection(E item)
    {
	requireNonNull(item, "item can't be null");
	context.setEventResponse(listItem(getNonSectionScreenAppearance(item)));
    }

    public String getNonSectionScreenAppearance(E item)
    {
	requireNonNull(item, "item can't be null");
	return item.toString();
    }

    public void announceSection(E item)
    {
	requireNonNull(item, "item can't be null");
	context.playSound(Sounds.DOC_SECTION);
	context.say(getSectionScreenAppearance(item));
    }

    public String getSectionScreenAppearance(E item)
    {
	requireNonNull(item, "item");
	return item.toString();
    }

    @Override public void announceItem(E item, Set<Flags> flags)
    {
	requireNonNull(item, "item can't be null");
	requireNonNull(flags, "flags can't be null");
	if (isSectionItem(item))
	    announceSection(item); else
	    announceNonSection(item);
    }

    @Override public String getScreenAppearance(E item, Set<Flags> flags)
    {
	requireNonNull(item, "item can't be null");
	requireNonNull(flags, "flags can't be null");
	if (isSectionItem(item))
	    return getSectionScreenAppearance(item);
	return "  " + getNonSectionScreenAppearance(item);
    }

    @Override public int getObservableLeftBound(E item)
    {
	requireNonNull(item, "item can't be null");
	if (isSectionItem(item))
	    return 0;
	return 2;
    }

    @Override public int getObservableRightBound(E item)
    {
	requireNonNull(item, "item");
	return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
    }
}
