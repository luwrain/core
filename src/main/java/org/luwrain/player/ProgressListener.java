// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

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
