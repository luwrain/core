/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.cpanel;

//import static org.luwrain.core.NullCheck.*;
import static java.util.Objects.*;

public class SimpleElement implements Element
{
    protected final Element parent;
    protected final String value;

    public SimpleElement(Element parent, String value)
    {
	this.parent = parent;
	this.value = requireNonNull(value, "value can't be null");
	if (value.isEmpty())
	    throw new IllegalArgumentException("value can't be empty");
    }

    @Override public Element getParentElement()
    {
	return parent;
    }

    @Override public String toString()
    {
	return value;
    }

    @Override public boolean equals(Object o)
    {
	if (o != null && o instanceof SimpleElement el)
	return value.equals(el.value);
	    return false;
    }

    @Override public int hashCode()
    {
	return value.hashCode();
    }
}
