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

package org.luwrain.controls.edit;

import org.luwrain.core.*;
import org.luwrain.controls.*;

import static java.util.Objects.*;

public class DefaultEditAreaAppearance extends DefaultMultilineEditAppearance implements EditArea.Appearance
{
    protected final Luwrain.SpeakableTextType speakableTextType;
    public DefaultEditAreaAppearance(ControlContext context, Luwrain.SpeakableTextType speakableTextType)
    {
	super(context);
	this.speakableTextType = speakableTextType;
    }

    public DefaultEditAreaAppearance(ControlContext context)
    {
	this(context, null);
    }

    @Override public void announceLine(int index, String line)
    {
	requireNonNull(line, "line can't be null");
	if (speakableTextType != null)
	    NavigationArea.defaultLineAnnouncement(context, index, context.getSpeakableText(line, speakableTextType
											    )); else
	    NavigationArea.defaultLineAnnouncement(context, index, line);
    }
}
