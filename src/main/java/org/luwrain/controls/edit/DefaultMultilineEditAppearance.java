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
import static org.luwrain.core.DefaultEventResponse.*;
import static org.luwrain.controls.edit.MultilineEdit.*;

public class DefaultMultilineEditAppearance implements MultilineEdit.Appearance
{
    protected final ControlContext context;

    public DefaultMultilineEditAppearance(ControlContext context)
    {
	requireNonNull(context, "context can't be null can't be null");
	this.context = context;
    }

    @Override public boolean onBackspaceTextBegin()
    {
	context.setEventResponse(hint(Hint.BEGIN_OF_TEXT));
	return true;
    }

    @Override public boolean onBackspaceMergeLines(ModificationResult res)
    {
	requireNonNull(res, "res can't be null");
	if (!res.isPerformed())
	    return false;
	context.setEventResponse(hint(Hint.LINE_BOUND));
	return true;
    }

    @Override public boolean onBackspaceDeleteChar(ModificationResult res)
    {
	requireNonNull(res, "res can't be null");
	if (!res.isPerformed() || res.getCharArg() == '\0')
	    return false;
	context.setEventResponse(letter(res.getCharArg()));
	return true;
    }

    @Override public boolean onDeleteChar(ModificationResult res)
    {
	requireNonNull(res, "res can't be null");
	if (!res.isPerformed() || res.getCharArg() == '\0')
	    return false;
	context.setEventResponse(letter(res.getCharArg()));
	return true;
    }

    @Override public boolean onDeleteCharTextEnd()
    {
	context.setEventResponse(hint(Hint.END_OF_TEXT));
	return true;
    }

    @Override public boolean onDeleteCharMergeLines(ModificationResult res)
    {
	requireNonNull(res, "res can't be null");
	if (!res.isPerformed())
	    return false;
	context.setEventResponse(hint(Hint.LINE_BOUND)); 
	return true;
    }

    @Override public boolean onTab(ModificationResult res)
    {
	requireNonNull(res, "res can't be null");
	if (!res.isPerformed())
	    return false;
	context.setEventResponse(hint(Hint.TAB));
	return true;
    }

    @Override public boolean onSplitLines(ModificationResult res)
    {
	requireNonNull(res, "res can't be null");
	if (!res.isPerformed())
	    return false;
	final String line = res.getStringArg();
	if (line == null || line.isEmpty())
	    context.setEventResponse(hint(Hint.EMPTY_LINE)); else
	    if (line.trim().isEmpty())
		context.setEventResponse(hint(Hint.SPACES)); else
		context.setEventResponse(text(line));
	return true;
    }

    @Override public boolean onChar(ModificationResult res)
    {
	requireNonNull(res, "res can't be null");
	if (!res.isPerformed())
	    return false;
	if (Character.isWhitespace(res.getCharArg()))
	{
	    final String word = res.getStringArg();
	    if (word != null && !word.trim().isEmpty())
		context.setEventResponse(text(word)); else
		context.setEventResponse(letter(res.getCharArg()));
	} else
	    context.setEventResponse(letter(res.getCharArg()));
	return true;
    }
}
