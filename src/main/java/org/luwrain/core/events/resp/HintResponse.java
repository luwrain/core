// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.events.resp;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.i18n.*;
import org.luwrain.io.json.*;

import static java.util.Objects.*;
import static org.luwrain.core.events.resp.Base.*;

public class HintResponse implements EventResponse
{
    protected final org.luwrain.core.Hint hint;
    protected final String text;

    public HintResponse(org.luwrain.core.Hint hint, String text)
    {
	requireNonNull(hint, "hint can't be null");
	this.hint = hint;
	this.text = text;
    }

    public HintResponse(org.luwrain.core.Hint hint)
    {
	this(hint, null);
    }

    @Override public void announce(Luwrain luwrain, Speech speech, CommonSettings sett)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	requireNonNull(speech, "speech can't be null");
	final String hintText;
	if (this.text == null)
	    hintText = getTextForHint(luwrain, hint); else
	    hintText = this.text;
	final Sounds sound = getSoundForHint(hint);
	if (sett.isHintsSounds() && sound != null)
	    luwrain.playSound(sound);
	if (sett.isHintsText() && hintText != null && !hintText.trim().isEmpty())
	    luwrain.speak(hintText, org.luwrain.core.Speech.PITCH_HINT); else
	    luwrain.silence();
	
    }
}
