// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import static java.util.Objects.*;

abstract public class AreaQuery extends Event
{
    static public final int UNIREF_AREA = 1;
    static public final int UNIREF_HOT_POINT = 2;
    static public final int URL_AREA = 3;
    static public final int URL_HOT_POINT = 4;
    static public final int CURRENT_DIR = 5;
    static public final int BEGIN_LISTENING = 6;
    static public final int BACKGROUND_SOUND = 7;;
    static public final int REGION_TEXT = 8;;

    private final int code;
    private boolean hasAnswer = false;

    public AreaQuery(int code)
    {
	//	super(AREA_QUERY_EVENT);
	this.code = code;
    }

    public final int getQueryCode()
    {
	return code;
    }

    abstract public Object getAnswer();

    public boolean hasAnswer()
    {
	return hasAnswer;
    }

    protected void answerTaken()
    {
	hasAnswer = true;
    }

    protected void secondAnswerCheck()
    {
	if (hasAnswer())
	    throw new IllegalArgumentException("Answer may not be made twice");
    } 

    static public boolean ask(Area area, AreaQuery query)
    {
	requireNonNull(area, "area can't be null");
	requireNonNull(query, "query can't be null");
	return area.onAreaQuery(query) && query.hasAnswer();
    }
}
