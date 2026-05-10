// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import static java.util.Objects.*;

public final class UniRefInfo implements Comparable
{
    private final boolean available;
    private final String value;
    private final String type;
    private final String addr;
    private final String title;

    public UniRefInfo(String value)
    {
	requireNonNull(value, "value can't be null");
	this.available = false;
	this.value = value;
	this.type = "";
	this.addr = "";
	this.title = "";
    }

    public UniRefInfo(String value, String type, String addr, String title)
    {
	requireNonNull(value, "value can't be null");
	requireNonNull(type, "type can't be null");
	requireNonNull(addr, "addr can't be null");
	requireNonNull(title, "title can't be null");
	this.available = true;
	this.value = value;
	this.type = type;
	this.addr = addr;
	this.title = title;
    }

    public boolean isAvailable()
    {
	return available;
    }

    public String getValue()
    {
	return value;
    }

    public String getType()
    {
	return type;
    }

    public String getAddr()
    {
	return addr;
    }

    public String getTitle()
    {
	return title;
    }

    @Override public String toString()
    {
	if (!available)
	    return value;
	return title;
    }

    @Override public boolean equals(Object o)
    {
	if (o == null || !(o instanceof UniRefInfo))
	    return false;
	final UniRefInfo uniRef = (UniRefInfo)o;
	return value.equals(uniRef.getValue());
    }

    @Override public int compareTo(Object o)
    {
	if (o == null || !(o instanceof UniRefInfo))
	    return 0;
	final UniRefInfo uniRef = (UniRefInfo)o;
	return value.compareTo(uniRef.getValue());
    }

    static public String makeValue(String type, String addr)
    {
	NullCheck.notEmpty(type, "type");
	requireNonNull(addr, "addr can't be null");
	if (type.indexOf(":") >= 0)
	    throw new IllegalArgumentException("type (" + type + ") can't contain the ':' character");
	final StringBuilder b = new StringBuilder();
	b.append(type).append(":").append(addr);
	return new String(b);
    }
}
