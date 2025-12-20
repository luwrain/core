// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.events;

import org.luwrain.core.*;

public class MoveHotPointEvent extends SystemEvent
{
protected final int newHotPointX;
    protected final int newHotPointY;
    protected final boolean precisely;

    MoveHotPointEvent(Code customCode,
		      int newHotPointX, int newHotPointY, boolean precisely)
    {
	super(customCode);
	this.newHotPointX = newHotPointX;
	this.newHotPointY = newHotPointY;
	this.precisely = precisely;
    }

    public MoveHotPointEvent(int newHotPointX, int newHotPointY, boolean precisely)
    {
	super(Code.MOVE_HOT_POINT);
	this.newHotPointX = newHotPointX;
	this.newHotPointY = newHotPointY;
	this.precisely = precisely;
    }

    public int getNewHotPointX() { return newHotPointX; }
    public int getNewHotPointY() { return newHotPointY; }
    public boolean precisely() {return precisely;}
}
