// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.queries;

import org.luwrain.core.*;

public final class UrlHotPointQuery extends AreaQuery
{
    private String url = null;

    public UrlHotPointQuery()
    {
	super(URL_HOT_POINT);
    }

    public void answer(String url)
    {
	NullCheck.notEmpty(url, "url");
	secondAnswerCheck();
	this.url = url;
answerTaken();
    }

    @Override public String getAnswer()
    {
	return url;
    }
}
