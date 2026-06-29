// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.websearch;

import java.util.*;
import lombok.*;

/**
 * Represents a search query to be submitted to a web search engine.
 *
 * <p>
 * A query consists of a free-form search text, zero or more content
 * categories that narrow the scope of the search (for instance, limiting
 * results to images or news articles), and an optional set of language
 * codes to restrict results to content in particular languages.
 * </p>
 *
 * <p>
 * The {@link #text} field is the only mandatory part of a query; both
 * {@link #categories} and {@link #langs} may be empty or {@code null},
 * in which case the search engine is expected to apply reasonable
 * defaults (typically searching all categories and languages).
 * </p>
 *
 * @see Engine#search
 * @see Response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class Query
{
    /**
     * Categories used to narrow the scope of a search query.
     * <p>
     * A query may be associated with multiple categories simultaneously;
     * an empty or {@code null} set of categories means "search across
     * all available categories".
     * </p>
     */
    public enum Category {
	/** General-purpose web search. */
	GENERAL,
	/** Search for music tracks, albums, or artists. */
	MUSIC,
	/** Search for images and pictures. */
	IMAGES,
	/** Search for news articles. */
	NEWS,
	/** Search for scientific publications and resources. */
	SCIENCE,
	/** Search for software development resources. */
	DEVEL,
	/** Search across social networks and social media. */
	SOCIAL
    };

    /** The free-form text of the search query. Must not be empty when submitting. */
    private String text;

    /**
     * Content categories to which the search should be restricted.
     * May be empty or {@code null} to search across all categories.
     */
    private Set<Category> categories;

    /**
     * Language tags (e.g. {@code "en"}, {@code "ru"}) to restrict
     * search results. May be empty or {@code null} to include all languages.
     */
    private Set<String> langs;
}
