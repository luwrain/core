// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class EmptySection implements Section
{
    @Override public SectionArea getSectionArea(ControlPanel controlPanel)
    {
	return null;
    }

    @Override public Element getElement()
    {
	return null;
    }

    @Override public Action[] getSectionActions()
    {
	return new Action[0];
    }

    @Override public boolean onSectionActionEvent(ControlPanel controlPanel, ActionEvent event)
    {
	return false;
    }
}
