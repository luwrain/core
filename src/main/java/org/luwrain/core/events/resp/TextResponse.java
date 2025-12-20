// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.events.resp;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.i18n.*;
import org.luwrain.io.json.*;

import static java.util.Objects.*;


public class TextResponse implements EventResponse
{
    protected final Sounds sound;
    protected final String text;

    public TextResponse(Sounds sound, String text)
    {
	requireNonNull(text, "text can't be null");
	this.sound = sound;
	this.text = text;
    }

    @Override public void announce(Luwrain luwrain, Speech speech, CommonSettings sett)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(text, "text");
	NullCheck.notNull(speech, "speech");
	luwrain.playSound(sound);
	if (!text.trim().isEmpty())
	    speech.speak(new String[]{text});
    }
}
