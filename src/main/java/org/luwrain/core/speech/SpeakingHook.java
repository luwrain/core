// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.speech;

import org.luwrain.core.*;
import static java.util.Objects.*;

public final class SpeakingHook implements Luwrain.HookRunner
{
    private String text;

    public SpeakingHook(String text)
    {
	requireNonNull(text, "text can't be null");
	this.text = text;
    }

    
    @Override public Luwrain.HookResult runHook(Luwrain.Hook hook)
    {
	requireNonNull(hook, "hook can't be null");
final Object res = hook.run(new Object[]{text});
if (res == null)
	return Luwrain.HookResult.CONTINUE;
final String value = res.toString();
if (value != null)
    this.text = value;
	return Luwrain.HookResult.CONTINUE;
    }

    public String getText()
    {
	return text;
    }
}
