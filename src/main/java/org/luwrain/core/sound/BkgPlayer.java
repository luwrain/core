// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.sound;

import java.util.*;
import java.net.*;

import org.luwrain.core.*;
import org.luwrain.core.MediaResourcePlayer.*;

import static java.util.Objects.*;

final class BkgPlayer
{
    static private final String LOG_COMPONENT = "core";

    private final Luwrain luwrain;
    private final String url;
    private Instance instance = null;
    private List<MediaResourcePlayer> players = null; //FIXME: Inefficient store there

    BkgPlayer(Luwrain luwrain, String url)
    {
	this.luwrain = requireNonNull(luwrain, "luwrain can't be null");
	NullCheck.notEmpty(url, "url");
	this.url = url;
    }                                                                           

    void start()
    {
	if (players == null)
	    players = luwrain.createInstances(MediaResourcePlayer.class);
	MediaResourcePlayer player = null;
	if (players != null)
	    for(var p: players)
		if (p.getSupportedMimeType().equals(ContentTypes.SOUND_MP3_DEFAULT))
		{
		    player = p;
		    break;
		}
	if (player == null)
	{
	    Log.error(LOG_COMPONENT, "unable to find a media resource player for " + url.toString());
	    return;
	}
	this.instance = player.newMediaResourcePlayer(luwrain, new MediaResourcePlayer.Listener(){
		@Override public void onPlayerTime(Instance instance, long msec)
		{
		}
		@Override public void onPlayerFinish(Instance instance)
		{
		    play();
		}
		@Override public void onPlayerError(Exception e)
		{
		    Log.error(LOG_COMPONENT, "media resource player error for " + url.toString() + ": " + e.getClass().getName() + ":" + e.getMessage());
		}
	    });
	play();
    }

    synchronized void stopPlaying()
    {
	if (instance == null)
	    return;
	instance.stop();
	instance = null;
    }

    synchronized private void play()
    {
	if (this.instance == null)
	    return;
	try {
	    instance.stop();
	    instance.play(new URL(url), new MediaResourcePlayer.Params());
	}
	catch(Throwable e)
	{
	    Log.error(LOG_COMPONENT, "unable to start playing of " + url.toString() + ": " + e.getClass().getName() + ":" + e.getMessage());
	    e.printStackTrace();
	}
    }
}                                                                               
