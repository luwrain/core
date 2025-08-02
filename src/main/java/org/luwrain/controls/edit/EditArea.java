/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

//LWR_API 1.0

package org.luwrain.controls.edit;

import java.util.*;
import java.util.concurrent.atomic.*;
import org.apache.logging.log4j.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.script.core.*;
import org.luwrain.script.controls.*;

import static org.luwrain.core.NullCheck.*;

import static java.util.Objects.*;
import static org.luwrain.script.Hooks.*;

public class EditArea extends NavigationArea
{
    static private final Logger log = LogManager.getLogger();

public interface Appearance extends MultilineEdit.Appearance
{
    void announceLine(int index, String line);
}

    public interface ChangeListener
    {
	void onEditChange(EditArea editArea, MarkedLines lines, HotPoint hotPoint);
    }

    public interface EditUpdating
    {
	boolean editUpdate(MutableMarkedLines lines, HotPointControl hotPoint);
    }

    public interface InputEventListener
    {
	boolean onEditAreaInputEvent(EditArea area, InputEvent event);
    }

    public interface EditFactory
    {
	MultilineEdit newMultilineEdit(MultilineEdit.Params params);
    }

    static public final class Params
    {
	public Params() {}
	public Params(ControlContext context)
	{
	    requireNonNull(context, "context can't be null");
	    this.context = context;
	    this.appearance = new EditUtils.DefaultEditAreaAppearance(context);
	    this.inputEventListeners = new ArrayList<>();
	    this.inputEventListeners.add(
					 (edit, event) -> edit.update( (lines, hotPoint) -> chainOfResponsibilityNoExc(context, TEXT_INPUT_ML, new Object[]{
			    //			    new EditAreaObj(edit, lines),
			    			    new EditUpdateObj(lines, hotPoint),
			    new InputEventObj(event)
						 })));
	    this.inputEventListeners.add(
					 					 (edit, event) -> edit.update( (lines, hotPoint) -> chainOfResponsibilityNoExc(context, TEXT_INPUT_SL, new Object[]{
			    			    new EditSingleLineUpdateObj(lines, hotPoint),
			    new InputEventObj(event)
			}))


		);
	}

	public ControlContext context = null;
	public Appearance appearance = null;
	public String name = "";
	public MutableMarkedLines content = null;
	public List<ChangeListener> changeListeners = null;
		public List<InputEventListener> inputEventListeners = null;
	public EditFactory editFactory = null;
    }

    protected final MutableMarkedLines content;
    protected final MultilineEditTranslator translator;
    protected final Appearance appearance;
    protected String areaName = "";
    protected final List<ChangeListener> changeListeners = new ArrayList<>();
    protected final MultilineEdit edit;
    protected final List<InputEventListener> inputEventListeners;

    public EditArea(Params params)
    {
	super(params.context);
	requireNonNull(params, "params can't be null");
	requireNonNull(params.appearance, "params.appearance can't be null");
	requireNonNull(params.name, "params.name can't be null");
	this.areaName = params.name;
	this.content = params.content != null?params.content:new MutableMarkedLinesImpl();
	this.appearance = params.appearance;
	if (params.changeListeners != null)
	    this.changeListeners.addAll(params.changeListeners);
	this.translator = new MultilineEditTranslator(content, this);
	this.edit = createEdit(params);
	if (params.inputEventListeners != null)
	    this.inputEventListeners = new ArrayList<>(params.inputEventListeners); else
	    this.inputEventListeners = new ArrayList<>();
    }

public void setChangeListeners(List<ChangeListener> listeners)
{
requireNonNull(listeners, "listeners can't be null");
this.changeListeners.clear();
this.changeListeners.addAll(listeners);
}

    protected MultilineEdit createEdit(Params areaParams)
    {
	NullCheck.notNull(areaParams, "areaParams");
	if (areaParams.editFactory != null)
	{
	    final var params = new MultilineEdit.Params();
	    params.context = context;
	    params.model = translator;
	    params.appearance = areaParams.appearance;
	    params.regionPoint = regionPoint;
	    final var edit = areaParams.editFactory.newMultilineEdit(params);
	    if (edit != null)
		return edit;
	}
	final var params = new MultilineEdit.Params();
	params.context = context;
	params.model = translator;
	params.appearance = areaParams.appearance;
	params.regionPoint = regionPoint;
	return new MultilineEdit(params);
    }

    public MultilineEdit getEdit()
    {
	return edit;
    }

    @Override public int getLineCount()
    {
	final int value = content.getLineCount();
	return value > 0?value:1;
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	if (index >= content.getLineCount())
	    return "";
	final String line = content.getLine(index);
	return line != null?line:"";
    }

    public void setLine(int index, String line)
    {
	requireNonNull(line, "line can't be null");
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	content.setLine(index, line);
	context.onAreaNewContent(this);
    }

    @Override public String getAreaName()
    {
	return areaName;
    }

    public void setAreaName(String areaName)
    {
	NullCheck.notNull(areaName, "areaName");
	this.areaName = areaName;
	context.onAreaNewName(this);
    }

    public List<String> getTextAsList()
    {
	return Arrays.asList(content.getLines());
    }

    public String[] getText()
    {
	return content.getLines();
    }

        public String getText(String lineSeparator)
    {
	if (content.getLineCount() == 0)
	    return "";
	final StringBuilder b = new StringBuilder();
	b.append(content.getLine(0));
	for(int i = 1;i < content.getLineCount();i++)
	    b.append(lineSeparator).append(content.getLine(i));
	return new String(b);
    }

    public void setText(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	content.setLines(lines);
	context.onAreaNewContent(this);
	setHotPoint(getHotPointX(), getHotPointY());
    }

    public boolean update(EditUpdating updating)
    {
	requireNonNull(updating, "updating can't be null");
	if (!updating.editUpdate(content, this))
	{
	    redraw();
return false;
	}
	redraw();
notifyChangeListeners();
return true;
    }

    public void clear()
    {
	content.clear();
	context.onAreaNewContent(this);
	setHotPoint(0, 0);
    }

    public MutableMarkedLines getContent()
    {
	return content;
    }

    public void refresh()
    {
	redraw();
	context.onAreaNewName(this);
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	requireNonNull(event, "event can't be null");
	if (inputEventListeners != null)
	    for(InputEventListener l: inputEventListeners)
	    {
		log.debug("Running edit input event listener");
		if (l.onEditAreaInputEvent(this, event))
		{
		    log.debug("true result");
		    return true;
		}
	    }
	if (edit.onInputEvent(event))
	{
	    if (translator.commit())
	    {
		refresh();
		notifyChangeListeners();
	    }
	    return true;
	}
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (edit.onSystemEvent(event))
	{
	    if (translator.commit())
	    {
		refresh();
		notifyChangeListeners();
	    }
	    return true;
	}
	return super.onSystemEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	requireNonNull(query, "query can't be null");
	if (edit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

        @Override public void announceLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	appearance.announceLine(index, line);
    }

    public Appearance getEditAppearance()
    {
	return this.appearance;
    }

    protected String getTabSeq()
    {
	return "\t";
    }

    protected void notifyChangeListeners()
    {
	for(ChangeListener l: this.changeListeners)
	    l.onEditChange(this, content, this);
    }
}
