// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.speech;

import org.luwrain.core.*;
import static java.util.Objects.*;

public class SpeakingText
{
    private ExtensionsManager  extensions;

    public SpeakingText(ExtensionsManager extensions)
    {
	requireNonNull(extensions, "extensions can't be null");
	this.extensions = extensions;
    }

        public String  processRegular(String text)
    {
	requireNonNull(text, "text can't be null");
	final SpeakingHook hook = new SpeakingHook(text);
	extensions.runHooks("luwrain.speech.text.regular", hook);
	return hook.getText().replaceAll("\\h", " ");
    }

        public String  processEventResponse(String text)
    {
	requireNonNull(text, "text can't be null");
	return processRegular(text);
    }
}
