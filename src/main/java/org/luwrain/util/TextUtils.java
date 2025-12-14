// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.util.*;

import org.luwrain.core.*;

import static java.util.Objects.*;

public final class TextUtils
{
        //On an empty line provided returns one empty line
    static public String[] splitLines(String text)
    {
	requireNonNull(text, "text can't be null");
	return text.replaceAll("\r\n", "\r").replaceAll("\r", "\n").split("\n", -1);
	    }

    static public List<String> splitLinesAsList(String text)
    {
	return Arrays.asList(splitLines(text));
    }

    static public String notLonger(String str, int maxLength)
    {
	if (maxLength < 0)
	    throw new IllegalArgumentException("maxLength can't be negative");
	return str.length() <= maxLength?str:str.substring(0, maxLength);
    }

    static public String getLastWord(String text, int upToPos)
    {
	requireNonNull(text, "text can't be null");
	String word = new String();
	boolean broken = false;
	for(int i = 0;i < text.length() && i < upToPos;++i)
	{
	    final char c = text.charAt(i);
	if (Character.getType(c) == Character.UPPERCASE_LETTER ||
	    Character.getType(c) == Character.LOWERCASE_LETTER ||
	    Character.getType(c) == Character.DECIMAL_DIGIT_NUMBER)
	{
	    if (broken)
		word = "";
	    broken = false;
	    word += c;
	    continue;
	}
	broken = true;
	}
	return word;
    }

    static public String sameCharString(char c, int count)
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < count;++i)
	    b.append(c);
	return new String(b);
    }

    static public String removeIsoControlChars(String value)
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < value.length();++i)
	    if (!Character.isISOControl(value.charAt(i)))
		b.append(value.charAt(i));
	return b.toString();
    }

    static public String replaceIsoControlChars(String value, char replaceWith)
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < value.length();++i)
	    if (!Character.isISOControl(value.charAt(i)))
		b.append(value.charAt(i)); else
		b.append(replaceWith);
	return b.toString();
    }

    static public String replaceIsoControlChars(String value)
    {
	return replaceIsoControlChars(value, ' ');
    }

    static public String[] wordWrap(String line, int width)
    {
	if (width < 3)
	    throw new IllegalArgumentException("width (" + String.valueOf(width) + ") can't be less than 3");
	final List<String> res = new ArrayList<>();
	final String[] words = line.split(" ", -1);
	StringBuilder b = new StringBuilder();
	for(String word: words)
	{
	    if (word.trim().isEmpty())
		continue;
	    if (b.length() + word.length() + 1 > width)
	    {
		res.add(new String(b));
		b = new StringBuilder();
	    }
		if (b.length() > 0)
		    b.append(" ");
		b.append(word);
	}
	if (b.length() > 0)
	    res.add(new String(b));
	return res.toArray(new String[res.size()]);
    }
}
