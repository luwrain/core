// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

import static java.util.Objects.*;

public class EditListPopup extends SimpleEditPopup
{
    static private final int MAX_ALTERNATIVES_TO_SAY = 100;

    public interface Item extends Comparable
    {
	String getValue();
	String getAnnouncement();
    }

    public interface Model
    {
	String getCompletion(String beginning);
	String[] getAlternatives(String beginning);
	//May return null, that means no item
	//Empty value is a usual valid value
	Item getListPopupPreviousItem(String text);
	//May return null, that means no item
	//Empty value is a usual valid value
	Item getListPopupNextItem(String text);
    }

    public interface Appearance
    {
	public enum Flags {BRIEF};

	void announceItem(Item item, Set<Flags> flags);
	String getSpeakableText(String prefix, String text);
    }

    protected final Model model;
    protected final Appearance appearance;

    public EditListPopup(Luwrain luwrain, Model model, Appearance appearance,
			 String name, String prefix, String text, Set<Popup.Flags> popupFlags)
    {
	super(luwrain, name, prefix, text, popupFlags);
	requireNonNull(model, "model can't be null");
	requireNonNull(appearance, "appearance can't be null");
	this.model = model;
	this.appearance = appearance;
    }

    public EditListPopup(Luwrain luwrain, EditListPopup.Model model,
			 String name, String prefix, String text, Set<Popup.Flags> popupFlags)
    {
	this(luwrain, model, new EditListPopupUtils.DefaultAppearance(luwrain), name, prefix, text, popupFlags);
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	requireNonNull(event, "event can't be null");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case TAB:
		onTab();
		return true;
	    case ARROW_DOWN:
		return onMoveDown(false);
	    case ARROW_UP:
		return onMoveUp(false);
	    case ALTERNATIVE_ARROW_DOWN:
		return onMoveDown(true);
	    case ALTERNATIVE_ARROW_UP:
		return onMoveUp(true);
	    default:
		return super.onInputEvent(event);
	    }
	return super.onInputEvent(event);
    }

    protected void onTab()
    {
	final String text = getTextBeforeHotPoint();
	final String after = getTextAfterHotPoint();
	if (!text.isEmpty())
	{
	    final String completion = model.getCompletion(text);
	    if (completion != null && !completion.isEmpty())
	    {
		luwrain.speak(getSpeakableText("", completion));
		setText(text + completion, after);
		return;
	    }
	}
	String[] alternatives = model.getAlternatives(text);
	if (alternatives == null || alternatives.length == 0)
	    return;
	final int count = alternatives.length <= MAX_ALTERNATIVES_TO_SAY?alternatives.length:MAX_ALTERNATIVES_TO_SAY;
	String res = "";
	for(int i = 0;i < count;++i)
	    res += alternatives[i] + " ";
	luwrain.speak(res);
    }

    protected boolean onMoveUp(boolean briefAnnouncement)
    {
	final Item item = model.getListPopupPreviousItem(getTextBeforeHotPoint());
	if (item == null)
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_ABOVE));
	    return true;
	}
	this.appearance.announceItem(item, briefAnnouncement?EnumSet.of(Appearance.Flags.BRIEF):EnumSet.noneOf(Appearance.Flags.class));
	setText(item.getValue(), "");
	return true;
    }

    protected boolean onMoveDown(boolean briefAnnouncement)
    {
	final Item item = model.getListPopupNextItem(getTextBeforeHotPoint());
	if (item == null)
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_BELOW));
	    return true;
	}
	this.appearance.announceItem(item, briefAnnouncement?EnumSet.of(Appearance.Flags.BRIEF):EnumSet.noneOf(Appearance.Flags.class));
	setText(item.getValue(), "");
	return true;
    }

    @Override protected String getSpeakableText(String prefix, String text)
    {
	requireNonNull(prefix, "prefix can't be null");
	requireNonNull(text, "text can't be null");
	return this.appearance.getSpeakableText(prefix, text);
    }
}
