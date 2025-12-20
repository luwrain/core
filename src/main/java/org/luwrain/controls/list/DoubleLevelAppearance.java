// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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
