/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 1.0

package org.luwrain.player;

/**
 * Callback interface for receiving volume change notifications.
 * <p>
 * Implementations of this interface are notified whenever the volume
 * level changes. This listener is also extended by {@link Playlist},
 * enabling playlists to store and forward volume updates to their own
 * listeners.
 *
 * @see Playlist
 * @see ProgressListener
 */
public interface VolumeListener
{
    /**
     * Called when the volume level has changed.
     *
     * @param newVolumeLevel The new volume level, guaranteed to be between
     *        {@link Player#MIN_VOLUME} and {@link Player#MAX_VOLUME}
     */
    void onNewVolume(int newVolumeLevel);
}
