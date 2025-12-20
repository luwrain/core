// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.queries;

import org.luwrain.core.*;

public final class UniRefAreaQuery extends AreaQuery
{
    private String uniRef = null;

    public UniRefAreaQuery()
    {
	super(UNIREF_AREA);
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
