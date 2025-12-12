// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.cpanel;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public interface ControlPanel
{
    void close();
    void gotoSectionsTree();
    boolean onSystemEvent(SystemEvent event);
    boolean onInputEvent(InputEvent event);
    void refreshSectionsTree();
    Luwrain getCoreInterface();
}
