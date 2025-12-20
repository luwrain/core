// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import static java.nio.file.Files.*;
import static java.util.Objects.*;
import static java.lang.System.*;

public final class TempDir implements AutoCloseable
{
    final Path path;

    public TempDir()
    {
	try {
	    path = createTempDirectory(getBaseDir(), ".lwrtmp-");
	}
	catch(IOException ex)
	{
	    throw new RuntimeException(ex);
	}
    }

    @Override public void close()
    {
	try {
	    try (final var s = walk(path)){
		final var l = new ArrayList<>(s.toList());
		Collections.reverse(l);
		l.forEach(p -> {
			try {
			    delete(p);
			}
			catch(IOException ex)
			{
			    throw new RuntimeException(ex);
			}
		    });
	    }
	}
	catch(IOException ex)
	{
	    throw new RuntimeException(ex);
	}
    }

    public File getFile()
    {
	return path.toFile();
    }

    public Path getPath()
    {
	return path;
    }

    private Path getBaseDir()
    {
	final String env = getenv("TMPDIR");
	if (env != null && !env.isEmpty())
	    return Paths.get(env);
	return Paths.get(getProperty("java.io.tmpdir"));
    }
}
