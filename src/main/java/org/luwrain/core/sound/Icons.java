// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.sound;

import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;
import org.apache.commons.io.*;

import org.luwrain.core.*;

import static java.util.Objects.*;
import static org.luwrain.util.StreamUtils.*;
import static org.luwrain.core.NullCheck.*;

final class Icons
{
    static private final Logger log = LogManager.getLogger();

    private final Configs configs;
    private final Map<Sounds, String> soundFiles = new HashMap<>();
        private final Map<Sounds, byte[]> cache = new HashMap<>();
    private WavePlayers.Simple previous = null;

    Icons(Configs configs)
    {
	this.configs = requireNonNull(configs, "configs can't be null");
    }

    void load()
    {
	cache.clear();
	soundFiles.clear();
	final var conf = configs.load(Config.class);
	if (conf != null && conf.icons != null)
	    for(var e: conf.icons.entrySet())
		soundFiles.put(e.getKey(), e.getValue());
    }

    void play(Sounds sound, int volumePercent)
    {
requireNonNull(sound, "sound can't be null");
	final File soundFile;
	if (!soundFiles.containsKey(sound))
	{
	    loadToCacheFromResource(sound);
	}

			if (cache.containsKey(sound))
		{
			    	if (previous != null)
	    previous.stopPlaying();
		previous = new WavePlayers.Simple(new ByteArrayInputStream(cache.get(sound)), volumePercent);
	new Thread(previous).start();
		} 

			    
	/*
	    soundFile = getSoundFile(sound);
	    if (soundFile == null)
	    {
		//Trying to load the sound file from resources
		loadToCache(sound);
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

        void play(File file, int volumePercent)
    {
requireNonNull(file, "file can't be null");
	if (previous != null)
	    previous.stopPlaying();
	previous = new WavePlayers.Simple(file.getAbsolutePath(), volumePercent);
	new Thread(previous).start();
    }

    void stop()
    {
	if (previous != null)
	    previous.stopPlaying();
    }

    private void loadToCacheFromResource(Sounds sound)
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
		    IOUtils.copy(is, os);
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
}
