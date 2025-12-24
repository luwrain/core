// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.NullCheck;
import org.luwrain.cpanel.*;

class TreeItem 
{
    Element el;
    Factory factory;
    boolean onDemandFilled = false;
    Section sect = null;
    final LinkedList<Element> children = new LinkedList<Element>();

    TreeItem(Element el, Factory factory)
    {
	NullCheck.notNull(el, "el");
	NullCheck.notNull(factory, "factory");
	this.el = el;
	this.factory = factory;
    }
}
