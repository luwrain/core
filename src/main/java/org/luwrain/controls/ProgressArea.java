// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class ProgressArea extends SimpleArea
{
    public ProgressArea(ControlContext environment)
    {
	super(environment);
    }

    public ProgressArea(ControlContext environment, String name)
    {
	super(environment, name);
    }

    public ProgressArea(ControlContext environment, String name,
			String[] lines)
    {
	super(environment, name, lines);
    }

    public void addProgressLine(String line)
    {
	NullCheck.notNull(line, "line");
	if (content.getLineCount() > 0)
	{
	    content.setLine(content.getLineCount() - 1, line);
	    content.addLine("");
	} else
	    addLine(line);
	environment.onAreaNewContent(this);
    }
}
