// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;

import org.luwrain.core.*;

import static org.apache.commons.io.IOUtils.*;
import static java.util.Objects.*;
import static org.luwrain.core.NullCheck.*;

public final class ResourceUtils
{
    static private final Logger log = LogManager.getLogger();
    
    static public File extractToTempFile(Class c, String resName, boolean loadAsLibrary) throws IOException
    {
	final File tempFile = File.createTempFile("libjni-", ".so");
		log.trace("Requested to create a temp file to save the resource {} from {}", resName, c.getName());
		log.trace("Creating a temp file {}", tempFile.getAbsolutePath());
		try (final var is = c.getResourceAsStream(resName)) {
			try (final var os = new FileOutputStream(tempFile)) {
			    copy(is, os);
			    os.flush();
			}
		    }
	tempFile.deleteOnExit();
	if (loadAsLibrary)
	{
	    log.trace("Loading {} as a library", tempFile.getAbsolutePath());
	        System.load(tempFile.getAbsolutePath());
	}
		    return tempFile;
    }

    
    static public List<String> readStringResourceAsList(Class c, String resourceName, String charset) throws IOException
    {
	requireNonNull(c, "c can't be null");
	requireNonNull(resourceName, "resourceName can't be null");
	requireNonNull(charset, "charset can't be null");
	if (resourceName.isEmpty())
	    throw new IllegalArgumentException("resourceName can't be empty");
	if (charset.isEmpty())
	    throw new IllegalArgumentException("charset can't be empty");
	final var res = new ArrayList<String>();
	try (final BufferedReader r = new BufferedReader(new InputStreamReader(c.getResourceAsStream(resourceName), charset))) {
	    {
		String line = r.readLine();
		while (line != null)
		{
		    res.add(line);
		    line = r.readLine();
		}
		return res;
	    }
	}
    }

    static public String readStringResource(Class c, String resourceName, String charset, String lineSeparator) throws IOException
    {
	notNull(c, "c");
	notEmpty(resourceName, "resourceName");
	notEmpty(charset, "charset");
	notEmpty(lineSeparator, "lineSeparator");
	final StringBuilder b = new StringBuilder();
	try (final BufferedReader r = new BufferedReader(new InputStreamReader(c.getResourceAsStream(resourceName), charset))) {
	    {
		String line = r.readLine();
		while (line != null)
		{
		    b.append(line).append(lineSeparator);
		    line = r.readLine();
		}
		return new String(b);
	    }
	}
    }

    static public String getStringResource(Class c, String resourceName) throws IOException
    {
	return readStringResource(c, resourceName, "UTF-8", System.lineSeparator());
    }
}
