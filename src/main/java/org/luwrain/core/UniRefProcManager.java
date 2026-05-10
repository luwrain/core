// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

import org.luwrain.core.events.*;

import static java.util.Objects.*;

final class UniRefProcManager
{
    static private final UniRefInfo EMPTY = new UniRefInfo(UniRefProcs.TYPE_EMPTY + ":", UniRefProcs.TYPE_EMPTY, "", "");

    private final Map<String, Entry> uniRefProcs = new HashMap<String, Entry>();

    boolean add(Luwrain luwrain, UniRefProc uniRefProc)
    {
	requireNonNull(luwrain, "luwrain can't be null");
	requireNonNull(uniRefProc, "uniRefProc can't be null");
	final String uniRefType = uniRefProc.getUniRefType();
	if (uniRefType == null || uniRefType.trim().isEmpty())
	    return false;
	if (uniRefProcs.containsKey(uniRefType))
	    return false;
	uniRefProcs.put(uniRefType, new Entry(luwrain, uniRefType, uniRefProc));
	return true;
    }

    UniRefInfo getInfo(String uniRef)
    {
	requireNonNull(uniRef, "uniRef can't be null");
	if (uniRef.trim().isEmpty())
	    return EMPTY;
	final String uniRefType = getUniRefType(uniRef);
	if (uniRefType == null || uniRefType.trim().isEmpty())
	    return new UniRefInfo(UniRefProcs.TYPE_STATIC + ":" + uniRef.trim(), UniRefProcs.TYPE_STATIC, "", uniRef.trim());
	if (!uniRefProcs.containsKey(uniRefType))
	    return new UniRefInfo(uniRef);
	final Entry entry = uniRefProcs.get(uniRefType);
	final UniRefInfo res = entry.uniRefProc.getUniRefInfo(uniRef);
	if (res == null)
	    return new UniRefInfo(uniRef);
	return res;
    }

    boolean open(String uniRef)
    {
	NullCheck.notEmpty(uniRef, "uniRef");
	final String uniRefType = getUniRefType(uniRef);
	if (uniRefType == null || uniRefType.trim().isEmpty())
	    return false;
	if (!uniRefProcs.containsKey(uniRefType))
	    return false;
	final Entry entry = uniRefProcs.get(uniRefType);
	return entry.uniRefProc.openUniRef(uniRef, entry.luwrain);
    }

    private String getUniRefType(String uniRef)
    {
	final int pos = uniRef.indexOf(':');
	if (pos < 1)
	    return null;
	for(int i = 0;i < pos;i++)
	    if (Character.isSpaceChar(i) || Character.isWhitespace(uniRef.charAt(i)) || Character.isISOControl(uniRef.charAt(i)))
		return null;
	return uniRef.substring(0, pos);
    }

    static private final class Entry 
    {
	final Luwrain luwrain;
	final String uniRefType;
	final UniRefProc uniRefProc;
	Entry(Luwrain luwrain, 
	      String uniRefType, UniRefProc uniRefProc)
	{
	    requireNonNull(luwrain, "luwrain can't be null");
	    requireNonNull(uniRefType, "uniRefType can't be null");
	    requireNonNull(uniRefProc, "uniRefProc can't be null");
	    if (uniRefType.trim().isEmpty())
		throw new IllegalArgumentException("uniRefType may not be empty");
	    this.luwrain = luwrain;
	    this.uniRefType = uniRefType;
	    this.uniRefProc = uniRefProc;
	}
    }
}
