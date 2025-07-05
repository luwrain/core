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

package org.luwrain.core;

import java.util.*;

import static java.util.Objects .*;

final class HelpSections
{
    static private final class Config
    {
	Map<String, String> sections;
    }

    private final Configs conf;

    HelpSections(Configs conf)
    {
	this.conf = requireNonNull(conf, "conf can't be null");
    }

    String getSectionUrl(String sectName)
    {
	requireNonNull(sectName, "sectName can['t be null");
	final var c = conf.load(Config.class);
	if (c == null || c.sections == null || !c.sections.containsKey(sectName))
	    return null;
	return c.sections.get(sectName);
    }
}
