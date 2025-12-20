// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.queries;

import java.nio.file.*;

import org.luwrain.core.*;

public final class BackgroundSoundQuery extends AreaQuery
{
    protected Answer answer;

    public BackgroundSoundQuery()
    {
	super(BACKGROUND_SOUND);
    }

    public void answer(Answer answer)
    {
	NullCheck.notNull(answer, "answer");
	secondAnswerCheck();
	this.answer = answer;
	answerTaken();
    }

    public Answer getAnswer()
    {
	return answer;
    }

    static public class Answer
    {
	private BkgSounds bkgSound = null;
	private String url = null;

	public Answer()
	{
	    bkgSound = null;
	    url = null;
	}

	public Answer(BkgSounds bkgSound)
	{
	    NullCheck.notNull(bkgSound, "bkgSound");
	    this.bkgSound = bkgSound;
	    this.url = null;
	}

	public Answer(String url)
	{
	    NullCheck.notEmpty(url, "url");
	    this.url = url;
	    this.bkgSound = null;
	}

	public boolean isEmpty()
	{
	    return bkgSound == null && url == null;
	}

	public boolean isUrl()
	{
	    return url != null;
	}

	public BkgSounds getBkgSound()
	{
	    return bkgSound;
	}

	public String getUrl()
	{
	    return url;
	}
    }
}
