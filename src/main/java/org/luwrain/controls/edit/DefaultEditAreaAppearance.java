// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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
