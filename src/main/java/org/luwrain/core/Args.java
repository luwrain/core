// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import com.beust.jcommander.*;

final class Args
{
    @Parameter(names = {"-s", "--speech"},
	       description = "Preferable speech engine",
	       arity = 1,
	       required = false)
	String speech;

        @Parameter(names = "--speech-param",
	       description = "Set a parameter for the used speech engine",
	       variableArity = true,	       required = false)
	List<String> speechParams;

    @Parameter(names = "--def-speech",
	       description = "Default speech engine (used in the case a user has not selected any)",
	       arity = 1,
	       required = false)
	String defSpeech;

        @Parameter(names = "--def-speech-param",
	       description = "Set a parameter for the default speech engine",
	       variableArity = true,	       required = false)
	List<String> defSpeechParams;

        @Parameter(names = "--lang",
	       description = "Preferable language  of user interface",
	       arity = 1,
	       required = false)
	String lang;

            @Parameter(names = "--os",
	       description = "Preferable interface  to operating system",
	       arity = 1,
	       required = false)
	String os;

    @Parameter(names = "--app-dir",
	       description = "The directory with the distribution files",
	       arity = 1,
	       required = false)
	String appDir;

    @Parameter(names = "--std-keys",
	       description = "Use the standard global keys layout",
	       required = false)
	       boolean stdGlobalKeys;

        @Parameter(names = {"--print-dirs"},
	       description = "Print directories information and exit",
	       required = false)
	boolean printDirs;

    @Parameter(names = {"-h", "--help"},
	       description = "Print the help screen and exit",
	       help = true,
	       required = false)
	boolean help;

    static Args parse(String[] args)
    {
	final var a = new Args();
	JCommander cmd = JCommander.newBuilder()
	.addObject(a)
	.build();
	cmd.parse(args);
	if (a.help)
	{
	    cmd.usage();
	    System.exit(0);
	}
	return a;
    }
}
