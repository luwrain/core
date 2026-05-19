// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.console;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.ConsoleArea.InputHandler;
import static java.util.Objects.*;

final class Utils
{
    static String firstWord(String text)
    {
	requireNonNull(text, "text can't be null");
	final int pos = text.indexOf(" ");
	if (pos < 0)
	    return text.trim();
	return text.substring(0, pos).trim();
    }
}
