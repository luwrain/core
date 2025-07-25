/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.util.*;
import org.apache.logging.log4j.*;

import org.luwrain.speech.*;

import static java.util.Objects.*;

public final class Speech
{
    static private final Logger log = LogManager.getLogger();
    static final int PITCH_HINT = -25;
    static final int PITCH_MESSAGE = -25;
    static private final String SPEECH_PREFIX = "--speech=";

    private final Args args;
    private final Configs configs;
    private final ExtensionsManager extensions;
    private final Map<String, Engine> engines = new HashMap<>();
    private Channel defaultChannel = null;
    private int pitch = 50;
    private int rate = 50;

    Speech(Args args, Configs configs, ExtensionsManager extensions)
    {
	this.args = requireNonNull(args, "args can't be null");
	this.configs = requireNonNull(configs, "configs can't be null");
	this.extensions = requireNonNull(extensions);
	this.pitch = 50;
	this.rate = 50;
    }

    void init()
    {
	final var engines = extensions.load(Engine.class);
	for(Engine e: engines)
	{
	    final String name = e.getExtObjName();
	    if (name == null || name.isEmpty())
	    {
		log.warn("The speech engine with empty name found, skipping it");
		continue;
	    }
	    if (this.engines.containsKey(name))
	    {
		log.warn("Two speech engines with the same name '" + name + "'");
		continue;
	    }
	    this.engines.put(name, e);
	    log.trace("Registered the speech engine '" + name + "'");
	}
	if (engines.isEmpty())
	{
	    log.warn("No speech engines, skipping speech output initialization");
	    defaultChannel = null;
	    return;
	}
	final String engineName;
	final var conf = configs.load(Config.class);
	if (args.speech != null && !args.speech.isEmpty())
	    engineName = args.speech; else
	    if (conf != null)
		engineName = conf.engineName; else
		engineName = null;
	    if (engineName == null)
	    {
log.warn("Speech engine not specified. Use '-s' command line option to set the desired engine name");
defaultChannel = null;
return;
	    }
	    final Map<String, String> params = new HashMap<>();
	    if (args.speechParams  != null && !args.speechParams.isEmpty())
	    {
		for(var p: args.speechParams)
		{
		    final var eq = p.indexOf("=");
		    if (eq > 0)
			params.put(p.substring(0, eq), p.substring(eq + 1)); else
			params.put(p, "");
		}
	    } else
	    if (conf != null && conf.params != null)
		params.putAll(conf.params);
	    log.trace("Loading speech engine '" + engineName + "' with params " + params.toString());
	this.defaultChannel = loadChannel(engineName, params);
	if (defaultChannel != null)
	    log.trace("Main speech engine is '" + engineName + "'"); else
	    log.error("Unable to load the default channel of the engine '" + engineName + "'");
    }

        public Channel loadChannel(String engineName, String paramsLine)
    {
	/*
	NullCheck.notEmpty(engineName, "engineName");
	NullCheck.notNull(paramsLine, "paramsLine");
	final Map<String, String> params = new HashMap<>();
	if (!parseParams(paramsLine, params))
	    return null;
	return loadChannel(engineName, params);
	*/
	return null;
    }

    private Channel loadChannel(String engineName, Map<String, String> params)
    {
	requireNonNull(engineName, "engineName");
	requireNonNull(params, "params");
	if (engineName.isEmpty())
	    throw new IllegalArgumentException("engineName can't be empty");
	if (!engines.containsKey(engineName))
	{
	    log.error("No such speech engine: '" + engineName + "'");
	    return null;
	}
	return engines.get(engineName).newChannel(params);
    }

    //Always cancels any previous text to speak
    public void speak(String text, int relPitch, int relRate)
    {
	NullCheck.notNull(text, "text");
	if (defaultChannel == null || text.isEmpty())
	    return;
	defaultChannel.speak(text, null, makePitch(relPitch), makeRate(relRate), true);
    }

    //Always cancels any previous text to speak
    public void speakEventResponse(String text)
    {
	NullCheck.notNull(text, "text");
	if (defaultChannel == null || text.isEmpty())
	    return;
	defaultChannel.speak(text, null, makePitch(0), makeRate(0), true);
    }

    //Always cancels any previous text to speak
    public void speakLetter(char letter, int relPitch, int relRate)
    {
	if (defaultChannel == null)
	    return;
	defaultChannel.speakLetter(letter, null, makePitch(relPitch), makeRate(relRate), true);
    }

    void silence()
    {
	if (defaultChannel == null)
	    return;
	defaultChannel.silence();
    }

    int getRate()
    {
	return rate;
    }

    void setRate(int value)
    {
	if (value < 0)
	    this.rate = 0; else
	    if (value > 100)
		this.rate = 100; else
		this.rate = value;
	//	sett.setRate(this.rate);
    }

    int getPitch()
    {
	return pitch;
    }

    void setPitch(int value)
    {
	if (value < 0)
	    this.pitch = 0; else
	    if (value > 100)
		this.pitch = 100; else
		this.pitch = value;
	//	sett.setPitch(this.pitch);
    }

    private int makePitch(int relPitch)
    {
	final int value = pitch + relPitch - 50;
	if (value < -50)
	    return -50;
	if (value > 50)
	    return 50;
	return value;
    }

    private int makeRate(int relRate)
    {
	final int value = rate + relRate - 50;
	if (value < -50)
	    return -50;
	if (value > 50)
	    return 50;
	return value;
    }

    static private final class Config
    {
	String engineName;
	Map<String, String> params;
    }
}
