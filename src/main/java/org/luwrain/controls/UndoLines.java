// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;

public class UndoLines implements MutableMarkedLines 
{
    protected final MutableLines lines;
    protected final List<Command> commands = new ArrayList<>();

    public UndoLines(MutableLines lines)
    {
	NullCheck.notNull(lines, "lines");
	this.lines = lines;
    }

    @Override public int getLineCount()
    {
	return lines.getLineCount();
    }

    @Override public String getLine(int index)
    {
	return lines.getLine(index);
    }

    @Override public void update(Updating updating)
    {
	//FIXME:
    }

    @Override public String[] getLinesAsArray()
    {
	return lines.getLinesAsArray();
    }

        @Override public List<String> getLines()
    {
	return lines.getLines();
    }


    @Override public void setLines(String[] lines)
    {
    }

    @Override public boolean add(String line)
    {
	NullCheck.notNull(line, "line");
	final Command cmd = new AddLine(lines, line);
	cmd.redo(lines);
	saveCommand(cmd);
	return true;
    }

    @Override public void add(int index, String line)
    {
	//FIXME:
    }

    @Override public void removeLine(int index)
    {
	//New command will take care about index bounds
	final Command cmd = new RemoveLine(lines, index);
	cmd.redo(lines);
	saveCommand(cmd);
    }

    @Override public void setLine(int index, String line)
    {
    }

    @Override public void clear()
    {
    }

    @Override public LineMarks getLineMarks(int index)
    {
	return null;
    }

    @Override public void setLineMarks(int index, LineMarks lineMarks)
    {
    }

    protected void saveCommand(Command command)
    {
	NullCheck.notNull(command, "command");
	commands.add(command);
    }

    static protected abstract class Command
    {
	public abstract void redo(MutableLines lines);
	public abstract void undo(MutableLines lines);
    }

    static protected final class AddLine extends Command
    {
	private final int addedLineIndex;
	private final String line;
	public AddLine(MutableLines lines, String line)
	{
	    NullCheck.notNull(lines, "lines");
	    NullCheck.notNull(line, "line");
	    this.addedLineIndex = lines.getLineCount();
	    this.line = line;
	}
	@Override public void redo(MutableLines lines)
	{
	    NullCheck.notNull(lines, "lines");
	    lines.add(line);
	}
	@Override public void undo(MutableLines lines)
	{
	    NullCheck.notNull(lines, "lines");
	    lines.removeLine(addedLineIndex);
	}
    }

        static protected final class RemoveLine extends Command
    {
	private final int removingLineIndex;
	private final String line;
	public RemoveLine(MutableLines lines, int index)
	{
	    NullCheck.notNull(lines, "lines");
	    if (index < 0 || index >= lines.getLineCount())
		throw new IllegalArgumentException("index (" + String.valueOf(index) + ") must be non-negative and less than " + String.valueOf(lines.getLineCount()));
	    this.removingLineIndex = lines.getLineCount();
	    this.line = lines.getLine(index);
	}
	@Override public void redo(MutableLines lines)
	{
	    NullCheck.notNull(lines, "lines");
	    lines.removeLine(removingLineIndex);
	}
	@Override public void undo(MutableLines lines)
	{
	    NullCheck.notNull(lines, "lines");
	    lines.add(removingLineIndex, line);
	}
    }

}
