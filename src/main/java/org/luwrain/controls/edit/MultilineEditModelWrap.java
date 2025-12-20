// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls.edit;

import java.util.*;

import static java.util.Objects.*;
import static org.luwrain.controls.edit.MultilineEdit.*;

public class MultilineEditModelWrap implements MultilineEdit.Model
{
    protected final Model wrappedModel;

    public MultilineEditModelWrap(Model wrappedModel)
    {
	this.wrappedModel = requireNonNull(wrappedModel, "wrappedModel can't be null");
    }

    public Model getWrappedModel()
    {
	return wrappedModel;
    }

    @Override public int getLineCount()
    {
	return wrappedModel.getLineCount();
    }

    @Override public String getLine(int index)
    {
	return wrappedModel.getLine(index);
    }

    @Override public int getHotPointX()
    {
	return wrappedModel.getHotPointX();
    }

    @Override public int getHotPointY()
    {
	return wrappedModel.getHotPointY();
    }

    @Override public String getTabSeq()
    {
	return wrappedModel.getTabSeq();
    }

    @Override public ModificationResult deleteChar(int pos, int lineIndex)
    {
	return wrappedModel.deleteChar(pos, lineIndex);
    }

    @Override public ModificationResult deleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return wrappedModel.deleteRegion(fromX, fromY, toX, toY);
    }

    @Override public ModificationResult insertRegion(int x, int y, String[] lines)
    {
	return wrappedModel.insertRegion(x, y, lines);
	}

    @Override public ModificationResult putChars(int pos, int lineIndex, String str)
    {
	return wrappedModel.putChars(pos, lineIndex, str);
    }

    @Override public ModificationResult mergeLines(int firstLineIndex)
    {
	return wrappedModel.mergeLines(firstLineIndex);
    }

    @Override public ModificationResult splitLine(int pos, int lineIndex)
    {
	return wrappedModel.splitLine(pos, lineIndex);
    }
}
