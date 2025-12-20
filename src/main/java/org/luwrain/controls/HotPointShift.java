// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import org.luwrain.core.*;

public class HotPointShift implements HotPointControl
{
    protected final HotPointControl control;
    private int offsetX = 0;
    private int offsetY = 0;

    public HotPointShift(HotPointControl control, int offsetX, int offsetY)
    {
	NullCheck.notNull(control, "control");
	if (offsetX < 0)
	    throw new IllegalArgumentException("offsetX can't be negative");
	if (offsetY < 0)
	    throw new IllegalArgumentException("offsetY can't be negative");
	this.control = control;
	this.offsetX = offsetX;
	this.offsetY = offsetY;
    }

    @Override public void beginHotPointTrans()
    {
	control.beginHotPointTrans();
    }

    @Override public void endHotPointTrans()
    {
	control.endHotPointTrans();
    }

    @Override public int getHotPointX()
    {
	final int value = control.getHotPointX();
	return value >= offsetX?value - offsetX:0;
    }

    @Override public void setHotPointX(int value)
    {
	control.setHotPointX(value + offsetX);
    }

    @Override public int getHotPointY()
    {
	final int value = control.getHotPointY();
	return value >= offsetY?value - offsetY:0;
    }

    @Override public void setHotPointY(int value)
    {
	control.setHotPointY(value + offsetY);
    }

    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }

    public void setOffsetX(int value)
    {
	if (value < 0)
	    throw new IllegalArgumentException("value can't be negative");
	offsetX = value;
    }

    public void setOffsetY(int value)
    {
	if (value < 0)
	    throw new IllegalArgumentException("value can't be negative");
	offsetY = value;
    }
}
