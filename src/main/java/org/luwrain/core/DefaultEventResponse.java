// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import org.luwrain.core.events.resp.*;

public class DefaultEventResponse
{
static public EventResponse text(String text) 
    {
	NullCheck.notNull(text, "text");
	return new TextResponse(null, text);
    }

    /**
     * Speak a text with simultaneous sound.
     *
     * @param sound A sound, may be null (no sound needed)
     * @param text A text to say
     */
    static public EventResponse text(Sounds sound, String text) 
    {
	NullCheck.notNull(text, "text");
	return new TextResponse(sound, text);
    }

    static public EventResponse letter(char letter)
    {
	if (Character.isWhitespace(letter) || letter == 160)//with non-breaking space
	    return new HintResponse(Hint.SPACE);
	return new LetterResponse(letter);
    }

        static public EventResponse hint(Hint hint) 
    {
	NullCheck.notNull(hint, "hint");
	return new HintResponse(hint);
    }

    static public EventResponse hint(Hint hint, String text) 
    {
	NullCheck.notNull(hint, "hint");
	NullCheck.notNull(text, "text");
	return new HintResponse(hint, text);
    }

    static public EventResponse listItem(String text) 
    {
	NullCheck.notNull(text, "text");
	return new ListItemResponse(null, text, null);
    }

    static public EventResponse listItem(String text, Suggestions suggestion) 
    {
	NullCheck.notNull(text, "text");
	return new ListItemResponse(null, text, suggestion);
    }

    static public EventResponse listItem(Sounds sound, String text, Suggestions suggestion) 
    {
	NullCheck.notNull(text, "text");
	return new ListItemResponse(sound, text, suggestion);
    }

    static public TreeItemResponse treeItem(TreeItemResponse.Type type, String text, int level, Suggestions suggestion)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(text, "text");
	return new TreeItemResponse(type, text, level, suggestion);
    }

        static public EventResponse treeItem(TreeItemResponse.Type type, String text, int level)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(text, "text");
	return new TreeItemResponse(type, text, level);
    }
}
