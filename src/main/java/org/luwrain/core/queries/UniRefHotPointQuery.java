// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.queries;

import org.luwrain.core.*;

public final class UniRefHotPointQuery extends AreaQuery
{
    private String uniRef = null;

    public UniRefHotPointQuery()
    {
	super(UNIREF_HOT_POINT);
    }

    public void answer(String uniRef)
    {
	NullCheck.notEmpty(uniRef, "uniRef");
	secondAnswerCheck();
	this.uniRef = uniRef;
answerTaken();
    }

    @Override public String getAnswer()
    {
	return uniRef;
    }
}
