// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

/**
 * Provides an abstract interface for internet search engines.
 *
 * <p>
 * This package abstracts the process of sending search queries to remote
 * web search services and receiving their results. It defines a common
 * data model for representing queries ({@link org.luwrain.io.websearch.Query}),
 * individual search result entries ({@link org.luwrain.io.websearch.Entry}),
 * and the overall response ({@link org.luwrain.io.websearch.Response}).
 * </p>
 *
 * <p>
 * The core contract is the {@link org.luwrain.io.websearch.Engine} interface,
 * which any search provider implementation must fulfill. Concrete implementations
 * are expected to handle communication with specific search services (such as
 * general-purpose web search engines, image search, news search, etc.) while
 * exposing a uniform API to the rest of the system.
 * </p>
 *
 * <p>
 * Typical usage involves:
 * </p>
 * <ol>
 *   <li>Constructing a {@link org.luwrain.io.websearch.Query} with the desired
 *       search text, optional category filters, and language constraints.</li>
 *   <li>Obtaining an {@link org.luwrain.io.websearch.Engine} implementation
 *       (provided by an extension or a service factory).</li>
 *   <li>Calling {@link org.luwrain.io.websearch.Engine#search
 *       Engine.search()} to execute the query and receive a
 *       {@link org.luwrain.io.websearch.Response}.</li>
 *   <li>Iterating over the {@link org.luwrain.io.websearch.Entry} items
 *       contained in the response.</li>
 * </ol>
 *
 * @see org.luwrain.io.websearch.Query
 * @see org.luwrain.io.websearch.Engine
 * @see org.luwrain.io.websearch.Entry
 * @see org.luwrain.io.websearch.Response
 */
package org.luwrain.io.websearch;
