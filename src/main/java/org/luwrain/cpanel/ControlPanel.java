// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.cpanel;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public interface ControlPanel
{
    void close();
    void gotoSectionsTree();
    boolean onSystemEvent(SectionArea area, SystemEvent event);
    boolean onInputEvent(SectionArea area, InputEvent event);
    void refreshSectionsTree();
    Luwrain getCoreInterface();
    boolean openAdditionalSectionArea(SectionArea sectionArea, AdditionalSectionArea additionalArea);
    boolean closeAdditionalSectionArea(SectionArea sectionArea);
}
