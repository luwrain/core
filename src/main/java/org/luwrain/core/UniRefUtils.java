// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import static java.util.Objects.*;

import org.luwrain.controls.ControlContext;

public final class UniRefUtils
{
    static private final String
	ALIAS = UniRefProcs.TYPE_ALIAS,
	FILE = UniRefProcs.TYPE_FILE;

        static public String makeUniRef(String component, String addr)
    {
	NullCheck.notEmpty(component, "component");
	requireNonNull(addr, "addr can't be null");
	return component + ":" + addr;
    }

    static public UniRefInfo make(String str)
    {
	requireNonNull(str, "str can't be null");
	final String text = str.trim();
	if (text.isEmpty())
	    return new UniRefInfo(UniRefInfo.makeValue(UniRefProcs.TYPE_EMPTY, ""), UniRefProcs.TYPE_EMPTY, "", "");
	return new UniRefInfo(UniRefInfo.makeValue(UniRefProcs.TYPE_STATIC, ""), UniRefProcs.TYPE_STATIC, text, text);
    }

    static public UniRefInfo make(java.io.File file)
    {
	requireNonNull(file, "file can't be null");
	final String path = file.getAbsolutePath();
	return new UniRefInfo(UniRefInfo.makeValue(FILE, path), FILE, path, file.getName());
    }

    static public UniRefInfo make(java.net.URL url)
    {
	requireNonNull(url, "url can't be null");
	final String addr = url.toString();
	return new UniRefInfo(UniRefInfo.makeValue(UniRefProcs.TYPE_URL, addr), UniRefProcs.TYPE_URL, addr, addr);
    }

    static public UniRefInfo make(Luwrain luwrain, Object obj)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	requireNonNull(obj, "obj can't be null");
	if (obj instanceof UniRefInfo)
	    return (UniRefInfo)obj;
	if (obj instanceof java.io.File)
	    return make((java.io.File)obj);
	if (obj instanceof java.net.URL)
	    return make((java.net.URL)obj);
	if (!(obj instanceof String))
	    return null;
	final String value = (String)obj;
	final UniRefInfo uniRefInfo = luwrain.getUniRefInfo(value);
	if (uniRefInfo != null)
	    return uniRefInfo;
	return make(value);
    }

    static public UniRefInfo[] make(Luwrain luwrain, Object[] objs)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	NullCheck.notNullItems(objs, "objs");
	final List<UniRefInfo> res = new ArrayList<>();
	for(Object o: objs)
	{
	    final UniRefInfo info = make(luwrain, o);
	    if (info != null)
		res.add(info);
	}
	return res.toArray(new UniRefInfo[res.size()]);
    }

    static public void defaultAnnouncement(ControlContext context, UniRefInfo info, Sounds defaultSound, Suggestions clickableSuggestion)
    {
	requireNonNull(context, "context can't be null");
	requireNonNull(info, "info can't be null");
	if (!info.isAvailable())
	{
	    context.setEventResponse(DefaultEventResponse.listItem(defaultSound != null?defaultSound:Sounds.LIST_ITEM, getDefaultAnnouncementText(context, info), null));
	    return;
	}
	switch(info.getType())
	{
	case "static":
	    context.setEventResponse(DefaultEventResponse.listItem(defaultSound != null?defaultSound:Sounds.LIST_ITEM, getDefaultAnnouncementText(context, info), null));
	    break;
	case "section":
	    context.setEventResponse(DefaultEventResponse.listItem(Sounds.DOC_SECTION, getDefaultAnnouncementText(context, info), null));
	    break;
	    	case "empty":
		    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    break;
	default:
	    context.setEventResponse(DefaultEventResponse.listItem(defaultSound != null?defaultSound:Sounds.LIST_ITEM, getDefaultAnnouncementText(context, info), clickableSuggestion));
	     return;
	}
    }

    static public String getDefaultAnnouncementText(ControlContext context, UniRefInfo uniRefInfo)
    {
	requireNonNull(context, "context can't be null");
	requireNonNull(uniRefInfo, "uniRefInfo can't be null");
	if (!uniRefInfo.isAvailable())
	    return context.getSpeakableText(uniRefInfo.getValue(), Luwrain.SpeakableTextType.NATURAL);
	switch(uniRefInfo.getType())
	{
	case "file":
	case "url":
	    return context.getSpeakableText(uniRefInfo.getTitle(), Luwrain.SpeakableTextType.PROGRAMMING);
	default:
	    return context.getSpeakableText(uniRefInfo.getTitle(), Luwrain.SpeakableTextType.NATURAL);
	}
    }

    static public String makeAlias(String title, String uniRef)
    {
	NullCheck.notEmpty(title, "title");
	requireNonNull(uniRef, "uniRef can't be null");
	return ALIAS + ":" + title.replaceAll(":", "\\\\:") + ":" + uniRef;
    }


    static boolean isAlias(String uniref)
    {
	requireNonNull(uniref, "uniref can't be null");
	if (uniref.isEmpty())
	    return false;
	return uniref.startsWith(ALIAS + ":");
    }

    static private int findAliasDelim(String aliasBody)
    {
	requireNonNull(aliasBody, "aliasBody can't be null");
	int delim = 0;
	while(delim < aliasBody.length() &&
	      (aliasBody.charAt(delim) != ':' || (delim > 0 && aliasBody.charAt(delim - 1) == '\\')))
	    ++delim;
	return delim < aliasBody.length()?delim:-1;
    }
}
