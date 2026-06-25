// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.player;

import java.util.*;

/**
 * The main audio player interface in LUWRAIN.
 * <p>
 * {@code Player} provides complete control over audio playback. All playback
 * operations happen in a separate background thread: calling methods like
 * {@link #play(Playlist, int, long, Set)} returns immediately, while the
 * actual playback proceeds concurrently. The player notifies registered
 * {@link Listener listeners} about state changes, track switches, time
 * progress, and errors.
 * <p>
 * A player instance always has a current {@link Playlist playlist} (once
 * playback has started at least once) and a current track number within it.
 * Tracks are identified by zero-based indices. The player supports
 * navigation (next/previous track, jump to a specific track), seeking
 * within the current track, and volume control.
 * <p>
 * The volume range is defined by {@link #MIN_VOLUME} and
 * {@link #MAX_VOLUME}. The default set of flags is an empty set (see
 * {@link #DEFAULT_FLAGS}).
 * <p>
 * A player instance is obtained through the {@link Factory} interface, which
 * is discovered by the LUWRAIN core at startup. Applications retrieve the
 * current player via {@link org.luwrain.core.Luwrain#getPlayer()
 * Luwrain.getPlayer()}.
 *
 * @see Playlist
 * @see FixedPlaylist
 * @see Listener
 * @see Factory
 */
public interface Player
{
    /** Minimum allowed volume level. */
    static public final int
    MIN_VOLUME = 0,
    /** Maximum allowed volume level. */
    MAX_VOLUME = 100;

    /** The name used to register the player as a shared object in the LUWRAIN object registry. */
    static public final String SHARED_OBJECT_NAME = "luwrain.player";

    /** The default, empty set of playback flags. */
    static public final Set<Flags> DEFAULT_FLAGS = EnumSet.noneOf(Flags.class);

    /**
     * Flags that modify playback behaviour.
     */
    public enum Flags
    {
	/** Repeat the playlist indefinitely after the last track finishes. */
	CYCLED,
	/** Shuffle tracks into a random order for playback. */
	RANDOM,
	/** The audio source is a continuous stream rather than a finite file. */
	STREAMING
    };

    /**
     * Result codes returned by playback-initiating methods.
     */
    public enum Result
    {
	/** Playback started successfully. */
	OK,
	/** The playlist is invalid (e.g. empty or containing malformed URLs). */
	INVALID_PLAYLIST,
	/** The audio format of the starting track is not supported. */
	UNSUPPORTED_FORMAT_STARTING_TRACK,
	/** The audio source cannot be accessed (network error, file not found, etc.). */
	INACCESSIBLE_SOURCE,
	/** An unspecified internal player error occurred. */
	GENERAL_PLAYER_ERROR
    };

    /**
     * Represents the current playback state of the player.
     */
    public enum State
    {
	/** The player is loading or buffering audio data. */
	LOADING,
	/** Audio is actively playing. */
	PLAYING,
	/** Playback is paused and can be resumed. */
	PAUSED,
	/** Playback is stopped; no audio is loaded. */
	STOPPED
    };

    /**
     * Starts playing the specified playlist. This method acts in a separate
     * thread and returns execution control immediately. If there was a
     * previous playback session, it will be silently cancelled. You may
     * specify the desired track number and a position in the audio file to
     * begin playing from.
     *
     * @param playlist The playlist to play; must not be {@code null}
     * @param startingTrackNum The zero-based index of the track to start from
     * @param startingPosMsec The position in milliseconds within the starting track to begin playback from
     * @param flags A set of {@link Flags} controlling playback behaviour; must not be {@code null}
     * @return A {@link Result} code indicating whether playback started successfully
     */
    Result play(Playlist playlist, int startingTrackNum, long startingPosMsec, Set<Flags> flags);

    /**
     * Stops the current playback. After stopping, the player transitions to
     * the {@link State#STOPPED STOPPED} state.
     *
     * @return {@code true} if the operation was successful, {@code false} otherwise
     */
    boolean stop();

    /**
     * Toggles between paused and playing states. If the player is currently
     * {@link State#PLAYING PLAYING}, it pauses; if {@link State#PAUSED PAUSED},
     * it resumes playback.
     *
     * @return {@code true} if the operation was successful, {@code false} otherwise
     */
    boolean pauseResume();

    /**
     * Jumps forward or backward in the current track by the specified offset.
     *
     * @param offsetMsec The offset in milliseconds; positive values jump forward,
     *                   negative values jump backward
     * @return {@code true} if the jump was successful, {@code false} otherwise
     */
    boolean jump(long offsetMsec);

    /**
     * Switches to the next track in the playlist.
     *
     * @return {@code true} if the switch was successful, {@code false} if there is
     *         no next track or an error occurred
     */
    boolean nextTrack();

    /**
     * Switches to the previous track in the playlist.
     *
     * @return {@code true} if the switch was successful, {@code false} if there is
     *         no previous track or an error occurred
     */
    boolean prevTrack();

    /**
     * Jumps to the specified track in the playlist.
     *
     * @param trackIndex The zero-based index of the track to play
     * @return {@code true} if the track was started successfully, {@code false} otherwise
     */
    boolean playTrack(int trackIndex);

    /**
     * Returns the current playback state.
     *
     * @return The current {@link State} of the player
     */
    State getState();

    /**
     * Checks whether a playlist is currently loaded.
     *
     * @return {@code true} if the player has an associated playlist, {@code false} otherwise
     */
    boolean hasPlaylist();

    /**
     * Returns the currently loaded playlist.
     *
     * @return The current {@link Playlist}, or {@code null} if no playlist has been loaded
     */
    Playlist getPlaylist();

    /**
     * Returns the zero-based index of the currently playing track.
     *
     * @return The current track number
     */
    int getTrackNum();

    /**
     * Returns the current volume level.
     *
     * @return The volume level, between {@link #MIN_VOLUME} and {@link #MAX_VOLUME}
     */
    int getVolume();

    /**
     * Sets the volume level.
     *
     * @param volume The desired volume level, must be between {@link #MIN_VOLUME}
     *               and {@link #MAX_VOLUME}
     */
    void setVolume(int volume);

    /**
     * Registers a listener to receive player events.
     *
     * @param listener The listener to add; must not be {@code null}
     */
    void addListener(Listener listener);

    /**
     * Unregisters a previously added listener.
     *
     * @param listener The listener to remove; must not be {@code null}
     */
    void removeListener(Listener listener);

    /**
     * Returns the current set of playback flags.
     *
     * @return An unmodifiable set of active {@link Flags}
     */
    Set<Flags> getFlags();
}
