// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.events;

import org.luwrain.core.*;
import static org.luwrain.core.NullCheck.*;

public class ActionEvent extends SystemEvent
{
    final Action action;

    public ActionEvent(Action action)
    {
	super(Code.ACTION);
	notNull(action, "action");
	this.action = action;
    }

    public Action getAction()
    {
	return action;
    }

    public String getActionName()
    {
	return action.name;
    }

    static public boolean isAction(Event event, String actionName)
    {
	if (event == null || !(event instanceof ActionEvent))
	    return false;
	notNull(actionName, "actionName");
	final ActionEvent actionEvent = (ActionEvent)event;
	return actionEvent.getActionName().equals(actionName);
    }
}
