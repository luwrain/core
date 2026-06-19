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
 * Callback interface for receiving playback progress updates.
 * <p>
 * This listener is invoked periodically by the player to report how
 * much of the current track has been played. The update frequency is
 * implementation-dependent but should be frequent enough to support
 * reasonable UI updates (e.g. once per second).
 * <p>
 * This interface is also extended by {@link Playlist}, allowing playlists
 * to serve as intermediate receivers that forward progress events to
 * their own listeners.
 *
 * @see Playlist
 * @see VolumeListener
 */
public interface ProgressListener
{
    /**
     * Called to report the current playback progress.
     *
     * @param trackIndex The zero-based index of the current track
     * @param timeMsec The elapsed time within the current track, in milliseconds
     */
    void onProgress(int trackIndex, long timeMsec);
}
