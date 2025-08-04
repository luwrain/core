/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

import java.util.*;
import java.util.function.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.edit.MultilineEdit.ModificationResult;

import static org.luwrain.core.NullCheck.*;

public final class EditUtils
{
    static public void blockBounds(EditArea editArea, int lineIndex, BiPredicate<String, LineMarks> pred, BiConsumer<MarkedLines, Integer> accepting)
    {
	NullCheck.notNull(editArea, "editArea");
	NullCheck.notNull(pred, "pred");
	NullCheck.notNull(accepting, "accepting");
	blockBounds(editArea.getContent(), lineIndex, pred, accepting);
    }

    static public void blockBounds(FormArea formArea, int lineIndex, BiPredicate<String, LineMarks> pred, BiConsumer<MarkedLines, Integer> accepting)
    {
	NullCheck.notNull(formArea, "formArea");
	NullCheck.notNull(pred, "pred");
	NullCheck.notNull(accepting, "accepting");
	blockBounds(formArea.getMultilineEditContent(), lineIndex, pred, accepting);
    }

    static public void blockBounds(MarkedLines lines, int lineIndex, BiPredicate<String, LineMarks> pred, BiConsumer<MarkedLines, Integer> accepting)
    {
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(pred, "pred");
	NullCheck.notNull(accepting, "accepting");
	if (lineIndex < 0)
	    throw new IllegalArgumentException("lineINdex can't be negative");
	if (lineIndex >= lines.getLineCount())
	    return;
	if (!pred.test(lines.getLine(lineIndex), lines.getLineMarks(lineIndex)))
	    return;
	int fromPos = lineIndex, toPos = lineIndex;
	while(fromPos > 0 && pred.test(lines.getLine(fromPos - 1), lines.getLineMarks(fromPos - 1)))
	    fromPos--;
	while(toPos + 1 < lines.getLineCount() && pred.test(lines.getLine(toPos + 1), lines.getLineMarks(toPos + 1)))
	    toPos++;
	for(int i = fromPos;i <= toPos;i++)
	    accepting.accept(lines, Integer.valueOf(i));
    }

    static public class DefaultEditAreaAppearance extends DefaultMultilineEditAppearance implements EditArea.Appearance
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
	notNull(line, "line");
	if (speakableTextType != null)
	    NavigationArea.defaultLineAnnouncement(context, index, context.getSpeakableText(line, speakableTextType
											    )); else
	NavigationArea.defaultLineAnnouncement(context, index, line);
    }
    }

        static public class DefaultMultilineEditAppearance implements MultilineEdit.Appearance
    {
	protected final ControlContext context;
	public DefaultMultilineEditAppearance(ControlContext context)
	{
	    NullCheck.notNull(context, "context");
	    this.context = context;
	}
	@Override public boolean onBackspaceTextBegin()
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_TEXT));
	    return true;
	}
	@Override public boolean onBackspaceMergeLines(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed())
		return false;
	    context.setEventResponse(DefaultEventResponse.hint(Hint.LINE_BOUND));
	    return true;
	}
	@Override public boolean onBackspaceDeleteChar(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed() || res.getCharArg() == '\0')
		return false;
	    context.setEventResponse(DefaultEventResponse.letter(res.getCharArg()));
	    return true;
	}
	@Override public boolean onDeleteChar(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed() || res.getCharArg() == '\0')
		return false;
	    context.setEventResponse(DefaultEventResponse.letter(res.getCharArg()));
	    return true;
	}
	@Override public boolean onDeleteCharTextEnd()
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_TEXT));
	    return true;
	}
	@Override public boolean onDeleteCharMergeLines(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed())
		return false;
	    context.setEventResponse(DefaultEventResponse.hint(Hint.LINE_BOUND)); 
	    return true;
	}
	@Override public boolean onTab(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed())
		return false;
	    context.setEventResponse(DefaultEventResponse.hint(Hint.TAB));
	    return true;
	}
	@Override public boolean onSplitLines(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed())
		return false;
	    final String line = res.getStringArg();
	    if (line == null || line.isEmpty())
		context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE)); else
		if (line.trim().isEmpty())
		    context.setEventResponse(DefaultEventResponse.hint(Hint.SPACES)); else
		    context.setEventResponse(DefaultEventResponse.text(line));
	    return true;
	}
	@Override public boolean onChar(ModificationResult res)
	{
	    NullCheck.notNull(res, "res");
	    if (!res.isPerformed())
		return false;
	    if (Character.isWhitespace(res.getCharArg()))
	    {
		final String word = res.getStringArg();
		if (word != null && !word.trim().isEmpty())
		    context.setEventResponse(DefaultEventResponse.text(word)); else
		    context.setEventResponse(DefaultEventResponse.letter(res.getCharArg()));
	    } else
		context.setEventResponse(DefaultEventResponse.letter(res.getCharArg()));
	    return true;
	}
    }

}
