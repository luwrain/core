// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import static org.luwrain.core.Base.*;
import static org.luwrain.core.NullCheck.*;

final class TilesManager
{
    final AppManager apps;
    private boolean activePopup = false;

    TilesManager(AppManager apps)
    {
	notNull(apps, "apps");
	this.apps = apps;
    }

    boolean setPopupActive()
    {
	if (!isPopupOpened())
	    return false;
	activePopup = true;
	return true;
    }

    void updatePopupState()
    {
	if (activePopup)
	{
	    if (!isPopupOpened())
		activePopup = false;
	} else
	{
	    if (apps.getFrontActiveAreaForActiveApp() == null && isPopupOpened())
		activePopup = true;
	}
    }

    Area getActiveArea()
    {
	    if (isPopupActive())
		return apps.getFrontAreaOfTopPopup();
	final Area activeArea = apps.getFrontActiveAreaForActiveApp();
	if (activeArea != null)
	    return activeArea;
	if (isPopupOpened())
	{
	    activePopup = true;
	    return apps.getFrontAreaOfTopPopup();
	}
	return null;
    }

boolean isPopupActive()
    {
	if (!activePopup)
	    return false;
	    if (isPopupOpened())
		return true;
	    activePopup = false;
	    return false;
    }

    void activateNextArea()
    {
	Area activeArea = getActiveArea();
	if (activeArea == null)
	    return;
	Object[] objs = getWindows().getObjects();
	final Tile[] tiles = new Tile[objs.length];
	for(int i = 0;i < objs.length;++i)
	    tiles[i] = (Tile)objs[i];
	if (tiles == null || tiles.length <= 0)
	{
	    activePopup = isPopupOpened();
	    return;
	}
	int index;
	for(index = 0;index < tiles.length;index++)
	    if (tiles[index].area == activeArea)
		break;
	index++;
	if (index >= tiles.length)
	    index = 0;
	activePopup = tiles[index].popup;
	if (!activePopup)
	{
	    apps.setActiveAreaForApp(tiles[index].app, tiles[index].area);
	    apps.setActiveApp(tiles[index].app);
	}
    }

    Tiles getWindows()
    {
	final Application activeApp = apps.getActiveApp();
			final Tiles windows;
	if (activeApp != null)
windows = constructLayoutOfApp(activeApp); else
	    windows = new Tiles();
	if (isPopupOpened())
	{
	    final Tile popupWindow = new Tile(apps.getAppOfLastPopup(), apps.getFrontAreaOfTopPopup(), apps.getPositionOfTopPopup());
	    switch(popupWindow.popupPos)
	    {
	    case BOTTOM:
		windows.addBottom(popupWindow);
		break;
	    case TOP:
		windows.addTop(popupWindow);
		break;
	    case LEFT:
		windows.addLeftSide(popupWindow);
		break;
	    case RIGHT:
		windows.addRightSide(popupWindow);
		break;
	    }
	}
	return windows;
    }

       private  boolean isPopupOpened()
    {
	if (!apps.hasAnyPopup())
	    return false;
	final Application app = apps.getAppOfLastPopup();
	if (app == null)//it is an environment popup
	    return true;
	return apps.isActiveApp(app);
    }

    private Tiles constructLayoutOfApp(Application app)
    {
	notNull(app, "app");
	final AreaLayout layout = apps.getFrontAreaLayout(app);
	if (layout == null)
	{
	    warn("got null area layout for the application " + app.getClass().getName());
	    return null;
	}
	final Tiles tiles = new Tiles();
	switch(layout.layoutType)
	{
	case SINGLE:
	    tiles.createSingle(new Tile(app, layout.area1));
	    break;
	case LEFT_RIGHT:
	    tiles.createLeftRight(new Tile(app, layout.area1),
				  new Tile(app, layout.area2));
	    break;
	case TOP_BOTTOM:
	    tiles.createTopBottom(new Tile(app, layout.area1),
				  new Tile(app, layout.area2));
	    break;
	case LEFT_TOP_BOTTOM:
	    tiles.createLeftTopBottom(new Tile(app, layout.area1),
				      new Tile(app, layout.area2),
				      new Tile(app, layout.area3));
	    break;
	case LEFT_RIGHT_BOTTOM:
	    tiles.createLeftRightBottom(new Tile(app, layout.area1),
					new Tile(app, layout.area2),
					new Tile(app, layout.area3));
	    break;
	}
	return tiles;
    }
}
