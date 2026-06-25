// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

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
