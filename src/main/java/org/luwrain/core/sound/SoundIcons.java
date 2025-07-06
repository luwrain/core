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

import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;

import org.luwrain.core.*;

import static java.util.Objects.*;
import static org.luwrain.util.StreamUtils.*;
import static org.luwrain.core.NullCheck.*;

public final class SoundIcons
{
    static private final Logger log = LogManager.getLogger();

    private final Configs configs;
    private final Map<Sounds, String> soundFiles = new HashMap<>();
        private final Map<Sounds, byte[]> cache = new HashMap<>();
    private WavePlayers.Simple previous = null;

    public SoundIcons(Configs configs)
    {
	this.configs = requireNonNull(configs, "configs can't be null");
    }

    public void load()
    {
	cache.clear();
	soundFiles.clear();
	final var conf = configs.load(Config.class);
	if (conf != null && conf.icons != null)
	    for(var e: conf.icons.entrySet())
		soundFiles.put(e.getKey(), e.getValue());
    }

    public void play(Sounds sound, int volumePercent)
    {
requireNonNull(sound, "sound can't be null");
/*
	final File soundFile;
	if (!soundFiles.containsKey(sound))
	{
	    soundFile = getSoundFile(sound);
	    if (soundFile == null)
	    {
		//Trying to load the sound file from resources
		loadToCache(sound);
		if (cache.containsKey(sound))
		{
			    	if (previous != null)
	    previous.stopPlaying();
		previous = new WavePlayers.Simple(new ByteArrayInputStream(cache.get(sound)), volumePercent);
	new Thread(previous).start();
		} else
		log.error("No sound file specified for Sounds." + sound.toString());
		return;
	    }
	    soundFiles.put(sound, soundFile.getAbsolutePath());
	} else
	    soundFile = new File(soundFiles.get(sound));
	if (previous != null)
	    previous.stopPlaying();
	previous = new WavePlayers.Simple(soundFile.getAbsolutePath(), volumePercent);
	new Thread(previous).start();
*/
    }

        public void play(File file, int volumePercent)
    {
requireNonNull(file, "file can't be null");
	if (previous != null)
	    previous.stopPlaying();
	previous = new WavePlayers.Simple(file.getAbsolutePath(), volumePercent);
	new Thread(previous).start();
    }

    public void stop()
    {
	if (previous != null)
	    previous.stopPlaying();
    }

    private void loadToCache(Sounds sound)
    {
requireNonNull(sound, "sound can't benull");
	if (cache.containsKey(sound))
	    return;
		final String name = sound.toString().toLowerCase().replaceAll("_", "-") + ".wav";
		final var s = getClass().getResourceAsStream(name);
		if (s == null)
		    return;
		final var os = new ByteArrayOutputStream();
		try {
		try (final var is = new BufferedInputStream(s)){
		    copyAllBytes(is, os);
		}
		os.flush();
		}
		catch(IOException ex)
		{
		    log.error("Unable to load a sound resource file for " + sound.toString(), ex);
		    return;
		}
		cache.put(sound, os.toByteArray());
    }

    static final class Config
    {
	Map<Sounds, String> icons;
    }
}
