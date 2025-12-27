// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;

public class MutableLinesImpl extends ArrayList<String> implements MutableLines 
{
    public MutableLinesImpl()
    {
    }

    public MutableLinesImpl(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	addAll(Arrays.asList(lines));
    }

    @Override public void update(Updating updating)
    {
	NullCheck.notNull(updating, "updating");
	updating.update(this);
    }

    @Override public int getLineCount()
    {
	return size();
    }

    @Override public String getLine(int index)
    {
	if (index < 0 || index >= size())
	    return "";
	return get(index);
    }

    @Override public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	clear();
	addAll(Arrays.asList(lines));
    }

    @Override public String[] getLinesAsArray()
    {
	return toArray(new String[size()]);
    }

    @Override public List<String> getLines()
    {
	return this;
    }

    @Override public void setLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index < 0)
	    throw new IllegalArgumentException("index (" + String.valueOf(index) + " can't be negative");
	if (index >= size())
	    throw new IllegalArgumentException("index (" + String.valueOf(index) + ") can't be greater or equal to line count (" + String.valueOf(size()) + ")");
	set(index, line);
    }

    @Override public void removeLine(int index)
    {
	if (index < 0 || index >= size())
	    throw new IllegalArgumentException("Invalid index (" + index + ")");
	remove(index);
    }

    public String getWholeText(String lineSep)
    {
	final String s = lineSep != null?lineSep:System.lineSeparator();
	if (size() == 0)
	    return "";
	if (size() == 1)
	    return get(0);
	final StringBuilder res = new StringBuilder();
	res.append(get(0));
	for(int i = 1;i < size();++i)
	    res.append(s).append(get(i));
	return new String(res);
    }

    public String getWholeText()
    {
	return getWholeText(null);
    }
}
