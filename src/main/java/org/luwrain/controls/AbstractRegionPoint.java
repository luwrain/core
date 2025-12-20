// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public interface AbstractRegionPoint extends HotPoint
{
    boolean onSystemEvent(SystemEvent event, int hotPointX, int hotPointY);
    boolean isInitialized();
    void set(int hotPointX, int hotPointY);
    void reset();
}
