// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.settings;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.util.function.*;

import com.google.gson.*;
import com.google.gson.reflect.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;
import org.luwrain.io.json.*;

import static java.util.Objects.*;
import static org.luwrain.core.DefaultEventResponse.*;

final class MainMenu extends EditableListArea<UniRefInfo> implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;

    MainMenu(ControlPanel controlPanel, EditableListArea.Params<UniRefInfo> params)
    {
	super(params);
	requireNonNull(controlPanel, "controlPanel can't be null");
	requireNonNull(params, "params can't be null");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
    }

    @Override public boolean saveSectionData()
    {
	final ListArea.Model<UniRefInfo> model = getListModel();
	final var items = new ArrayList<MainMenuItem>();
	for(int i = 0;i < model.getItemCount();i++)
	    items.add(new MainMenuItem(MainMenuItem.TYPE_UNIREF, model.getItem(i).getValue()));
	luwrain.updateConf(CommonSettings.class, c -> c.setMainMenuItems(items));
	return true;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	requireNonNull(event, "event can't be null");
	if (controlPanel.onInputEvent(this, event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	requireNonNull(event, "event can't be null");
	if (controlPanel.onSystemEvent(this, event))
	    return true;
	return super.onSystemEvent(event);
    }


    static MainMenu create(ControlPanel controlPanel)
    {
	//	requireNonNull(controlPanel, "controlPanel");
	final Luwrain luwrain = requireNonNull(controlPanel, "controlPanel can't be null").getCoreInterface();
	//	final Settings.UserInterface sett = null;//FIXME:newreg Settings.createUserInterface(luwrain.getRegistry());
	final var items = new ArrayList<MainMenuItem>();
	final var conf = luwrain.loadConf(CommonSettings.class);
	if (conf != null)
	    items.addAll(requireNonNullElse(conf.getMainMenuItems(), List.of()));
	final List<UniRefInfo> uniRefs = new ArrayList<>();
	if (items != null)
	    for(MainMenuItem item: items)
	    {
		final UniRefInfo info = UniRefUtils.make(luwrain, item.getValue());
		if (info != null)
		    uniRefs.add(info);
	    }
	final EditableListArea.Params<UniRefInfo> params = new EditableListArea.Params<>();
	params.context = new DefaultControlContext(luwrain);
	params.name = luwrain.i18n().getStaticStr("CpMainMenu");
	params.appearance = new Appearance(luwrain);
	params.model = new ListUtils.DefaultEditableModel<UniRefInfo>(UniRefInfo.class, uniRefs){
		@Override public UniRefInfo adjust(Object o)
		{
		    requireNonNull(o, "o can't be null");
		    return UniRefUtils.make(luwrain, o);
		}
	    };
	params.clipboardSaver = new ListUtils.FunctionalClipboardSaver<>(
									 (entry)->{ return entry; },
									 (entry)->entry.getTitle());
	return new MainMenu(controlPanel, params);
    }

    static private final class Appearance extends ListUtils.DoubleLevelAppearance<UniRefInfo>
    {
	static private final String STATIC_PREFIX = "static:";
	Appearance(Luwrain luwrain) { super(new DefaultControlContext(luwrain)); }
	@Override public boolean isSectionItem(UniRefInfo info)
	{
	    requireNonNull(info, "info can't be null");
	    return info.getType().equals(UniRefProcs.TYPE_SECTION);
	}
	@Override public String getSectionScreenAppearance(UniRefInfo info)
	{
	    requireNonNull(info, "info can't be null");
	    final String title = info.getTitle();
	    if (!title.startsWith(STATIC_PREFIX))
		return title;
	    return context.getI18n().getStaticStr(title.substring(STATIC_PREFIX.length()));
	}
    	@Override public String getNonSectionScreenAppearance(UniRefInfo info)
	{
	    requireNonNull(info, "info can't be null");
	    final String title = info.getTitle();
	    if (!title.startsWith(STATIC_PREFIX))
		return title;
	    return context.getI18n().getStaticStr(title.substring(STATIC_PREFIX.length()));
	}
	@Override public void announceNonSection(UniRefInfo info)
	{
	    requireNonNull(info, "info can't be null");
	    context.setEventResponse(text(Sounds.DESKTOP_ITEM, context.getSpeakableText(getNonSectionScreenAppearance(info), Luwrain.SpeakableTextType.NATURAL)));
	}
	public void announceSection(UniRefInfo info)
	{
	    requireNonNull(info, "info can't be null");
	    context.setEventResponse(text(Sounds.DOC_SECTION, context.getSpeakableText(getNonSectionScreenAppearance(info), Luwrain.SpeakableTextType.NATURAL)));//FIXME:DefaultEventResponse.listItem()
	}
    }
}
