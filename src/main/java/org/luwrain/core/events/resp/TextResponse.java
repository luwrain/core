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
