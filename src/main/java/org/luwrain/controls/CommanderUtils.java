// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import org.luwrain.core.*;

import org.luwrain.controls.CommanderArea.EntryType;

public class CommanderUtils
{
    static public class ByNameComparator<E> implements java.util.Comparator<E>
    {
	@Override public int compare(E o1, E o2)
	{
	    NullCheck.notNull(o1, "o1");
	    NullCheck.notNull(o2, "o2");
	    if (!(o1 instanceof CommanderArea.NativeItem) || !(o2 instanceof CommanderArea.NativeItem))
		return 0;
	    final CommanderArea.NativeItem w1 = (CommanderArea.NativeItem)o1;
	    final CommanderArea.NativeItem w2 = (CommanderArea.NativeItem)o2;
	    if (w1.getEntryType() == EntryType.PARENT)
		return w2.getEntryType() == EntryType.PARENT?0:-1;
	    if (w2.getEntryType() == EntryType.PARENT)
		return w1.getEntryType() == EntryType.PARENT?0:1;
	    final String name1 = w1.getBaseName().toLowerCase();
	    final String name2 = w2.getBaseName().toLowerCase();
	    if (w1.isDirectory() && w2.isDirectory())
		return name1.compareTo(name2);
	    if (w1.isDirectory())
		return -1;
	    if (w2.isDirectory())
		return 1;
	    return name1.compareTo(name2);
	}
    }

    static public class AllEntriesFilter<E> implements CommanderArea.Filter<E>
    {
	@Override public boolean commanderEntrySuits(E entry)
	{
	    return true;
	}
    }

    static public void defaultEntryAnnouncement(ControlContext context, String name, CommanderArea.EntryType type, boolean marked)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(type, "type");
	if (name.trim().isEmpty() && type != EntryType.PARENT)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return;
	}
	final StringBuilder b = new StringBuilder();
	b.append(context.getSpeakableText(name, Luwrain.SpeakableTextType.PROGRAMMING));
	switch(type)
	{
	case PARENT:
	    context.say(context.getStaticStr("CommanderParentDirectory"));//FIXME:
	    return;
	case DIR:
	    b.append(context.getStaticStr("CommanderDirectory"));
	    break;
	case SYMLINK:
	case SYMLINK_DIR:
	    b.append(context.getStaticStr("CommanderSymlink"));
	    break;
	case SPECIAL:
	    b.append(context.getStaticStr("CommanderSpecial"));
	    break;
	}
	context.setEventResponse(DefaultEventResponse.text(marked?Sounds.SELECTED:Sounds.LIST_ITEM, new String(b)));
    }
}
