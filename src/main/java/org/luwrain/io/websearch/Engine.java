// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.websearch;

import java.io.*;
import org.luwrain.core.*;

/**
 * Contract for a web search engine implementation.
 *
 * <p>
 * An {@code Engine} abstracts all network communication with a particular
 * remote search service. Implementations are expected to translate a
 * generic {@link Query} into the provider-specific request format, parse
 * the provider's response, and return a normalized {@link Response}
 * object containing structured search results.
 * </p>
 *
 * <p>
 * The {@link #search(Luwrain, Query)} method is the sole entry point.
 * It receives the LUWRAIN environment handle (in case the implementation
 * needs access to configuration, network facilities, or user settings)
 * together with the query to be executed. It returns a populated
 * {@link Response} or throws {@link IOException} if the network request
 * fails or the response cannot be parsed.
 * </p>
 *
 * <p>
 * Implementations are typically provided as LUWRAIN extensions and
 * registered through the {@code ServiceLoader} mechanism, so that
 * applications can discover them without hardcoding specific search
 * backends.
 * </p>
 *
 * @see Query
 * @see Response
 * @see Entry
 */
public interface Engine
{
    /**
     * Executes the specified search query and returns the retrieved results.
     *
     * @param luwrain The LUWRAIN environment handle; provides access to
     *                network, configuration, and user settings
     * @param query   The search query to execute; must not be {@code null}
     *
     * @return A {@link Response} containing the search results;
     *         never {@code null}
     *
     * @throws IOException If a network error occurs or the remote
     *                     response cannot be retrieved or parsed
     */
    Response search(Luwrain luwrain, Query query) throws IOException;
}
