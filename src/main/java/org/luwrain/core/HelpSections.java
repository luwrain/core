// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

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
