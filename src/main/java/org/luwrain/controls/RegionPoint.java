// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class RegionPoint implements AbstractRegionPoint
{
    protected int hotPointX = -1;
    protected int hotPointY = -1;

    public boolean onSystemEvent(SystemEvent event, int hotPointX, int hotPointY)
    {
	NullCheck.notNull(event, "event");
	if (hotPointX < 0 || hotPointY < 0)
	    throw new IllegalArgumentException("hotPointX and hotPointY must be greater or equal to zero");
	if (event.getType() == SystemEvent.Type.REGULAR)
	    switch(event.getCode())
	    {
	    case REGION_POINT:
		this.hotPointX = hotPointX;
		this.hotPointY = hotPointY;
		return true;
	    }
	return false;
    }

    public boolean isInitialized()
    {
	return hotPointX >= 0 && hotPointY >= 0;
    }

    public void set(int hotPointX, int hotPointY)
    {
	if (hotPointX < 0 || hotPointY < 0)
	    throw new IllegalArgumentException("hotPointX and hotPointY must be greater or equal to zero");
	this.hotPointX = hotPointX;
	this.hotPointY = hotPointY;
    }

    @Override public int getHotPointX()
    {
	return hotPointX;
    }

    @Override public int getHotPointY()
    {
	return hotPointY;
    }

    public void reset()
    {
	hotPointX = -1;
	hotPointY = -1;
    }
}
