
package org.luwrain.core;

import java.util.*;
import com.beust.jcommander.*;

final class Args
{
    @Parameter(names = "--app-dir",
	       description = "The directory with the distribution files",
	       arity = 1,
	       required = false)
	String appDir;

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
