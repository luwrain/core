// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.properties;

import java.io.*;
import java.util.*;
import org.luwrain.core.*;
import static java.util.Objects.*;

public final class Braille implements PropertiesProvider
{
    private final org.luwrain.core.BrailleImpl braille;
    private PropertiesProvider.Listener listener = null;

    public Braille(org.luwrain.core.BrailleImpl braille)
    {
	requireNonNull(braille, "braille can't be null");
	this.braille = braille;
    }

    @Override public String getExtObjName()
    {
	return this.getClass().getName();
    }

    @Override public String[] getPropertiesRegex()
    {
	return new String[]{"^luwrain \\.braille\\."};
    }

    @Override public Set<PropertiesProvider.Flags> getPropertyFlags(String propName)
    {
	return EnumSet.of(PropertiesProvider.Flags.PUBLIC);
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	switch(propName)
	{
	case "luwrain.braille.active":
	    return braille.isActive()?"1":"0";
	case "luwrain.braille.driver":
	    return braille.getDriver();
	case "luwrain.braille.error":
	    return braille.getErrorMessage();
	case "luwrain.braille.displaywidth":
	    return "" + braille.getDisplayWidth();
	case "luwrain.braille.displayheight":
	    return "" + braille.getDisplayHeight();
	default:
	    return null;
	}
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
