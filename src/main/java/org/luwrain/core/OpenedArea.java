// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import static org.luwrain.core.NullCheck.*;

final class OpenedArea implements AreaWrapperFactory.Disabling
{
    final Area area;
    Area wrapper = null;

    OpenedArea(Area area)
    {
	 notNull(area, "area");
	this.area = area;
    }

    boolean hasArea(Area area)
    {
	notNull(area, "area");
	return this.area == area || wrapper == area;
    }

    Area getFrontArea()
    {
	if (wrapper != null)
	    return wrapper;
	return area;
    }

    @Override public void disableAreaWrapper()
    {
	wrapper = null;
    }
}
