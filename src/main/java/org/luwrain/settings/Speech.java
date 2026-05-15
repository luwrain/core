// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.settings;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import static java.util.Objects.*;

final class Speech extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    private org.luwrain.io.json.Speech params;

    Speech(ControlPanel controlPanel, String name)
    {
	super(new DefaultControlContext(controlPanel.getCoreInterface()), name);
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	this.params = requireNonNullElse(luwrain.loadConf(org.luwrain.io.json.Speech.class), new org.luwrain.io.json.Speech());
	addEdit("main-engine-name", luwrain.i18n().getStaticStr("CpSpeechMainEngineName"), requireNonNullElse(params.getMainEngineName(), ""));
	addEdit("main-engine-params", luwrain.i18n().getStaticStr("CpSpeechMainEngineParams"), requireNonNullElse(params.getMainEngineParams(), ""));
	addEdit("listening-engine-name", luwrain.i18n().getStaticStr("CpSpeechListeningEngineName"), requireNonNullElse(params.getListeningEngineName(), ""));
	addEdit("listening-engine-params", luwrain.i18n().getStaticStr("CpSpeechListeningEngineParams"), requireNonNullElse(params.getListeningEngineParams(), ""));
	addEdit("listening-pitch", luwrain.i18n().getStaticStr("CpSpeechListeningPitch"), "" + params.getListeningPitch());
	addEdit("listening-rate", luwrain.i18n().getStaticStr("CpSpeechListeningRate"), "" + params.getListeningRate());
    }

    @Override public boolean saveSectionData()
    {
	params.setMainEngineName(getEnteredText("main-engine-name"));
	params.setMainEngineParams(getEnteredText("main-engine-params"));
	params.setListeningEngineName(getEnteredText("listening-engine-name"));
	params.setListeningEngineParams(getEnteredText("listening-engine-params"));
	final int listeningPitch;
	try {
	    listeningPitch = Integer.parseInt(getEnteredText("listening-pitch"));
	}
	catch(NumberFormatException e)
	{
	    luwrain.message(luwrain.i18n().getStaticStr("CpSpeechInvalidListeningPitch"), Luwrain.MessageType.ERROR);
	    return false;
	}
	if (listeningPitch < 0 || listeningPitch > 100)
	{
	    luwrain.message(luwrain.i18n().getStaticStr("CpSpeechInvalidListeningPitch"), Luwrain.MessageType.ERROR);
	    return false;
	}
	params.setListeningPitch(listeningPitch);
	final int listeningRate;
	try {
	    listeningRate = Integer.parseInt(getEnteredText("listening-rate"));
	}
	catch(NumberFormatException e)
	{
	    luwrain.message(luwrain.i18n().getStaticStr("CpSpeechInvalidListeningRate"), Luwrain.MessageType.ERROR);
	    return false;
	}
	if (listeningRate < 0 || listeningRate > 100)
	{
	    luwrain.message(luwrain.i18n().getStaticStr("CpSpeechInvalidListeningRate"), Luwrain.MessageType.ERROR);
	    return false;
	}
	params.setListeningRate(listeningRate);
	luwrain.saveConf(params);
	return true;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	if (controlPanel.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	if (controlPanel.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    static Speech create(ControlPanel controlPanel)
    {
	requireNonNull(controlPanel, "controlPanel can't be null");
	final Luwrain luwrain = controlPanel.getCoreInterface();
	return new Speech(controlPanel, luwrain.i18n().getStaticStr("CpSpeechGeneral"));
    }
}
