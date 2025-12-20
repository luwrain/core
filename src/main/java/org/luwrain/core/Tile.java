// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import static org.luwrain.core.NullCheck.*;

final class Tile
{
    final Application app;
    final Area area;
    final boolean popup;
    final Popup.Position popupPos;
    int x = 0, y = 0, width = 0, height = 0;//With title bar;
    int scrolledVert = 0, scrolledHoriz = 0;

    Tile(Application app, Area area)
    {
	//app can be null
	notNull(area, "area");
	this.app = app;
	this.area = area;
	this.popup = false;
	this.popupPos = null;
    }

    Tile(Application app, Area area, Popup.Position popupPos)
    {
	//app can be null
	notNull(area, "area");
	notNull(popupPos, "popupPos");
	this.app = app;
	this.area = area;
	this.popup = true;
	this.popupPos = popupPos;
    }

    void markInvisible()
    {
	x = 0;
	y = 0;
	width = 0;
	height = 0;
    }

    boolean isVisible()
    {
	return x >= 0 && y >= 0 && width > 0 && height > 0;
    }

    boolean valid()
    {
	return isVisible();
    }
}
