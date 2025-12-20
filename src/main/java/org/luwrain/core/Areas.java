// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;

import static org.luwrain.core.NullCheck.*;

abstract class Areas extends Base
{
        protected final AppManager apps;
    protected final TilesManager tiles;
    protected final WindowManager windowManager;

    protected Areas(Config conf)
    {
	super(conf);
	this.apps = new AppManager();
	this.tiles = new TilesManager(apps);
	this.windowManager = new WindowManager(conf.getInteraction(), tiles);
    }

    //        abstract Area getActiveArea(boolean speakMessages);

    void onNewAreasLayout()
    {
	tiles.updatePopupState();
	windowManager.redraw();
	updateBackgroundSound(null);
    }

    protected void updateBackgroundSound(Area updateFor)
    {
	final Area area = tiles.getActiveArea();
	//The requested area isnt active, we are doing nothing
	if (updateFor != null && area != updateFor)
	    return;
	if (area != null)
	{
	    final BackgroundSoundQuery query = new BackgroundSoundQuery();
	    if (AreaQuery.ask(area, query))
	    {
		final BackgroundSoundQuery.Answer answer = query.getAnswer();
		if (answer.isUrl())
		    soundManager.playBackground(answer.getUrl()); else
		    soundManager.playBackground(answer.getBkgSound()); 
		return;
	    }
	    if (updateFor != null)
	    {
	    soundManager.stopBackground();
	    return;
	    }
	}
	//General update, checking only for popups
	if (tiles.isPopupActive())
	    soundManager.playBackground(BkgSounds.POPUP); else
	    soundManager.stopBackground();
    }

    //Instance is not mandatory but can increase speed of search
    Area getFrontAreaFor(Luwrain instance, Area area)
    {
	Area effectiveArea = null;
	if (instance != null)
	{
	    final Application app = interfaces.findApp(instance);
	    if (app != null && apps.isAppLaunched(app))
		effectiveArea = apps.getCorrespondingEffectiveArea(app, area);
	}
	//No provided instance or it didn't help
	if (effectiveArea == null)
	    effectiveArea = apps.getCorrespondingEffectiveArea(area);
	return effectiveArea;
    }
}
