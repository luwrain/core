// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import static org.luwrain.core.NullCheck.*;

public final class ScriptText extends ScriptSource
{
    private final String text;


    public ScriptText(String text)
    {
	notNull(text, "text");
	this.text = text;
    }

    public String getText()
    {
	return this.text;
    }
}
