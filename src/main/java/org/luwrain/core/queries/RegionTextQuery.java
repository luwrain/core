// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.queries;

import org.luwrain.core.*;

public final class RegionTextQuery extends AreaQuery
{
    private String regionText = null;

    public RegionTextQuery()
    {
	super(REGION_TEXT);
    }

    public void answer(String text)
    {
	NullCheck.notNull(text, "text");
	secondAnswerCheck();
	this.regionText = text;
answerTaken();
    }

    @Override public String getAnswer()
    {
	return regionText;
    }
}
