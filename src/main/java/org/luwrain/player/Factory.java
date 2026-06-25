// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.player;

import org.luwrain.core.*;

/**
 * Factory interface for creating {@link Player} instances.
 * <p>
 * Implementations of this interface are responsible for constructing
 * concrete audio players. The LUWRAIN core discovers available factories
 * at startup through its extension system and the Java
 * {@link java.util.ServiceLoader ServiceLoader}. The first successfully
 * loaded factory is used to create the system-wide player instance.
 * <p>
 * Factories must be registered as LUWRAIN extensions or as services in
 * {@code META-INF/services/org.luwrain.player.Factory}.
 *
 * @see Player
 * @see Params
 */
public interface Factory
{
    /**
     * Parameters passed to a factory when creating a new player.
     */
    static public final class Params
    {
	/**
	 * The {@link Luwrain} interface providing access to the LUWRAIN
	 * environment. The factory may use it to interact with the core,
	 * for example to access configuration or play system sounds.
	 */
	public Luwrain luwrain = null;
    }

    /**
     * Creates a new {@link Player} instance.
     * <p>
     * The returned player is expected to be fully initialized and ready
     * to accept playback requests. Implementations may return {@code null}
     * to indicate that the player cannot be created; in this case the
     * LUWRAIN core will log an error and continue without a player.
     *
     * @param params The parameters for player creation; must not be {@code null}
     * @return A new {@link Player} instance, or {@code null} if creation failed
     */
    Player newPlayer(Params params);
}
