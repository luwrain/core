// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

import static java.util.Objects.*;

/**
 * {code Area} interface implementation with internal lines storing. This
 * area type has its own lines container based on 
 * {@link MutableLinesImpl} class. It is the minimal area implementation which
 * doesn't have any abstract methods. It is useful, if it is necessary to
 * have an area with some static content, available for changing through
 * the operations of {@link MutableLines} interface.
 */
public class SimpleArea extends NavigationArea implements MutableLines
{
    protected final ControlContext environment;
    protected String name = "";
    protected final MutableLinesImpl content = new MutableLinesImpl();

    public SimpleArea(ControlContext environment)
    {
	super(environment);
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
    }

    public SimpleArea(ControlContext environment, String name)
    {
	super(environment);
	this.environment = environment;
	this.name = name;
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(name, "name");
    }

    public SimpleArea(ControlContext environment, String name,
		      String[] lines)
    {
	super(environment);
	this.environment = environment;
	this.name = name;
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(name, "name");
	NullCheck.notNullItems(lines, "lines");
	content.setLines(lines);
    }

    @Override public void update(Updating updating)
    {
	NullCheck.notNull(updating, "updating");
	content.update(updating);
	afterChange();
    }

    @Override public int getLineCount()
    {
	final int value = content.getLineCount();
	return value > 0?value:1;
    }

    @Override public String getLine(int index)
    {
	if (index >= content.getLineCount())
	    return "";
	final String line = content.getLine(index);
	return line != null?line:"";
    }

    public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	content.setLines(lines);
	afterChange();
    }

    public String[] getLinesAsArray()
    {
	return content.getLinesAsArray();
    }

    @Override public List<String> getLines()
    {
	return content.getLines();
    }

    public void setLine(int index, String line)
    {
	requireNonNull(line, "line can't be null");
	content.setLine(index, line);
	afterChange();
    }

    @Override public boolean add(String line)
    {
	requireNonNull(line, "line can't be null");
	content.add(line);
	afterChange();
	return true;
    }

    //index is the position of newly inserted line
    public void add(int index, String line)
    {
	requireNonNull(line, "line can't be null");
	content.add(index, line);
	afterChange();
    }

    public void removeLine(int index)
    {
	content.removeLine(index);
	afterChange();
    }

    @Override public void clear()
    {
	content.clear();
	afterChange();
    }

    /*
    @Override public LineMarks getLineMarks(int index)
    {
	return content.getLineMarks(index);
    }

    @Override public void setLineMarks(int index, LineMarks lineMarks)
    {
	NullCheck.notNull(lineMarks, "lineMarks");
	content.setLineMarks(index, lineMarks);
	afterChange();
    }
    */

    @Override public String getAreaName()
    {
	return name;
    }

    public void setName(String name)
    {
	NullCheck.notNull(name, "name");
	this.name = name;
	environment.onAreaNewName(this);
    }

    public String getWholeText()
    {
	return content.getWholeText();
    }

    private void afterChange()
    {
	environment.onAreaNewContent(this);
    }
}
