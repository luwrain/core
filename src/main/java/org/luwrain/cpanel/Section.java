// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public interface Section
{
    SectionArea getSectionArea(ControlPanel controlPanel);
    Element getElement();
    Action[] getSectionActions();
    boolean onSectionActionEvent(ControlPanel controlPanel, ActionEvent event);
}
