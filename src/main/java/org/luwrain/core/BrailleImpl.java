// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import static java.util.Objects.*;

public final class BrailleImpl
{
    private Registry registry;
    private Braille braille;
    private boolean active = false;
    private String errorMessage = "";

    void init(Registry registry, Braille braille,
	      EventConsumer eventConsumer)
    {
	requireNonNull(registry, "registry can't be null");
	requireNonNull(eventConsumer, "eventConsumer can't be null");
	this.braille = braille;
	if (braille == null)
	{
	    active = false;
	    errorMessage = "No braille support in the operating system";
	    return;
	}
	final Settings.Braille settings = Settings.createBraille(registry);
	if (!settings.getEnabled(false))
	    return;
	final InitResult res = braille.init(eventConsumer);
	if (res.isOk())
	{
	    active = true;
	    errorMessage = "";
	} else
	{
	    active = false;
	    errorMessage = res.toString();
	}
    }

    void textToSpeak(String text)
    {
	requireNonNull(text, "text can't be null");
	if (braille == null)
	    return;
	braille.writeText(text);
    }

    public boolean isActive()
    {
	return active;
    }

    public String getDriver()
    {
	return braille != null?braille.getDriverName():"";
    }

    public String getErrorMessage()
    {
	return errorMessage;
    }

    public int getDisplayWidth()
    {
	return braille != null?braille.getDisplayWidth():0;
    }

    public int getDisplayHeight()
    {
	return braille != null?braille.getDisplayHeight():0;
    }
}
