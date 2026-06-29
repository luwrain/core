// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.websearch;

import java.util.*;
import lombok.*;

/**
 * The result of executing a web search query.
 *
 * <p>
 * A {@code Response} bundles the original {@link Query} together with the
 * list of {@link Entry} items that the search engine returned. The
 * {@link #query} field allows the caller to correlate this response with
 * the original request, which is particularly useful when multiple
 * queries are executed concurrently or when processing results asynchronously.
 * </p>
 *
 * <p>
 * The {@link #entries} list may be empty if the search did not produce
 * any matches. It is never {@code null}.
 * </p>
 *
 * @see Engine#search
 * @see Query
 * @see Entry
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Response
{
    /**
     * The original query for which this response was produced.
     * May be {@code null} if the response has been created without
     * a corresponding query (for instance, when deserializing).
     */
    private Query query;

    /**
     * The list of search result entries, in the order returned by the
     * search provider. An empty list means no results were found;
     * never {@code null}.
     */
    private List<Entry> entries;
}
