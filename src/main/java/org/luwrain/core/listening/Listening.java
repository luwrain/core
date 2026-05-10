// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.listening;

import org.luwrain .core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.speech.*;
import org.luwrain.core.ListenableArea.ListeningInfo;

import static java.util.Objects.*;

public final class Listening
{
    private final Luwrain luwrain;
    private final Speech speech;
    private final Runnable completion;
    private final Settings.SpeechParams sett;
    private final Channel channel;
        private final Area area;
    private ListenableArea listenableArea = null;

    public Listening(Luwrain luwrain, Speech speech, Area area, Runnable completion)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	requireNonNull(speech, "speech can't be null");
	requireNonNull(area, "area can't be null");
	requireNonNull(completion, "completion can't be null");
	this.luwrain = luwrain;
	this.speech = speech;
	this.area = area;
	this.completion = completion;
	this.sett = null;//Settings.createSpeechParams(luwrain.getRegistry());
		if (sett.getListeningEngineName("").isEmpty())
		{
		    this.channel = null;
	    return;
		}
	channel = speech.loadChannel(sett.getListeningEngineName(""), sett.getListeningEngineParams(""));
		//channel.setDefaultRate(45);
	//channel.setDefaultPitch(30);
    }

    public boolean start()
    {
	if (channel == null)
	    return false;
	if (area instanceof ListenableArea)
	{
	    this.listenableArea = (ListenableArea)this.area;
	    final ListeningInfo info = listenableArea.onListeningStart();
	    if (info == null || info.noMore())
	    {
		this.listenableArea = null;
		this.channel.close();
		return false;
	    }
	speak(info);
	return true;
	}
	this.listenableArea = new CompatArea(area);
	ListeningInfo info = listenableArea.onListeningStart();
	if (info != null && !info.noMore())
	{
		speak(info);
		return true;
	}
	this.listenableArea = new PlainArea(area);
	info = listenableArea.onListeningStart();
	if (info != null && !info.noMore())
	{
		speak(info);
		return true;
	}
	this.listenableArea = null;
	this.channel .close();
	return false;
	    }

    public void cancel()
    {
	if (channel == null || listenableArea == null)
	    return;
	this.channel.silence();
	this.channel.close();
	this.listenableArea = null;
	this.completion.run();
    }

    private void onFinish(ListeningInfo listeningInfo)
    {
	requireNonNull(listeningInfo, "listeningInfo can't be null");
	if (listenableArea == null)
	    return;
	listenableArea.onListeningFinish(listeningInfo);
	final ListeningInfo nextInfo = listenableArea.onListeningStart();
	if (nextInfo == null || nextInfo.noMore())
	{
	    listenableArea = null;
	    channel.close();
	    this.completion.run();
	    return;
	}
	speak(nextInfo);
    }

    private void speak(ListeningInfo listeningInfo)
    {
	requireNonNull(listeningInfo, "listeningInfo can't be null");
	final Channel.Listener listener = (id)->luwrain.runUiSafely(()->onFinish(listeningInfo));
	channel .speak(luwrain.getSpeakableText(listeningInfo.getText(), Luwrain.SpeakableTextType.NATURAL), listener, sett.getListeningPitch(50) - 50, 50 - sett.getListeningRate(50), false);
    }
}
