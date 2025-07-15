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

package org.luwrain.shell;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.*;
import org.luwrain.util.*;

import static java.util.Objects.*;

public final class Conversations
{
    private final Luwrain luwrain;
    public Conversations(Luwrain luwrain)
    {
	this.luwrain = requireNonNull(luwrain, "luwrain can't be null");
    }

    public File open()
    {
	final var current = new File(luwrain.getActiveAreaAttr(Luwrain.AreaAttr.DIRECTORY));
	final var popup = new FilePopup(luwrain, 
					luwrain.i18n().getStaticStr("OpenPopupName"), luwrain.i18n().getStaticStr("OpenPopupPrefix"), 
					null, current, new File(luwrain.getDir("~")),
					Popups.loadFilePopupFlags(luwrain), Popups.DEFAULT_POPUP_FLAGS){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    requireNonNull(event, "event can't be null");
		    if (event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case INSERT:
			    {
				if (text().isEmpty())
				    return false;
				final File file = FileUtils.ifNotAbsolute(luwrain.getFileProperty("luwrain.dir.userhome"), text());
				if (file.exists())
				{
				    if (file.isDirectory())
					luwrain.message(luwrain.i18n().getStaticStr("DirAlreadyExists"), Luwrain.MessageType.ERROR); else
					luwrain.message(luwrain.i18n().getStaticStr("FileAlreadyExists"), Luwrain.MessageType.ERROR);
				    return true;
				}
				if (file.mkdir())
				    luwrain.message(luwrain.i18n().getStaticStr("DirCreated"), Luwrain.MessageType.OK); else
				    luwrain.message(luwrain.i18n().getStaticStr("UnableToCreateDir"), Luwrain.MessageType.ERROR);
			    }
			    return true;
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    switch(query.getQueryCode())
		    {
		    case AreaQuery.UNIREF_AREA:
			{
			    final File f = FileUtils.ifNotAbsolute(luwrain.getFileProperty("luwrain.dir.userhome"), text);
			    if (f.getAbsolutePath().isEmpty())
				return false;
			    ((UniRefAreaQuery)query).answer("file:" + f.getAbsolutePath());
			    return true;
			}
		    default:
			return super.onAreaQuery(query);
		    }
		}
	    };
	luwrain.popup(popup);
	if (popup.wasCancelled())
	    return null;
	return popup.result();
    }

    public String command(String[] allCommands)
    {
	requireNonNull(allCommands, "allCommands can't be null");
	final EditListPopup popup = new EditListPopup(luwrain, new EditListPopupUtils.FixedModel(allCommands), new EditListPopupUtils.DefaultAppearance(luwrain, Luwrain.SpeakableTextType.PROGRAMMING),
						      luwrain.i18n().getStaticStr("CommandPopupName"), luwrain.i18n().getStaticStr("CommandPopupPrefix"), "", EnumSet.noneOf(Popup.Flags.class)){
		@Override public boolean onOk()
		{
		    final String t = text.trim();
		    if (t.isEmpty())
		    {
			luwrain.playSound(Sounds.INACCESSIBLE);
			return false;
		    }
		    for(String c: allCommands)
			if (t.equals(c))
			    return true;
		    luwrain.message(luwrain.i18n().getStaticStr("NoCommand"), Luwrain.MessageType.ERROR);
		    return false;
		}
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    requireNonNull(query, "query can't be null");
		    switch(query.getQueryCode())
		    {
		    case AreaQuery.UNIREF_AREA:
			if (text.trim().isEmpty())
			    return false;
			((UniRefAreaQuery)query).answer("command:" + text().trim());
			return true;
		    default:
			return super.onAreaQuery(query);
		    }
		}
	    };
	luwrain.popup(popup);
	if (popup.wasCancelled())
	    return null;
	return !popup.text().isEmpty()?popup.text():null;
    }

    public boolean deleteDesktopItemConfirmation(String name)
    {
	requireNonNull(name, "name can't be null");
	final var popup = new YesNoPopup(luwrain, 
						"Удаление элемента",//FIXME:
						"Вы действительно хотите удалить элемент \"" + name + "\" с рабочего стола?",//FIXME:
						false,
						Popups.DEFAULT_POPUP_FLAGS);
	luwrain.popup(popup);
	return !popup.wasCancelled() && popup.result();
    }

    public boolean deleteDesktopItemsConfirmation(int count)
    {
	final YesNoPopup popup = new YesNoPopup(luwrain, 
						"Удаление элемента",//FIXME:
						"Вы действительно хотите удалить " + luwrain.i18n().getNumberStr(count, "items") + " с рабочего стола?",//FIXME:
						false,
						Popups.DEFAULT_POPUP_FLAGS);
	luwrain.popup(popup);
	return !popup.wasCancelled() && popup.result();
    }
}
