// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.cpanel;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.cpanel.*;
import static java.util.Objects.*;

final class ControlPanelImpl implements org.luwrain.cpanel.ControlPanel
{
    private final Luwrain luwrain;
    private final ControlPanelApp app;

    ControlPanelImpl(Luwrain luwrain, ControlPanelApp app)
    {
	this.luwrain = requireNonNull(luwrain, "luwrain can't be null");
	this.app = requireNonNull(app, "app can't be null");
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

    @Override public boolean onInputEvent(SectionArea area, InputEvent event)
    {
	requireNonNull(event, "event can't be null");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ESCAPE:
		close();
		return true;
	    case TAB:
		if (app.currentOptionsArea != null && app.currentOptionsArea == area && app.additionalArea != null)
		    luwrain.setActiveArea(app.additionalArea); else
		    gotoSectionsTree();
		return true;
	    }
	return false;
    }

    @Override public boolean onSystemEvent(SectionArea area, SystemEvent event)
    {
	requireNonNull(event, "event can't be null");
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

        @Override public boolean openAdditionalSectionArea(SectionArea sectionArea, AdditionalSectionArea additionalArea)
    {
	requireNonNull(sectionArea, "sectionArea can't be null");
	requireNonNull(additionalArea, "additionalArea can't be null");
	if (app.currentOptionsArea == null || app.currentOptionsArea != sectionArea)
	    return false;
	app.additionalArea = additionalArea;
	luwrain.onNewAreaLayout();
	luwrain.setActiveArea(app.additionalArea);
	return true;
    }
    
    @Override public boolean  closeAdditionalSectionArea(SectionArea sectionArea)
    {
		requireNonNull(sectionArea, "sectionArea can't be null");
	if (app.currentOptionsArea == null || app.currentOptionsArea != sectionArea)
	    return false;
	if (app.additionalArea == null)
	    return false;
	app.additionalArea = null;
	luwrain.onNewAreaLayout();
	app.gotoOptions();
	return true;
    }

    @Override public Luwrain getCoreInterface()
    {
	return luwrain;
    }
}
