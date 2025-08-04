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
import org.luwrain.controls.edit.MultilineEdit.*;
import org.luwrain.controls.edit.EditUtils.*;

import static java.util.Objects.*;

/**
 * Implements a listener of all changes in 
 * {@link MultilineEdit.Model}. This class contains the abstract method 
 * {@code onMultilineEditChange} called each time when any changes occurred in
 * the state of the model.  This allows users to implement any necessary
 * actions, which should have effect if and only if something was changed
 * in the model and this class guarantees that {@code
 * onMultilineEditChange} is called strictly after changes in the model.
 *
 * @see MultilineEdit
 */
public abstract class MultilineEditModelChangeListener implements MultilineEdit.Model
{
    protected final MultilineEdit.Model model;

    public MultilineEditModelChangeListener(MultilineEdit.Model model)
	{
	    requireNonNull(model, "model can't be null");
	    this.model = model;
	}

	/** Called if the model gets some changes. There is a guarantee that this method
	 * is invoked strictly after the changes in the model.
	 */	 
	abstract public void onAfterMultilineEditChange();

    @Override public int getLineCount()
    {
	return model.getLineCount();
    }

    @Override public String getLine(int index)
    {
	return model.getLine(index);
    }

    @Override public int getHotPointX()
    {
	return model.getHotPointX();
    }

    @Override public int getHotPointY()
    {
	return model.getHotPointY();
    }

	@Override public String getTabSeq()
	{
	    return model.getTabSeq();
	}

    @Override public ModificationResult deleteChar(int pos, int lineIndex)
    {
	final ModificationResult res = model.deleteChar(pos, lineIndex);
	if (res.isPerformed())
	    onAfterMultilineEditChange();
	return res;
    }

    @Override public ModificationResult deleteRegion(int fromX, int fromY, int toX, int toY)
    {
	final ModificationResult res = model.deleteRegion(fromX, fromY, toX, toY);
	if (res.isPerformed())
	    onAfterMultilineEditChange();
	return res;
    }

    @Override public ModificationResult insertRegion(int x, int y, String[] lines)
    {
	final ModificationResult res = model.insertRegion(x, y, lines);
	if (res.isPerformed())
	    onAfterMultilineEditChange();
	return res;
    }

    @Override public ModificationResult putChars(int pos, int lineIndex, String str)
    {
	final ModificationResult res = model.putChars(pos, lineIndex, str);
	if (res.isPerformed())
	    onAfterMultilineEditChange();
	return res;
    }

    @Override public ModificationResult mergeLines(int firstLineIndex)
    {
	final ModificationResult res = model.mergeLines(firstLineIndex);
	if (res.isPerformed())
	    onAfterMultilineEditChange();
	return  res;
    }

    @Override public ModificationResult splitLine(int pos, int lineIndex)
    {
	final ModificationResult res = model.splitLine(pos, lineIndex);
	if (res.isPerformed())
	    onAfterMultilineEditChange();
	return res;
    }
    }

