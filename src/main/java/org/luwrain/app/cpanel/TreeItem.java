// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.NullCheck;
import org.luwrain.cpanel.*;

import static java.util.Objects.*;

final class TreeItem 
{
    Element el;
    Factory factory;
    boolean onDemandFilled = false;
    Section sect = null;
    final LinkedList<Element> children = new LinkedList<Element>();

    TreeItem(Element el, Factory factory)
    {
	requireNonNull(el, "el can't be null");
	requireNonNull(factory, "factory can't be null");
	this.el = el;
	this.factory = factory;
    }
}
