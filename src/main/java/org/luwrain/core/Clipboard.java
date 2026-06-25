// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.DataFlavor;

import com.google.gson.*;

import static java.util.Objects.*;
import static java.util.stream.Collectors.*;
import static org.luwrain.util.TextUtils.*;

public final class Clipboard implements ClipboardOwner, java.util.function.Supplier<Object>
{
    static private final Logger log = LogManager.getLogger();

    private final Gson gson = new Gson();
    private Obj<?>[] objs = null;
    private String systemClipboard = null;

    public <E> boolean set(E[] o)
    {
	if (o == null)
	    return false;
	for(int i = 0;i < o.length;i++)
	    if (o[i] == null || o[i].getClass().isArray())
		return false;
		this.objs = new Obj[o.length];
	for(int i = 0;i < o.length;i++)
	    this.objs[i] = saveObj(o[i], o[i].toString());
	setSystemClipboard();
	return true;
    }

    public boolean set(Object[] o, String[] s)
    {
	if (o == null || s == null)
	    return false;
	if (o.length != s.length)
	    return false;
	for(int i = 0;i < o.length;i++)
	{
	    if (o[i] == null || s[i] == null)
		return false;
	    if (o[i].getClass().isArray())
		return false;
	}
	this.objs = new Obj[o.length];
	for(int i = 0;i < o.length;i++)
	    this.objs[i] = saveObj(o[i], s[i]);
	setSystemClipboard();
	return true;
    }

    public boolean set(Object o)
    {
	if (o == null || o.getClass().isArray())
	    return false;
	return set(new Object[]{o});
    }

    private void setSystemClipboard()
    {
	if (this.objs == null || this.objs.length == 0)
	    return;
	try {
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(new String(	Arrays.stream(this.objs)
														.map( e -> e.str)
														.collect(joining(System.lineSeparator())))), this);
	}
	catch(Throwable ex)
	{
	    log.error("Unable to set the system clipboard", ex);
	}
    }
    
    @Override public Object[] get()
    {
	if (this.objs == null)
	    return getSystemClipboard();
	final Object[] res = new Object[this.objs.length];
	for(int i = 0;i < this.objs.length;i++)
	    res[i] = restore(this.objs[i]);
	return res;
    }

    public String[] getStrings()
    {
	if (this.objs == null)
	    return getSystemClipboard();
	final String[] res = new String[this.objs.length];
	for(int i = 0;i < this.objs.length;i++)
	    res[i] = this.objs[i].str;
	return res;
	    }

    public String getString(String lineSep)
    {
	final String[] str = getStrings();
	if (str == null || str.length == 0)
	    return "";
	final StringBuilder b = new StringBuilder();
	b.append(str[0]);
	for(int i = 1;i < str.length;i++)
	    b.append(str[i]).append(lineSep);
	return new String(b);
    }

    private String[] getSystemClipboard()
    {
	final String s ;
	try {
	    s = (String)Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
	    log.trace("System clipboard content is {}", s);
	    if (s == null)
		return new String[0];
	}
	catch(Throwable ex)
	{
	    log.error("Unable to set the system clipboard {}", ex);
	    return null;
	}
	return splitLines(s);
    }

    public boolean isEmpty()
    {
	if (objs == null || objs.length == 0)
	{
	    final String[] s = getSystemClipboard();
	    return s == null || s.length == 0;
	}
	return false;
    }

    public void clear()
    {
	this.objs = null;
	    }

    @SuppressWarnings("unchecked")
    private <E> Obj saveObj(E o, String s)
    {
	requireNonNull(o, "o can't be null");
	requireNonNull(s, "s can't be null");
	if (o instanceof String)
	    return new Obj<String>(String.class, null, o.toString(), (String)o);
	if (o instanceof java.net.URL)
	    return new Obj<java.net.URL>(java.net.URL.class, null, s, (java.net.URL)o);
	    if (o instanceof java.io.File)
			    return new Obj<java.io.File>(java.io.File.class, null, s, (java.io.File)o);
	       if (o instanceof java.net.URI)
	    return new Obj<java.net.URI>(java.net.URI.class, null, s, (java.net.URI)o);
	final StringWriter w = new StringWriter();
	gson.toJson(o, w);
	w.flush();
	return new Obj<E>((Class<E>)o.getClass(), w.toString(), s, null);
    }

    private <E> E restore(Obj<E> obj)
    {
	requireNonNull(obj, "obj can't be null");
	if (obj.obj != null)
	    return obj.obj;
	NullCheck.notNull(obj.content, "obj.content");
	return gson.fromJson(obj.content, obj.cl);
    }

            @Override public void              lostOwnership(java.awt.datatransfer.Clipboard clipboard, Transferable contents)
    {
	log.warn("the clipboard lost ownership");
	this.objs = null;
    }

static private final class Obj<E>
{
    final Class<E> cl;
    final String content;
        final String str;
    final E obj;
    Obj(Class<E> cl, String content, String str, E obj)
    {
	requireNonNull(cl, "cl can't be null");
	requireNonNull(str, "str can't be null");
	this.cl = cl;
	this.content = content;
		this.str = str;
	this.obj = obj;
    }
}
}
