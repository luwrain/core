// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.console;

import java.util.*;

interface ConsoleCommand
{
    boolean onCommand(String text, App app);
}
