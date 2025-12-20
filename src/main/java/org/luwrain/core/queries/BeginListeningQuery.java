// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.queries;

import org.luwrain.core.*;

public final class BeginListeningQuery extends AreaQuery
{
    protected Answer answer = null;

    public BeginListeningQuery()
    {
	super(BEGIN_LISTENING);
    }

    public void answer(Answer answer)
    {
	NullCheck.notNull(answer, "answer");
	secondAnswerCheck();
	this.answer = answer;
	answerTaken();
    }

    @Override public Answer getAnswer()
    {
	return answer;
    }

    static public class Answer
    {
	protected final String text;
	protected final Object extraInfo;

	public Answer(String text, Object extraInfo)
	{
	    NullCheck.notNull(text, "text");
	    this.text = text;
	    this.extraInfo = extraInfo;
	}

	public String getText()
	{
	    return text;
	}

	public Object getExtraInfo()
	{
	    return extraInfo;
	}

	@Override public String toString()
	{
	    return text;
	}
    }

    static public class PositionedAnswer extends Answer
    {
	protected final int x;
	protected final int y;

	public PositionedAnswer(String text, int x, int y)
	{
	    super(text, new int[]{x, y});
	    if (x < 0)
		throw new IllegalArgumentException("x (" + x + ") may not be negative ");
	    if (y < 0)
		throw new IllegalArgumentException("y (" + y + ") may not be negative");
	    this.x = x;
	    this.y = y;
	}

	public int getX()
	{
	    return x;
	}

	public int getY()
	{
	    return y;
	}
    }
}
