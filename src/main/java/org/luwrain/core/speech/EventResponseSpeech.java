// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.speech;

import org.luwrain.core.*;
import org.luwrain.i18n.*;
import static java.util.Objects.*;

public final class EventResponseSpeech implements EventResponse.Speech
{
    private final Speech speech;
    private final I18n i18n;
    private final SpeakingText speakableText;

    public EventResponseSpeech(Speech speech, I18n i18n, SpeakingText speakableText)
    {
	requireNonNull(speech, "speech can't be null");
	requireNonNull(i18n, "i18n can't be null");
	requireNonNull(speakableText, "speakableText can't be null");
	this.speech = speech;
	this.i18n = i18n;
	this.speakableText = speakableText;
    }

    @Override public void speak(String[] parts)
    {
	NullCheck.notNullItems(parts, "parts");
	if (parts.length == 0)
	    return;
	if (parts.length == 1)
	{
	    speech.speakEventResponse(speakableText.processEventResponse(parts[0]));
	    return;
	}
	final StringBuilder b = new StringBuilder();
	b.append(parts[0]);
	for(int i = 1;i < parts.length;++i)
	    b.append(", ").append(parts[i]);
	speech.speakEventResponse(speakableText.processEventResponse(new String(b)));
    }

    @Override public void speakLetter(char letter)
    {
	final String value = i18n.hasSpecialNameOfChar(letter);
	if (value == null)
	    speech.speakLetter(letter, 0, 0); else
	    speech.speak(value, 0, 0);
    }
}
