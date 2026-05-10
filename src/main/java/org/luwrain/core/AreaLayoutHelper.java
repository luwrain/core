// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import static java.util.Objects.*;

public class AreaLayoutHelper
{
    public enum Position {LEFT, RIGHT, TOP, BOTTOM};

    public interface UpdateNotification
    {
	void onLayoutUpdate();
    }

    protected final UpdateNotification notification;
    protected Area basicArea = null;
    protected AreaLayout basicLayout = null;

    protected Area additionalArea = null;
    protected Position additionalAreaPos = null;
    protected AreaLayout tempLayout = null;

    public AreaLayoutHelper(UpdateNotification notification)
    {
	requireNonNull(notification, "notification can't be null");
	this.notification = notification;
    }

    public AreaLayoutHelper(UpdateNotification notification, Area basicArea)
    {
	requireNonNull(notification, "notification can't be null");
	requireNonNull(basicArea, "basicArea can't be null");
	this.notification = notification;
	this.basicArea = basicArea;
    }

    public AreaLayoutHelper(UpdateNotification notification, AreaLayout basicLayout)
    {
	requireNonNull(notification, "notification can't be null");
	requireNonNull(basicLayout, "basicLayout can't be null");
	this.notification = notification;
	this.basicLayout = basicLayout;
    }

    public void setBasicArea(Area area)
    {
	requireNonNull(area, "area can't be null");
	this.basicArea = area;
	this.basicLayout = null;
	notification.onLayoutUpdate();
    }

    public void setBasicLayout(AreaLayout layout)
    {
	requireNonNull(layout, "layout can't be null");
	this.basicLayout = layout;
	this.basicArea = null;
	notification.onLayoutUpdate();
    }

    public void clear()
    {
	this.basicArea = null;
	this.basicLayout = null;
	notification.onLayoutUpdate();
    }

    public boolean openAdditionalArea(Area area, Position pos)
    {
	requireNonNull(area, "area can't be null");
	requireNonNull(pos, "pos can't be null");
	if (basicLayout != null)
	    return false;
	additionalArea = area;
	additionalAreaPos = pos;
	notification.onLayoutUpdate();
	return true;
    }

    public void closeAdditionalArea()
    {
	if (additionalArea == null)
	    return;
	additionalArea = null;
	additionalAreaPos = null;
	notification.onLayoutUpdate();
    }

    public Area getAdditionalArea()
    {
	return additionalArea;
    }

    public boolean hasAdditionalArea()
    {
	return additionalArea != null && additionalAreaPos != null;
    }

    public void openTempArea(Area area)
    {
	requireNonNull(area, "area can't be null");
	tempLayout = new AreaLayout(area);
	notification.onLayoutUpdate();
    }

    public void openTempLayout(AreaLayout layout)
    {
	requireNonNull(layout, "layout can't be null");
	tempLayout = layout;
	notification.onLayoutUpdate();
    }

    public void closeTempLayout()
    {
	if (tempLayout == null)
	    return;
	tempLayout = null;
	notification.onLayoutUpdate();
    }

    public AreaLayout getTempLayout()
    {
	return tempLayout;
    }

    public AreaLayout getLayout()
    {
	if (tempLayout != null)
	    return tempLayout;
	if (basicLayout != null)
	    return basicLayout;
	if (basicArea == null)
	    return null;
	if (additionalArea == null || additionalAreaPos == null)
	    	    	return new AreaLayout(basicArea);
			    switch(additionalAreaPos)
	    {
	    case RIGHT:
		return new AreaLayout(AreaLayout.LEFT_RIGHT, basicArea, additionalArea);
	    case LEFT:
		return new AreaLayout(AreaLayout.LEFT_RIGHT, additionalArea, basicArea);
	    case TOP:
		return new AreaLayout(AreaLayout.TOP_BOTTOM, additionalArea, basicArea);
	    case BOTTOM:
		return new AreaLayout(AreaLayout.TOP_BOTTOM, basicArea, additionalArea);
	    default:
		return null;
	    }
    }

    static public boolean activateNextArea(Luwrain luwrain, AreaLayout layout, Area activeArea)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	requireNonNull(layout, "layout can't be null");
	requireNonNull(activeArea, "activeArea can't be null");
	final Area area = layout.getNextArea(activeArea);
	if (area == null)
	    return false;
	luwrain.setActiveArea(area);
	return true;
    }

    static public boolean activatePrevArea(Luwrain luwrain, AreaLayout layout, Area activeArea)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	requireNonNull(layout, "layout can't be null");
	requireNonNull(activeArea, "activeArea can't be null");
	final Area area = layout.getPrevArea(activeArea);
	if (area == null)
	    return false;
	luwrain.setActiveArea(area);
	return true;
    }
}
