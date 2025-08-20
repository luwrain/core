/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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
