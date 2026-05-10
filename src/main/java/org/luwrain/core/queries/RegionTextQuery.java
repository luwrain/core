// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.queries;

import org.luwrain.core.*;
import static java.util.Objects.*;

public final class RegionTextQuery extends AreaQuery
{
    private String regionText = null;

    public RegionTextQuery()
    {
	super(REGION_TEXT);
    }

    public void answer(String text)
    {
	requireNonNull(text, "text can't be null");
	secondAnswerCheck();
	this.regionText = text;
answerTaken();
    }

    @Override public String getAnswer()
    {
	return regionText;
    }
}
