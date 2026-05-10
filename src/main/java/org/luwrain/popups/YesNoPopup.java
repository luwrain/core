// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import static java.util.Objects.*;

public class YesNoPopup implements Popup, PopupClosingTranslator.Provider
{
    protected final Luwrain luwrain;
    protected final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected final String name;
    protected final String text;
    protected boolean res;
    protected final boolean defaultRes;
    protected final Set<Popup.Flags> popupFlags;

    public YesNoPopup(Luwrain luwrain, String name, String text,
		      boolean defaultRes, Set<Popup.Flags> popupFlags)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	requireNonNull(name, "name can't be null");
	requireNonNull(text, "text can't be null");
	requireNonNull(popupFlags, "popupFlags can't be null");
	this.luwrain = luwrain;
	this.name = name;
	this.text = text;
	this.defaultRes = defaultRes;
	this.res = defaultRes;
	this.popupFlags = popupFlags;
    }

    protected String getSpeakableText(String text)
    {
	requireNonNull(text, "text can't be null");
	return luwrain.getSpeakableText(text, Luwrain.SpeakableTextType.NATURAL);
    }

    @Override public int getLineCount()
    {
	return 1;
    }

    @Override public String getLine(int index)
    {
	return index == 0?text:"";
    }

    @Override public int getHotPointX()
    {
	return text.length();
    }

    @Override public int getHotPointY()
    {
	return 0;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	requireNonNull(event, "event can't be null");
	if (closing.onInputEvent(event))
	    return true;
	if (!event.isSpecial())
	{
	    final char c = event.getChar();
	    if (InputEvent.getKeyboardLayout().onSameButton(c, 'y'))
	    {
		res = true;
		closing.doOk();
		return true;
	    }
	    if (InputEvent.getKeyboardLayout().onSameButton(c, 'n'))
	    {
		res = false;
		closing.doOk();
		return true;
	    }
	    return false;
	}
	if (event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case ENTER:
	    closing.doOk();
	    return true;
	case ARROW_UP:
	case ARROW_DOWN:
	case ARROW_LEFT:
	case ARROW_RIGHT:
	    luwrain.speak(getSpeakableText(text));
	    return true;
	default:
	    return false;
	}
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	requireNonNull(event, "event can't be null");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return false;
	switch (event.getCode())
	{
	case CLIPBOARD_COPY:
	case CLIPBOARD_COPY_ALL:
	    luwrain.getClipboard().set(text);
	    return true;
	case INTRODUCE:
	    luwrain.speak(getSpeakableText(text), Sounds.POPUP);
	    return true;
	default:
	return closing.onSystemEvent(event);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	requireNonNull(query, "query can't be null");
	switch(query.getQueryCode())
	{
	case AreaQuery.REGION_TEXT:
	    {
	    if (!(query instanceof RegionTextQuery))
		return false;
	    final RegionTextQuery regionTextQuery = (RegionTextQuery)query;
	    regionTextQuery.answer(text);
	    return true;
	    }
	default:
	    return false;
	}
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public String getAreaName()
    {
	return name;
    }

    public boolean result()
    {
	return res;
    }

    @Override public boolean onOk()
    {
	return true;
    }

    @Override public boolean onCancel()
    {
	return true;
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
