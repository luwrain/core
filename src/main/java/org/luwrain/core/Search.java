// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.util.*;

public class Search implements Area
{
    private final Area area;
    private final Core core;
    private final AreaWrapperFactory.Disabling disabling;
    private int hotPointX = 0;
    private int hotPointY = 0;
    private String expression = "";//What we already have found

    public Search(Area area, Core core, AreaWrapperFactory.Disabling disabling)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(core, "core");
	NullCheck.notNull(disabling, "disabling");
	this.area = area;
	this.core = core;
	this.disabling = disabling;
	hotPointX = area.getHotPointX();
	hotPointY = area.getHotPointY();
	if (hotPointX < 0)
	    hotPointX = 0;
	if (hotPointY < 0)
	    hotPointY = 0;
	core.message(core.i18n.getStaticStr("SearchMode"), Luwrain.MessageType.NONE);
	core.soundManager.playIcon(Sounds.SEARCH);
    }

    @Override public String getAreaName()
    {
	return "Режим поиска: " + area.getAreaName();//FIXME:
    }

    @Override public int getHotPointX()
    {
	return hotPointX + expression.length();
    }

    @Override public int getHotPointY()
    {
	return hotPointY;
    }

    @Override public int getLineCount()
    {
	return area.getLineCount();
    }

    @Override public String getLine(int index)
    {
	return area.getLine(index);
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case TAB:
		return onNewChar('\0');
	    case ESCAPE:
		closeSearch(false);
		return true;
	    case ENTER:
		return closeSearch(true);
	    case ARROW_LEFT:
	    case ARROW_RIGHT:
	    case ARROW_UP:
	    case ARROW_DOWN:
		return announceCurrentLine();
	    default:
		return false;
	    }
	return onNewChar(event.getChar());
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	return area.onSystemEvent(event);
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	switch(query.getQueryCode())
	{
	case AreaQuery.BACKGROUND_SOUND:
	    ((BackgroundSoundQuery)query).answer(new BackgroundSoundQuery.Answer(BkgSounds.SEARCH));
	    return true;
	default:
	    return area.onAreaQuery(query);
	}
    }

    private boolean onNewChar(char c)
    {
	final String lookFor = c != '\0'?expression + Character.toLowerCase(c):expression;
	if (c == '\0')
	{
	    if (expression.isEmpty())
		return false;
	    ++hotPointX;
	}
	if (hotPointY > getLineCount())
	{
	    core.soundManager.playIcon(Sounds.BLOCKED);
	    return true;
	}
	String line = getLine(hotPointY);
	if (line != null && hotPointX <line.length())
	{
	    line = line.toLowerCase();
	    line = line.substring(hotPointX);
	    final int pos = line.indexOf(lookFor);
	    if (pos >= 0)
	    {
		hotPointX += pos;
		expression = lookFor;
		core.windowManager.redraw();
		core.message(getLine(hotPointY)/*.substring(hotPointX)*/, Luwrain.MessageType.NONE);
		return true;
	    }
	} //On the current line
	for(int i = hotPointY + 1;i < getLineCount();++i)
	{
	    line = getLine(i);
	    if (line == null)
		continue;
	    line = line.toLowerCase();
	    final int pos = line.indexOf(lookFor);
	    if (pos < 0)
		continue;
	    hotPointX = pos;
	    hotPointY = i;
	    core.message(line/*.substring(pos)*/, Luwrain.MessageType.NONE);
	    expression = lookFor;
	    core.windowManager.redraw();
	    return true;
	}
	core.soundManager.playIcon(Sounds.BLOCKED);
	return true;
    }

    private boolean closeSearch(boolean accept)
    {
	if (accept )
	{
	    if (!area.onSystemEvent(new MoveHotPointEvent(hotPointX, hotPointY, false)))
		return false;
	    core.setAreaIntroduction();
	} else
	    core.message("Поиск отменён", Luwrain.MessageType.NONE);
	core.soundManager.playIcon(Sounds.CANCEL);
disabling.disableAreaWrapper();
	core.onNewAreasLayout();
	return true;
    }

    private boolean announceCurrentLine()
    {
	if (hotPointY >= area.getLineCount())
	    return false;
	final String line = area.getLine(hotPointY);
	if (line == null)//Security wrapper should make this impossible
	    return false;
	new LuwrainImpl(core).speak(line);//FIXME:
	return true;
    }
}
