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

/**
 * Provides the audio player subsystem for LUWRAIN.
 * <p>
 * This package defines interfaces and classes for playing audio content
 * such as music, podcasts, and audiobooks within the LUWRAIN environment.
 * The player operates in a separate thread, allowing applications to
 * initiate playback and continue functioning without blocking.
 * <p>
 * The central interface is {@link org.luwrain.player.Player}, which provides
 * complete control over playback: starting and stopping, pausing and resuming,
 * navigating between tracks, adjusting volume, and jumping to specified
 * positions. The player reports its state changes through
 * {@link org.luwrain.player.Listener listener} callbacks.
 * <p>
 * Audio content is organized in {@link org.luwrain.player.Playlist playlists},
 * which are ordered collections of tracks identified by URLs. The package
 * includes {@link org.luwrain.player.FixedPlaylist}, a simple immutable
 * playlist implementation suitable for most common use cases.
 * <p>
 * The actual audio playback engine is not included in this package;
 * instead, implementations of the {@link org.luwrain.player.Player}
 * interface are loaded at runtime through the
 * {@link org.luwrain.player.Factory} mechanism. The LUWRAIN core discovers
 * available player factories via its extension system and the Java
 * {@link java.util.ServiceLoader ServiceLoader}.
 * <p>
 * To obtain the current player instance, applications call
 * {@link org.luwrain.core.Luwrain#getPlayer() Luwrain.getPlayer()}.
 *
 * @see org.luwrain.player.Player
 * @see org.luwrain.player.Playlist
 * @see org.luwrain.player.Factory
 */
package org.luwrain.player;
