// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.speech;

import org.luwrain.core.*;

public class SpeakingText
{
    private ExtensionsManager  extensions;

    public SpeakingText(ExtensionsManager extensions)
    {
	NullCheck.notNull(extensions, "extensions");
	this.extensions = extensions;
    }

        public String  processRegular(String text)
    {
	NullCheck.notNull(text, "text");
	final SpeakingHook hook = new SpeakingHook(text);
	extensions.runHooks("luwrain.speech.text.regular", hook);
	return hook.getText().replaceAll("\\h", " ");
    }

        public String  processEventResponse(String text)
    {
	NullCheck.notNull(text, "text");
	return processRegular(text);
    }
}
