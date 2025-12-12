// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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
