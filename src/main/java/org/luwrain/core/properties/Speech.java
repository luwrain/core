// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.properties;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.speech.*;

import static java.util.Objects.*;

public final class Speech implements PropertiesProvider
{
    private final org.luwrain.core.Speech speech;
    private PropertiesProvider.Listener listener = null;

    public Speech(org.luwrain.core.Speech speech)
    {
	requireNonNull(speech, "speech can't be null");
	this.speech = speech;
    }

    @Override public String getExtObjName()
    {
	return this.getClass().getName();
    }

    @Override public String[] getPropertiesRegex()
    {
	return new String[]{"^luwrain \\.speech\\."};
    }

    @Override public Set<PropertiesProvider.Flags> getPropertyFlags(String propName)
    {
	return EnumSet.of(PropertiesProvider.Flags.PUBLIC);
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	if (!propName.startsWith("luwrain.speech.channel."))
	    return null;
	//FIXME:
	return null;
    }

    @Override public boolean setProperty(String propName, String value)
    {
	NullCheck.notEmpty(propName, "propName");
	requireNonNull(value, "value can't be null");
	return false;
    }

    @Override public void setListener(PropertiesProvider.Listener listener)
    {
	this.listener = listener;
    }
}
