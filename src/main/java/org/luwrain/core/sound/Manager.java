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

package org.luwrain.core.sound;

import java.net.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;

import static java.util.Objects.*;

public final class Manager
{
    private final Configs configs;
    private final ExtObjects extObjs;
    private final Icons icons;
    private final Path soundsDir;
    private BkgPlayer bkgPlayer = null;
    private boolean startingMode = false;

    public Manager(ExtObjects extObjs, Luwrain luwrain, Configs configs, Path soundsDir)
    {

requireNonNull(luwrain, "luwrain can't be null");
this.extObjs = requireNonNull(extObjs, "");
this.configs = requireNonNull(configs, "configs can't be null");
	this.soundsDir = requireNonNull(soundsDir, "soundsDir can't be null");
	this.icons = new Icons(configs);
    }

    public void playIcon(Sounds sound)
    {
	if (sound == null)
	{
	    icons.stop();
	    return;
	}
	final String volumeStr = "100";//FIXME:
	int volume = 100;
	try {
	    if (!volumeStr.trim().isEmpty())
		volume = Integer.parseInt(volumeStr);
	}
	catch(NumberFormatException e)
	{
	    volume = 100;
	}
	volume = Math.max(volume, 0);
	volume = Math.min(volume, 1100);
	icons.play(sound, volume);
    }

    public void playIcon(File file)
    {
	requireNonNull(file, "file can't be null");
	final String volumeStr = "50";//FIXME:
	int volume = 100;
	try {
	    if (!volumeStr.trim().isEmpty())
		volume = Integer.parseInt(volumeStr);
	}
	catch(NumberFormatException e)
	{
	    volume = 100;
	}
	if (volume < 0)
	    volume = 0;
	if (volume > 100)
	    volume = 100;
	icons.play(file, volume);
    }

    public void cancelIcon()
    {
	icons.stop();
    }

    public void playBackground(BkgSounds sound)
    {
	requireNonNull(sound, "sound can't be null");
		stopBackground();
		final var conf = configs.load(Config.class);
		if (conf == null || conf.bkg == null || !conf.bkg.containsKey(sound))
		    return;
		this.bkgPlayer = new BkgPlayer(this.extObjs, conf.bkg.get(sound));
	this.bkgPlayer.start();
    }


        public void playBackground(String sound)
    {
	requireNonNull(sound, "sound can't be null");
		stopBackground();
		if (sound.isEmpty())
		    return;
		this.bkgPlayer = new BkgPlayer(this.extObjs, sound);
	this.bkgPlayer.start();
    }

    public void stopBackground()
    {
	if (startingMode)
	    return;
	if (bkgPlayer != null)
	    bkgPlayer.stopPlaying();
	bkgPlayer = null;
    }

    public void startingMode()
    {
	if (startingMode)
	    return;
	playBackground(BkgSounds.STARTING);
	startingMode = true;
    }

    public void stopStartingMode()
    {
	if (!startingMode)
	    return;
	startingMode = false;
	stopBackground();
    }

    private String getFileUrl(String fileName)
    {
	NullCheck.notNull(fileName, "fileName");
	if (fileName.isEmpty())
	    return "";
	Path path = Paths.get(fileName);
	if (!path.isAbsolute())
	    path = soundsDir.resolve(path);
	try {
	    return path.toUri().toURL().toString();
	}
	catch(MalformedURLException e)
	{
	    Log.warning("core", "unable to construct sound file URL using string \'" + fileName + "\'");
	    return fileName;
	}
    }
}
