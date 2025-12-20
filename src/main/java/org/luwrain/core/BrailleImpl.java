// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public final class BrailleImpl
{
    private Registry registry;
    private Braille braille;
    private boolean active = false;
    private String errorMessage = "";

    void init(Registry registry, Braille braille,
	      EventConsumer eventConsumer)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(eventConsumer, "eventConsumer");
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
	NullCheck.notNull(text, "text");
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
