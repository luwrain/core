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
import java.net.*;

import org.luwrain.core.*;

/**
 * An immutable implementation of {@link Playlist} backed by an array of URLs.
 * <p>
 * {@code FixedPlaylist} stores a fixed list of track URLs that cannot be
 * modified after construction. All URLs are validated at construction time:
 * if any of them cannot be parsed as a valid {@link URL}, the constructor
 * throws {@link IllegalArgumentException}.
 * <p>
 * This class also implements {@link VolumeListener#onNewVolume(int)} and
 * {@link ProgressListener#onProgress(int, long)} by storing the received
 * values and optionally forwarding them to delegate listeners provided at
 * construction time.
 * <p>
 * Multiple constructors are provided for convenience, allowing the caller
 * to omit optional listeners and use the default volume. For a single-track
 * playlist, the convenience constructor {@link #FixedPlaylist(String)}
 * wraps the URL in a single-element array.
 * <p>
 * Example usage:
 * <pre>{@code
 * Playlist playlist = new FixedPlaylist(new String[]{
 *     "https://example.org/song1.mp3",
 *     "https://example.org/song2.mp3"
 * }, progressListener, volumeListener, 80);
 * }</pre>
 *
 * @see Playlist
 * @see Player
 */
public final class FixedPlaylist implements Playlist
{
    private final String[] urls;
    private final VolumeListener volumeListener;
    private final ProgressListener progressListener;
    private int volume = Player.MAX_VOLUME;

    /**
     * Creates a new fixed playlist with the specified tracks, listeners, and volume.
     * <p>
     * Every element of {@code urls} is validated by attempting to construct a
     * {@link URL} from it. If any URL is malformed, {@link IllegalArgumentException}
     * is thrown.
     *
     * @param urls An array of track URLs; must not be {@code null} and must
     *             not contain {@code null} elements
     * @param progressListener An optional delegate for progress notifications;
     *                         may be {@code null}
     * @param volumeListener An optional delegate for volume notifications;
     *                       may be {@code null}
     * @param volume The initial volume level; will be clamped to the range
     *               {@link Player#MIN_VOLUME} to {@link Player#MAX_VOLUME}
     * @throws IllegalArgumentException if any element of {@code urls} is not a valid URL
     * @throws NullPointerException if {@code urls} is {@code null} or contains {@code null} elements
     */
    public FixedPlaylist(String[] urls, ProgressListener progressListener, VolumeListener volumeListener, int volume)
    {
	NullCheck.notNullItems(urls, "urls");
	this.volumeListener = volumeListener;
	this.progressListener = progressListener;
	this.volume = Math.min(Math.max(volume, Player.MIN_VOLUME), Player.MAX_VOLUME);
	this.urls = new String[urls.length];
	for(int i = 0;i < urls.length;i++)
	{
	    final URL u;
	    try {
		u = new URL(urls[i]);
	    }
	    catch(MalformedURLException e)
	    {
		throw new IllegalArgumentException(e);
	    }
	    this.urls[i] = u.toString();
	}
    }

    /**
     * Creates a new fixed playlist with the specified tracks, volume listener, and volume.
     * No progress listener is set.
     *
     * @param urls An array of track URLs; must not be {@code null} and must
     *             not contain {@code null} elements
     * @param volumeListener An optional delegate for volume notifications;
     *                       may be {@code null}
     * @param volume The initial volume level; will be clamped
     * @throws IllegalArgumentException if any element of {@code urls} is not a valid URL
     */
    public FixedPlaylist(String[] urls, VolumeListener volumeListener, int volume)
    {
	this(urls, null, volumeListener, Player.MAX_VOLUME);
    }

    /**
     * Creates a new fixed playlist with the specified tracks.
     * No listeners are set and the default volume is used.
     *
     * @param urls An array of track URLs; must not be {@code null} and must
     *             not contain {@code null} elements
     * @throws IllegalArgumentException if any element of {@code urls} is not a valid URL
     */
    public FixedPlaylist(String[] urls)
    {
	this(urls, null, Player.MAX_VOLUME);
    }

    /**
     * Convenience constructor for a single-track playlist.
     *
     * @param url The URL of the single track; must be a valid URL
     * @throws IllegalArgumentException if {@code url} is not a valid URL
     */
    public FixedPlaylist(String url)
    {
	this(new String[]{url});
    }

    /**
     * Returns a copy of the internal array of track URLs.
     * <p>
     * This method returns a defensive copy, so modifications to the returned
     * array do not affect this playlist.
     *
     * @return A new array containing all track URLs
     */
    public String[] getAllTracks()
    {
	return urls.clone();
    }

    /**
     * Returns the total number of tracks.
     *
     * @return The number of tracks in this playlist
     */
    @Override public int getTrackCount()
    {
	return urls.length;
    }

    /**
     * Returns the URL of the track at the specified index.
     *
     * @param index The zero-based track index
     * @return The URL of the track
     * @throws ArrayIndexOutOfBoundsException if {@code index} is out of bounds
     */
    @Override public String getTrackUrl(int index)
    {
	return urls[index];
    }

    /**
     * Returns the current volume level stored in this playlist.
     *
     * @return The volume level
     */
    @Override public int getVolume()
    {
	return this.volume;
    }

    /**
     * Updates the stored volume and forwards the notification to the
     * delegate volume listener, if one is set.
     *
     * @param newVolumeLevel The new volume level
     */
    @Override public void onNewVolume(int newVolumeLevel)
    {
	this.volume = newVolumeLevel;
	if (volumeListener != null)
	    volumeListener.onNewVolume(volume);
    }

    /**
     * Forwards the progress notification to the delegate progress listener,
     * if one is set.
     *
     * @param trackIndex The zero-based index of the current track
     * @param timeMsec The elapsed time in milliseconds
     */
    @Override public void onProgress(int trackIndex, long timeMsec)
    {
	if (progressListener != null)
	    progressListener.onProgress(trackIndex, timeMsec);
    }
}
