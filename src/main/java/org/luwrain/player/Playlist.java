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

import java.util.*;

import org.luwrain.core.*;

/**
 * Represents an ordered collection of audio tracks.
 * <p>
 * A playlist supplies the player with track URLs to play. Each track is
 * identified by a zero-based index, and the playlist reports the total
 * number of tracks via {@link #getTrackCount()}. Individual track URLs
 * are obtained through {@link #getTrackUrl(int)}.
 * <p>
 * This interface extends both {@link VolumeListener} and
 * {@link ProgressListener}, meaning the playlist itself can receive
 * notifications about volume changes and playback progress. This enables
 * playlist implementations to store and forward these values, as
 * demonstrated by {@link FixedPlaylist}.
 * <p>
 * Implementations must ensure that {@link #getTrackUrl(int)} returns a
 * valid, playable URL for every index from 0 to {@code getTrackCount() - 1}.
 *
 * @see FixedPlaylist
 * @see Player
 * @see VolumeListener
 * @see ProgressListener
 */
public interface Playlist extends VolumeListener, ProgressListener
{
    /**
     * Returns the total number of tracks in this playlist.
     *
     * @return The number of tracks, always a non-negative integer
     */
    int getTrackCount();

    /**
     * Returns the URL of the track at the specified index.
     *
     * @param index The zero-based track index
     * @return The URL of the track, never {@code null}
     * @throws IndexOutOfBoundsException if {@code index} is negative or
     *         not less than {@link #getTrackCount()}
     */
    String getTrackUrl(int index);

    /**
     * Returns the current volume level associated with this playlist.
     *
     * @return The volume level, between {@link Player#MIN_VOLUME} and
     *         {@link Player#MAX_VOLUME}
     */
    int getVolume();
}
