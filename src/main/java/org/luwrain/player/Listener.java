// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.player;

/**
 * Callback interface for receiving player events.
 * <p>
 * Register implementations of this interface with {@link Player#addListener(Listener)}
 * to be notified about changes in playback state, track switches, time
 * progress updates, and errors. All listener methods are called from the
 * player's internal thread, so implementations must ensure proper thread
 * safety if they interact with UI or other shared resources.
 * <p>
 * A typical use of this listener is to update a user interface element
 * showing the current track name and elapsed time, or to react to
 * playback errors.
 *
 * @see Player
 * @see Player#addListener(Listener)
 * @see Player#removeListener(Listener)
 */
public interface Listener
{
    /**
     * Called when a new playlist has been loaded.
     *
     * @param playlist The newly loaded playlist; never {@code null}
     */
    void onNewPlaylist(Playlist playlist);

    /**
     * Called when playback switches to a new track.
     *
     * @param playlist The current playlist; never {@code null}
     * @param trackNum The zero-based index of the new track
     */
    void onNewTrack(Playlist playlist, int trackNum);

    /**
     * Called periodically to report the current playback position.
     * <p>
     * The frequency of these updates is implementation-specific but is
     * typically around once per second.
     *
     * @param playlist The current playlist; never {@code null}
     * @param trackNum The zero-based index of the current track
     * @param msec The elapsed time within the current track, in milliseconds
     */
    void onTrackTime(Playlist playlist, int trackNum, long msec);

    /**
     * Called when the player state changes (e.g. from playing to paused).
     *
     * @param playlist The current playlist; never {@code null}
     * @param state The new {@link Player.State}
     */
    void onNewState(Playlist playlist, Player.State state);

    /**
     * Called when an error occurs during playback.
     * <p>
     * The player remains in its current state and does not automatically
     * advance to the next track on error; the application should decide
     * how to handle the situation based on the exception details.
     *
     * @param playlist The current playlist, or {@code null} if the error occurred before a playlist was set
     * @param e The exception describing the error; never {@code null}
     */
    void onPlayingError(Playlist playlist, Exception e);
}
