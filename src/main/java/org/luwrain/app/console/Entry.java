// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.console;

import java.util.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.message.*;

final class Entry
{
    final String logger;
    final String message;
    final Throwable ex;
    Entry(LogEvent event)
    {
	this.logger = event.getLoggerName();
	this.message = event.getMessage().getFormattedMessage();
	this.ex = event.getMessage().getThrowable();
    }
}
