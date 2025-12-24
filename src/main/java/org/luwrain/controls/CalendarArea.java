// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

import static java.util.Objects.*;

public class CalendarArea implements Area
{
    private final ControlContext context;
    private Calendar calendar;
    private int[][] table;
    private int tableX = 0, tableY = 0;

    public CalendarArea(ControlContext context, Calendar calendar)
    {
	this.context = requireNonNull(context, "context can't be null");
	this.calendar = requireNonNull(calendar, "calendar can't be null");
	refresh();
    }

    public int getHotPointX()
    {
	return tableX * 4;
    }

    public int getHotPointY()
    {
	return tableY + 1;
    }

    public int getLineCount()
    {
	return 1 + (table != null?table.length:0);
    }

    public String getLine(int index)
    {
	if (index == 0)
	    return "Mon Tue Wen Thu Fri Sat Sun";//FIXME:
	if (table == null || index - 1 >= table.length)
	    return "";
	return constructLine(index - 1);
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	if (!event.isSpecial() || event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case ARROW_RIGHT:
	    calendar.add(Calendar.DAY_OF_MONTH, 1);
	    break;
	case ARROW_LEFT:
	    calendar.add(Calendar.DAY_OF_MONTH, -1);
	    break;
	case ARROW_DOWN:
	    calendar.add(Calendar.WEEK_OF_MONTH, 1);
	    break;
	case ARROW_UP:
	    calendar.add(Calendar.WEEK_OF_MONTH, -1);
	    break;
	case PAGE_DOWN:
	    calendar.add(Calendar.MONTH, 1);
	    break;
	case PAGE_UP:
	    calendar.add(Calendar.MONTH, -1);
	    break;
	default:return false;
	}
	refresh();
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
	context.onAreaNewName(this);
	context.say(constructDayStringForSpeech(calendar));
	return true;
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	return false;
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return false;
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public String getAreaName()
    {
	//FIXME:Customizable behaviour;
	//FIXME:ROOT locale;
	return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ROOT) + ", " + calendar.get(Calendar.YEAR);
    }

    public void setCalendar(Calendar calendar)
    {
	this.calendar = calendar;
	refresh();
    }

    public Calendar getCalendar()
    {
	return calendar;
    }

    public String constructDayStringForSpeech(Calendar c)
    {
	//FIXME:ROOT locale;
	return c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ROOT) + " " + 
	c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ROOT) + " " +
	c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.YEAR);
    }

    private void refresh()
    {
	table = fillTable();
	tableX = 0;
	tableY = 0;
	if (table == null)
	    return;
	final int day = calendar.get(Calendar.DAY_OF_MONTH);
	for(int i = 0;i < table.length;++i)
	    for(int j = 0;j < table[i].length;++j)
		if (table[i][j] == day)
		{
		    tableY = i;
		    tableX = j;
		}
    }

    private int[][] fillTable()
    {
	Calendar c = (Calendar)calendar.clone();
	int[][] res = new int[c.getActualMaximum(Calendar.WEEK_OF_MONTH)][];
	for(int i = 0;i < res.length;++i)
	    res[i] = new int[7];
	for(int i = 0;i < res.length;++i)
	    for(int j = 0;j < res[i].length;++j)
	    res[i][j] = -1;
	final int count = c.getActualMaximum(Calendar.DAY_OF_MONTH);
	for(int i = 1;i <= count;++i)
	{
	    c.set(Calendar.DAY_OF_MONTH, i);
	    final int week = c.get(Calendar.WEEK_OF_MONTH);
	    final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK)  > 1?c.get(Calendar.DAY_OF_WEEK)  - 1:7;
	    res[week - 1][dayOfWeek - 1] = i;
	}
	return res;
    }

    private String constructLine(int index)
    {
	if (table == null || index >= table.length)
	    return "";
	String line = "";
	for(int i = 0;i < table[index].length;++i)
	    if (table[index][i] >= 0)
	    {
		if (table[index][i] < 10)
		    line += table[index][i] + "   "; else
		    line += table[index][i] + "  ";
	    } else
		line += "    ";
	return line;
    }
}
