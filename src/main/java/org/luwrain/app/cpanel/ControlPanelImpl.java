// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.cpanel;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

class ControlPanelImpl implements org.luwrain.cpanel.ControlPanel
{
    private Luwrain luwrain;
    private ControlPanelApp app;

    ControlPanelImpl(Luwrain luwrain, ControlPanelApp app)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(app, "app");
	this.luwrain = luwrain;
	this.app = app;
    }

    @Override public void close()
    {
	app.closeApp();
    }

    @Override public void gotoSectionsTree()
    {
	app.gotoSections();
    }

    @Override public void refreshSectionsTree()
    {
app.refreshSectionsTree();
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ESCAPE:
		close();
		return true;
	    case TAB:
		gotoSectionsTree();
		return true;
	    }
	return false;
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return false;
	switch(event.getCode())
	{
	case CLOSE:
	    close();
	    return true;
	default:
	    return false;
	}
    }

    @Override public Luwrain getCoreInterface()
    {
	return luwrain;
    }
}
