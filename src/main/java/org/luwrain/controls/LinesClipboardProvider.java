// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;

public class LinesClipboardProvider implements ClipboardTranslator.Provider
{
    public interface ClipboardSource
    {
	Clipboard getClipboard();
    }

    protected final Lines lines;
    protected final ClipboardSource clipboardSource;

    public LinesClipboardProvider(Lines lines, ClipboardSource clipboardSource)
    {
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(clipboardSource, "clipboardSource");
	this.lines = lines;
	this.clipboardSource = clipboardSource;
    }

    @Override public boolean onClipboardCopyAll()
    {
	final List<String> res = new ArrayList<>();
	final int count = lines.getLineCount();
	if (count < 1)
	{
	    clipboardSource.getClipboard().set(new String[0]);
	    return true;
	}
	for(int i = 0;i < count;++i)
	{
	    final String line = lines.getLine(i);
	    if (line == null)
		return false;
	    res.add(line);
	}
	clipboardSource.getClipboard().set(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onClipboardCopy(int fromX, int fromY, int toX, int toY, boolean withDeleting)
    {
	if (withDeleting)
	    return false;
	final int count = lines.getLineCount();
	if (count < 1)
	    return false;
	if (fromY >= count || toY > count || fromY > toY)
	    return false;
	if (fromY == toY)
	{
	    final String line = lines.getLine(fromY);
	    if (line == null)
		return false;
	    final int fromPos = Math.min(fromX, line.length());
	    final int toPos = Math.min(toX, line.length());
	    if (fromPos >= toPos)
		return false;
	    clipboardSource.getClipboard().set(line.substring(fromPos, toPos));
	    return true;
	}
	final List<String> res = new ArrayList<>();
	final String firstLine = lines.getLine(fromY);
	if (firstLine == null)
	    return false;
	res.add(firstLine.substring(Math.min(fromX, firstLine.length())));
	for(int i = fromY + 1;i < toY;++i)
	{
	    final String line = lines.getLine(i);
	    if (line == null)
		return false;
	    res.add(line);
	}
	final String lastLine = lines.getLine(toY);
	if (lastLine == null)
	    return false;
	res.add(lastLine.substring(0, Math.min(toX, lastLine.length())));
	clipboardSource.getClipboard().set(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onDeleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }
}
