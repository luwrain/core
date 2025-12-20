// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.events.resp;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.i18n.*;
import org.luwrain.io.json.*;

import static java.util.Objects.*;
import static org.luwrain.core.events.resp.Base.*;


public class ListItemResponse implements EventResponse
{
    protected final Sounds sound;
    protected final String text;
    protected final Suggestions suggestion;

    public ListItemResponse(Sounds sound, String text, Suggestions suggestion)
    {
	NullCheck.notNull(text, "text");
	this.sound = sound;
	this.text = text;
	this.suggestion = suggestion;
    }

    @Override public void announce(Luwrain luwrain, Speech speech, CommonSettings sett)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(speech, "speech");
	if (sound != null)
	    luwrain.playSound(sound); else
	    luwrain.playSound(Sounds.LIST_ITEM);
	if (suggestion == null)
	{
	    speech.speak(new String[]{text});
	    return;
	}
	final String suggestionText = getSuggestionText(suggestion, luwrain.i18n());
	if (suggestionText != null)
	    speech.speak(new String[]{text, suggestionText}); else
	    speech.speak(new String[]{text});
    }
}
