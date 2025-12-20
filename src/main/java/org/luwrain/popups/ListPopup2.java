// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class ListPopup2<E> extends ListArea<E> implements Popup, PopupClosingTranslator.Provider
{
    protected final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected final Luwrain luwrain;
    protected final Set<Popup.Flags> popupFlags;
    protected Object result = null;

    public ListPopup2(Luwrain luwrain, ListArea.Params<E> params, 
			 Set<Popup.Flags> popupFlags)
    {
	super(params);
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(popupFlags, "popupFlags");
	this.luwrain = luwrain;
	this.popupFlags = popupFlags;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onInputEvent(event))
	    return true;
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		return closing.doOk();
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onSystemEvent(event))
	    return true;
	switch(event.getCode())
	{
	case INTRODUCE:
	    luwrain.silence();
	    luwrain.playSound(Sounds.POPUP);
	    luwrain.speak(getAreaName());
	    return true;
	default:
	return super.onSystemEvent(event);
	}
    }

    @Override public boolean onOk()
    {
	result = selected();
	return result != null;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    public Object result()
    {
	return result;
    }

    @Override public Luwrain getLuwrainObject()
    {
	return luwrain;
    }

    @Override public boolean isPopupActive()
    {
	return closing.continueEventLoop();
    }

    public boolean wasCancelled()
    {
	return closing.cancelled();
    }

    @Override public Set<Popup.Flags> getPopupFlags()
    {
	return popupFlags;
    }
}
