// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.queries;

import java.nio.file.*;

import org.luwrain.core.*;

public final class CurrentDirQuery extends AreaQuery
{
    protected String answer = null;

    public CurrentDirQuery()
    {
	super(CURRENT_DIR);
    }

    public void answer(String currentDir)
    {
	NullCheck.notEmpty(currentDir, "currentDir");
	secondAnswerCheck();
	if (!Paths.get(currentDir).isAbsolute())
	    throw new IllegalArgumentException("currentDir must be absolute");
	this.answer = currentDir;
answerTaken();
    }

    public String getAnswer()
    {
	return answer;
    }
}
