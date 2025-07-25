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

package org.luwrain.shell.desktop;

import  com.google.auto.service.*;

import org.luwrain.core.*;
import org.luwrain.shell.*;

import static java.util.Objects.*;

@AutoService(org.luwrain.core.Desktop.class)
public final class Desktop implements org.luwrain.core.Desktop
{
    private Luwrain luwrain = null;
    private String name = "";
    private DesktopArea desktopArea = null;
    private Strings strings = null;
    private Conversations conv = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	this.luwrain = requireNonNull(luwrain, "luwrain can't be null");
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	this.strings = (Strings)o;
	this.conv = new Conversations(luwrain, strings);
	this.name = "";
	final var conf = luwrain.loadConf(org.luwrain.shell.Config.class);
	if (conf != null)
	    this.name = requireNonNullElse(conf.getDesktopTitle(), "").trim();
	if (this.name.isEmpty())
	    this.name = luwrain.i18n().getStaticStr("Desktop");
	this.desktopArea = new DesktopArea(luwrain, name, conv);
	return new InitResult();
    }

    @Override public String getAppName()
    {
	return this.name;
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(desktopArea);
    }

    @Override public void onAppClose()
    {
    }
}
