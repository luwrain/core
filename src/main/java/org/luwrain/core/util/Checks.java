// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.util;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public final class Checks
{
    static private final String LOG_COMPONENT = "Checks";

    static private final String DEFAULT_LANG = "en";
    static private final String ENV_LANG = "LUWRAIN_LANG";
    static public final String CMDARG_LANG = "--lang=";

    static public String detectLang(CmdLine cmdLine)
    {
	NullCheck.notNull(cmdLine, "cmdLine");
	final String cmdLineArg = cmdLine.getFirstArg(CMDARG_LANG);
	if (cmdLineArg != null)
	    switch(cmdLineArg.trim().toLowerCase())
	    {
	    case "ru":
	    case "en":
	    case "ro":
		return cmdLineArg.trim().toLowerCase();
	    default:
		Log.error(LOG_COMPONENT, "unknown language \'" + cmdLineArg + "\' in the command line options");
		return "";
	    }
	if(System.getenv().containsKey(ENV_LANG) && !System.getenv().get(ENV_LANG).trim().isEmpty())
	{
	    final String lang = System.getenv().get(ENV_LANG).toLowerCase().trim();
	    switch(lang)
	    {
	    case "en":
	    case "ru":
	    case "ro":
		return lang;
	    default:
		Log.warning(LOG_COMPONENT, "the environment variable " + ENV_LANG + " contains an improper value \'" + lang + "\', ignoring it");
	    }
	}
	final String lang = Locale.getDefault().getISO3Language().trim().toLowerCase();
	switch(lang)
	{
	case "eng":
	    return "en";
	case "rus":
	    return "ru";
	default:
	    Log.warning(LOG_COMPONENT, "locale detects the UI language as " + lang + ", but it isn\'t supported, using the default language " + DEFAULT_LANG);
	    return DEFAULT_LANG;
	}
    }
}
