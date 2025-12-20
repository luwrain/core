// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

import static org.luwrain.core.NullCheck.*;

final class OpenedPopup
{
    final Application app;
    final int index;//Popup index in the owning application
    final Popup.Position position;
    final Base.PopupStopCondition stopCondition;
    final Set<Popup.Flags> flags;

    OpenedPopup(Application app,
		int index,
		Popup.Position position,
		Base.PopupStopCondition stopCondition,
		Set<Popup.Flags> flags)
    {
	//app can be null
	notNull(position, "position");
	notNull(stopCondition, "stopCondition");
	notNull(flags, "flags");
	this.app = app;
	this.index = index;
	this.position = position;
	this.stopCondition = stopCondition;
	this.flags = flags;
    }
}
