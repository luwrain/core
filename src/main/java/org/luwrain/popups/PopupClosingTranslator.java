// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

/**
 * Unifies all actions which could result in closing of a popup. There
 * are several actions which mean that the popup must be closed (escape
 * button, closing environment event, accepting a result etc) at that
 * some of the actions mean closing normally and another mean cancelling
 * the popup. This class encapsulates the usual popup behaviour of popup
 * closing, processing various types of events. The popup itself is
 * accessed through {@code Provider} interface and is allowed to accept
 * or reject the recognized actions.
 */
public class PopupClosingTranslator
{
    public interface Provider
    {
	boolean onOk();
	boolean onCancel();
    }

    protected final Provider provider;
    protected boolean shouldContinue = true; 
    protected boolean cancelled = true;

    public PopupClosingTranslator(Provider provider)
    {
	NullCheck.notNull(provider, "provider");
	this.provider = provider;
    }

    public boolean doOk()
    {
	if (!provider.onOk())
	    return false;
	cancelled = false;
	shouldContinue = false;
	return true;
    }

    public boolean doCancel()
    {
	if (!provider.onCancel())
	    return false;
	cancelled = true;
	shouldContinue = false;
	return true;
    }

    public boolean cancelled()
    {
	return cancelled;
    }

    public boolean continueEventLoop()
    {
	return shouldContinue;
    }

    public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!event.isModified() && event.isSpecial() && event.getSpecial() == InputEvent.Special.ESCAPE)
	    return doCancel();
	return false;
    }

    public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return false;
	switch(event.getCode())
	{
	case CANCEL:
	case CLOSE:
	    return doCancel();
	case OK:
	    return doOk();
	default:
	    return false;
	}
    }
}
