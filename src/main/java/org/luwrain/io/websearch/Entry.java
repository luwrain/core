// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.websearch;

import java.util.*;
import lombok.*;

/**
 * A single result item returned by a web search engine.
 *
 * <p>
 * Each entry represents one search result — typically a link to a web
 * page, document, or other resource that matched the original
 * {@link Query}. It carries the most important metadata that is common
 * across search providers:
 * </p>
 *
 * <ul>
 *   <li>{@link #title} — a short human-readable title of the result, as
 *       returned by the provider. May be empty but never {@code null}.</li>
 *   <li>{@link #snippet} — a brief fragment of text from the result that
 *       includes or highlights the matched query terms. May be empty if
 *       the provider does not supply snippets.</li>
 *   <li>{@link #displayUrl} — the URL suitable for displaying to the user
 *       (often a shortened or canonical form).</li>
 *   <li>{@link #clickUrl} — the URL that should be followed when the user
 *       activates this result. This may differ from {@link #displayUrl}
 *       if the search provider uses redirect tracking.</li>
 *   <li>{@link #providers} — an optional list of provider-specific tags
 *       or additional metadata (e.g. {@code "video"}, {@code "recipe"},
 *       {@code "news"}).</li>
 * </ul>
 *
 * @see Response
 * @see Query
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Entry
{
    /**
     * The title of this search result. Typically displayed as the main
     * clickable link text. May be empty, never {@code null}.
     */
    private String title;

    /**
     * A brief text excerpt from the result document that may include the
     * matched search terms. May be empty or {@code null} if not available.
     */
    private String snippet;

    /**
     * The URL shown to the user for this result. This is usually a
     * human-friendly or canonical form of the target address.
     */
    private String displayUrl;

    /**
     * The actual URL to follow when the user opens this result. May be
     * identical to {@link #displayUrl} or differ (e.g. including tracking
     * parameters or redirect services).
     */
    private String clickUrl;

    /**
     * Optional provider-specific tags describing the nature of this
     * result (e.g. {@code "video"}, {@code "recipe"}, {@code "news"}).
     * May be empty or {@code null}.
     */
    private List<String> providers;
}
