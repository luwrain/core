// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

import static java.util.Objects.*;

public class DefaultSection implements Section
{
    public interface AreaFactory
    {
	SectionArea newSectionArea(ControlPanel controlPanel);
    }

    public interface ActionHandler
    {
	boolean onSectionActionEvent(ControlPanel controlPanel, ActionEvent event);
    }

    protected final Element element;
    protected final String name;
    protected final AreaFactory areaFactory;
    protected final ActionHandler actionHandler;
    protected final Action[] actions;

    private SectionArea area = null;

    public DefaultSection(Element element, String name)
    {
	this.element = requireNonNull(element, "element can't be null");
	this.name = requireNonNull(name, "name can't be null");
	this.areaFactory = null;
	this.actionHandler = null;
	this.actions = new Action[0];
    }

    public DefaultSection(Element element, String name, AreaFactory areaFactory)
    {
	this.element = requireNonNull(element, "element can't be null");
	this.name = requireNonNull(name, "name can't be null");
	this.areaFactory = areaFactory;
	this.actionHandler = null;
	this.actions = new Action[0];
    }

    public DefaultSection(Element element, String name, AreaFactory areaFactory, Action[] actions, ActionHandler actionHandler)
    {
	this.element = requireNonNull(element, "element can't be null");
	this.name = requireNonNull(name, "name can't be null");
		this.areaFactory = areaFactory;
		this.actions = requireNonNullElse(actions, new Action[0]);
	this.actionHandler = actionHandler;
    }

    @Override public SectionArea getSectionArea(ControlPanel controlPanel)
    {
	requireNonNull(controlPanel, "controlPanel can't be null");
	if (area != null)
	    return area;
	if (areaFactory == null)
	    return null;
	area = areaFactory.newSectionArea(controlPanel);
	return area;
    }

    @Override public Element getElement()
    {
	return element;
    }

    @Override public Action[] getSectionActions()
    {
	return actions.clone();
    }

    @Override public boolean onSectionActionEvent(ControlPanel controlPanel, ActionEvent event)
    {
	requireNonNull(controlPanel, "controlPanel can't be null");
	requireNonNull(event, "event can't be null");
	if (actionHandler == null)
	    return false;
	return actionHandler.onSectionActionEvent(controlPanel, event);
    }

    @Override public String toString()
    {
	return name;
    }
}
