// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import org.apache.logging.log4j.*;

import static java.util.Objects.*;

final class LaunchedApp extends LaunchedAppPopups
{
    static private final Logger log = LogManager.getLogger();
    
    final Application app;
    Application activeAppBeforeLaunch = null;
    private AreaLayout.Type layoutType;
    private Area[] areas;
    private OpenedArea[] areaWrappings;
    private int activeAreaIndex = 0;

    LaunchedApp(Application app)
    {
	requireNonNull(app, "app can't be null");
	this.app = app;
    }

    boolean init()
    {
	final AreaLayout layout = getValidAreaLayout();
	layoutType = layout.layoutType;
	areas = layout.getAreas();
	if (areas == null)
	{
	    log.warn("The application {} has area layout without areas", app.getClass().getName());
	    return false;
	}
	this.areaWrappings = new OpenedArea[areas.length];
	for(int i = 0;i < areas.length;++i)
	{
	    if (areas[i] == null)
	    {
		log.warn("The application {} has null area", app.getClass().getName());
		return false;
	    }
	    areaWrappings[i] = new OpenedArea(areas[i]);
	}
	return true;
    }

    boolean refreshAreaLayout()
    {
	final Area previouslyActiveArea = areas[activeAreaIndex];
	final AreaLayout newLayout = getValidAreaLayout();
	final AreaLayout.Type newLayoutType = newLayout.layoutType;
	final Area[] newAreas = newLayout.getAreas();
	if (newAreas == null)
	{
	    log.warn("The application {} has area layout without areas", app.getClass().getName());
	    return false;
	}
	final OpenedArea[] newAreaWrappings = new OpenedArea[newAreas.length];
	for(int i = 0;i < newAreas.length;++i)
	{
	    if (newAreas[i] == null)
	    {
		log.warn("The application {} has null area", app.getClass().getName());
		return false;
	    }
	    newAreaWrappings[i] = new OpenedArea(newAreas[i]);
	}
	layoutType = newLayoutType;
	areas = newAreas;
	areaWrappings = newAreaWrappings;
	activeAreaIndex = -1;
	for(int i = 0;i < areas.length;++i)
	    if (previouslyActiveArea == areas[i])
		activeAreaIndex = i;
	if (activeAreaIndex < 0 || activeAreaIndex > areas.length)
	    activeAreaIndex = 0;
	return true;
    }

    private AreaLayout getValidAreaLayout()
    {
	final AreaLayout layout;
	try {
	    layout = app.getAreaLayout(); //TODO: Safe context
	}
	catch (Throwable e)
	{
	    log.warn("The application {} thrown an exception on getAreaLayout()", app.getClass().getName(), e);
	    return null;
	}
	if (layout == null)
	{
	    log.warn("the application {} returned empty area layout", app.getClass().getName());
	    return null;
	}
	if (!layout.isValid())
	{
	    log.warn("The application {} returned invalid area layout", app.getClass().getName());
	    return null;
	}
	return layout;
    }

    void removeReviewWrappers()
    {
	if (areaWrappings != null)
	    for(OpenedArea w: areaWrappings)
		w.wrapper = null;
    }

    //Takes the reference of any kind, either to original area  or to a wrapper
    boolean setActiveArea(Area area)
    {
	requireNonNull(area, "area can't be null");
	if (areaWrappings == null)
	    return false;
	int index = 0;
	while(index < areaWrappings.length && !areaWrappings[index].hasArea(area))
	    ++index;
	if (index >= areaWrappings.length)
	    return false;
	activeAreaIndex = index;
	return true;
    }

    Area getFrontActiveArea()
    {
	if (activeAreaIndex < 0 || areaWrappings == null)
	    return null;
	return areaWrappings[activeAreaIndex].getFrontArea();
    }

    @Override public Area getCorrespondingFrontArea(Area area)
    {
	requireNonNull(area, "area can't be null");
	for(OpenedArea w: areaWrappings)
	    if (w.hasArea(area))
		return w.getFrontArea();
	return super.getCorrespondingFrontArea(area);
    }

    @Override public OpenedArea getAreaWrapping(Area area)
    {
	requireNonNull(area, "area can't be null");
	for(OpenedArea w: areaWrappings)
	    if (w.hasArea(area))
		return w;
	return super.getAreaWrapping(area);
    }

    AreaLayout getFrontAreaLayout()
    {
	final Area[] a = new Area[areas.length];
	for(int i = 0;i < areaWrappings.length;++i)
	    a[i] = areaWrappings[i].getFrontArea();
	return new AreaLayout(layoutType, a);
    }

    void sendBroadcastEvent(org.luwrain.core.events.SystemEvent event)
    {
	requireNonNull(event, "event can't be null");
	//	super.sendBroadcastEvent(event);
	for(OpenedArea w: areaWrappings)
	    w.getFrontArea().onSystemEvent(event);
    }
}
