// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import org.luwrain.core.*;

public class DefaultTableAppearance implements TableArea.Appearance
{
    private ControlContext environment = null;

    public DefaultTableAppearance(ControlContext environment)
    {
	this.environment = environment;
    }

    @Override public void announceRow(TableArea.Model model,
			     int index,
			     int flags)
    {
	if (model == null)
	    return;
	String value = "";
	for(int i = 0;i < model.getColCount();++i)
	{
	    String text = getCellText(model, i, index);
	    value += (text != null?text:"");
	}
	if (!value.trim().isEmpty())
	    environment.say(value); else
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
    }

    @Override public int getInitialHotPointX(TableArea.Model model)
    {
	return 0;
    }

    @Override public String getCellText(TableArea.Model model, int col, int row)
    {
	if (model == null)
	    return "";
	Object cell = model.getCell(col, row);
	if (cell == null)
	    return null;
	String text = cell.toString();
	return text != null?text:"";
    }

    @Override public String getRowPrefix(TableArea.Model model, int index)
    {
	return "";
    }

    @Override public int getColWidth(TableArea.Model model, int  colIndex)
    {
	if (model == null)
	    return 0;
	int maxLen = 0;
	for(int i = 0;i < model.getRowCount();++i)
	{
	    final int len = getCellText(model, colIndex, i).length();
	    if (len > maxLen)
		maxLen = len;
	}
	return maxLen;
    }
}
