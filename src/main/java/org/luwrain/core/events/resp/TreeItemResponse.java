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

package org.luwrain.core.events.resp;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.i18n.*;
import org.luwrain.io.json.*;

import static java.util.Objects.*;
import static org.luwrain.core.events.resp.Base.*;

public class TreeItemResponse implements EventResponse
{
    public enum Type {LEAF, EXPANDED, COLLAPSED};
    protected final Type type;
    protected final Sounds sound;
    protected final String text;
    protected final int level;
    protected final Suggestions suggestion;

    public TreeItemResponse(Type type, Sounds sound, String text, int level, Suggestions suggestion)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(text, "text");
	this.type = type;
	this.sound = sound;
	this.text = text;
	this.level = level;
	if (level < 1)
	    throw new IllegalArgumentException("level (" + level + ") may not be less than one");
	this.suggestion = suggestion;
    }

    public TreeItemResponse(Type type, String text, int level, Suggestions suggestion)
    {
	this(type, null, text, level, suggestion);
    }

    public TreeItemResponse(Type type, String text, int level)
    {
	this(type, null, text, level, null);
    }

    @Override public void announce(Luwrain luwrain, Speech speech, CommonSettings sett)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(speech, "speech");
	if (sound != null)
	    luwrain.playSound(sound); else
	    switch(type)
	    {
	    case COLLAPSED:
		luwrain.playSound(Sounds.COLLAPSED);
		break;
	    case EXPANDED:
		luwrain.playSound(Sounds.EXPANDED);
		break;
	    case LEAF:
		luwrain.playSound(Sounds.LIST_ITEM);
		break;
	    }
	final List<String> parts = new ArrayList<>();
	parts.add(text);
	switch(type)
	{
	case EXPANDED:
	    parts.add(luwrain.i18n().getStaticStr("TreeExpanded"));
	    break;
	case COLLAPSED:
	    parts.add(luwrain.i18n().getStaticStr("TreeCollapsed"));
	    break;
	}
	if (level > 1)
	    parts.add(luwrain.i18n().getStaticStr("TreeLevel") + String.valueOf(level));
	if (suggestion != null)
	{
	    final String suggestionText = getSuggestionText(suggestion, luwrain.i18n());
	    if (suggestionText != null)
		parts.add(suggestionText);
	}
	speech.speak(parts.toArray(new String[parts.size()]));
    }
}
