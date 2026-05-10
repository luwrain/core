// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import org.luwrain.core.queries.*;
import static java.util.Objects.*;

final class AreaText
{
    private final Area area;

    AreaText(Area area)
    {
	requireNonNull(area, "area can't be null");
	this.area = area;
    }

    String get(Luwrain.AreaTextType type)
    {
	requireNonNull(type, "type can't be null");
	switch(type)
	{
	case WORD:
	    return getWord();
	case REGION:
	    return getRegion();
	case LINE:
	case SENTENCE:
	case URL:
	default:
	    return null;
	}
    }

    String getWord()
    {
	final int x = area.getHotPointX();
	final int y = area.getHotPointY();
	if (y >= area.getLineCount())
	    return "";
	final String line = area.getLine(y);
	if (line == null || x >= line.length())
	    return "";
	if (!wordChar(line.charAt(x)))
	    return "";
	int i = x;
	String res = "";
	while (i >= 0 && wordChar(line.charAt(i)))
	{
	    res = line.charAt(i) + res;
	    --i;
	}
	i = x + 1;
	while (i < line.length() && wordChar(line.charAt(i)))
	{
	    res += line.charAt(i);
	    ++i;
	}
	return res;
    }

    private String getRegion()
    {
	final RegionTextQuery query = new RegionTextQuery();
	if (!AreaQuery.ask(area, query))
	    return null;
	return query.getAnswer();
    }

    static private boolean wordChar(char c)
    {
	return Character.isLetter(c) || Character.isDigit(c) || c == '_' || c == '-';
    }
}
