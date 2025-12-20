// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.net.*;

import static java.util.Objects.*;

public class DefaultStarter implements Starter
{
    protected final String name;
    protected final StarterCategory category;
    protected final URI uri;

    public DefaultStarter(String name, URI uri, StarterCategory category)
    {
	this.name = requireNonNull(name, "name can't be null");
	this.uri = requireNonNull(uri, "yuri can't be null");
	this.category = requireNonNull(category, "category can't be null");
	if (name.isEmpty())
	    throw new IllegalArgumentException("name can't be empty");
    }

    public DefaultStarter(URI uri, StarterCategory category)
    {
	this("Starter " + uri.toString(), uri, category);
    }

    public DefaultStarter(String uri, StarterCategory category)
    {
	this(URI.create(uri), category);
    }

    @Override public String getExtObjName()	
    {
	return name;
    }

    @Override public StarterCategory getCategory()
    {
	return category ;
    }

    @Override public URI getUri()
    {
	return uri;
    }
}
