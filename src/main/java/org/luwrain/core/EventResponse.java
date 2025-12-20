// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import org.luwrain.io.json.*;

public interface EventResponse
{
    public interface Speech
    {
	void speak(String[] fragments);
	void speakLetter(char letter);
    }

    void announce(Luwrain luwrain, org.luwrain.core.EventResponse.Speech speech, CommonSettings sett);
}
