// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import org.luwrain.core.*;

public interface Braille
{
    InitResult init(EventConsumer eventConsumer);
    String getDriverName();
    int getDisplayWidth();
    int getDisplayHeight();
    void writeText(String text);
}
