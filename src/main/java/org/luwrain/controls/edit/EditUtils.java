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

import java.util.function.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

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



}
