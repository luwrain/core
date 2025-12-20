// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls.edit;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.edit.MultilineEdit.ModificationResult;
import org.luwrain.controls.edit.MultilineCorrector.*;

import static org.luwrain.core.NullCheck.*;

public class LegacyMultilineTranslator implements MultilineEditCorrector
{
    protected final MultilineTranslator translator;
    protected final MutableLines lines;
    protected final HotPointControl hotPoint;
    protected String tabSeq = "\t";

    public LegacyMultilineTranslator(MultilineTranslator translator)
    {
	notNull(translator, "translator");
	this.translator = translator;
	this.lines = translator.lines;
	this.hotPoint = translator.hotPoint;
    }

    @Override public ModificationResult deleteChar(int pos, int line)
    {
	final var c = new DeleteCharChange(line, pos);
	translator.change(c);
	return c.getResult();
    }

    @Override public ModificationResult deleteRegion(int fromX, int fromY, int toX, int toY)
    {
	final var c = new DeleteFragmentChange(fromY, fromX, toY, toX);
	translator.change(c);
	return c.getResult();
    }

    @Override public ModificationResult insertRegion(int x, int y, String[] text)
    {
	final var c = new InsertFragmentChange(y, x, Arrays.asList(text));
	translator.change(c);
	return c.getResult();
    }

    @Override public ModificationResult putChars(int pos, int line, String str)
    {
	final var c = new InsertCharsChange(line, pos, str);
	translator.change(c);
	return c.getResult();
    }

    @Override public ModificationResult mergeLines(int firstLineIndex)
    {
	final var c = new MergeLinesChange(firstLineIndex);
	translator.change(c);
	return c.getResult();
    }

    @Override public ModificationResult splitLine(int pos, int line)
    {
	final var c = new SplitLineChange(line, pos);
	translator.change(c);
	return c.getResult();
    }

    @Override public ModificationResult doEditAction(TextEditAction action)
    {
	action.doTextEdit(lines, hotPoint);
	return new ModificationResult(true);
    }

    public boolean commit()
    {
	return true;
    }

    @Override public int getLineCount()
    {
	final int count = lines.getLineCount();
	return count > 0?count:1;
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	if (index == 0 && lines.getLineCount() == 0)
	    return "";
	return lines.getLine(index);
    }

    @Override public int getHotPointX() { return hotPoint.getHotPointX(); }
    @Override public int getHotPointY() {return hotPoint.getHotPointY(); }
    @Override public String getTabSeq() { return tabSeq; }
}
