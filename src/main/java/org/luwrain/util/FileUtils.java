// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static java.util.Objects.*;
import static java.nio.file.Files.*;
import static org.luwrain.util.StreamUtils.*;
import static org.luwrain.util.Sha1.*;

public final class FileUtils
{
    static public final String UTF_8 = "UTF_8";

    static public String writeRandomFile(Path path, int len) throws IOException
    {
	byte[] data = new byte[len];
	new Random().nextBytes(data);
	write(path, data);
	return getSha1(data);
    }

    static public String readTextFile(File file, String charset) throws IOException
    {
	requireNonNull(file, "file can't be null");
	requireNonNull(charset, "charset can't be null");
	if (charset.isEmpty())
	    throw new IllegalArgumentException("charset can't be empty");
	try (final var is = new FileInputStream(file)) {
    	    return new String(readAllBytes(is), charset);
	}
    }

    static public String readTextFile(File file) throws IOException
    {
	return readTextFile(file, UTF_8);
    }

    static public void writeTextFile(File file, String text, String charset) throws IOException
    {
	requireNonNull(file, "file can't be null");
	requireNonNull(text, "text can't be null");
	requireNonNull(charset, "charset can't be null");
	if (charset.isEmpty())
	    throw new IllegalArgumentException("charset can't be empty");
	final OutputStream os = new FileOutputStream(file);
	try {
	    writeAllBytes(os, text.getBytes(charset));
	}
	finally {
	    os.flush();
	    os.close();
	}
    }

    //lineSeparator may be null, means use default 
    static public String[] readTextFileMultipleStrings(File file, String charset, String lineSeparator) throws IOException
    {
	requireNonNull(file, "file can't be null");
	requireNonNull(charset, "charset can't be null");
	if (charset.isEmpty())
	    throw new IllegalArgumentException("charset can't be empty");
	final String text = readTextFile(file, charset);
	if (text.isEmpty())
	    return new String[0];
	return text.split(lineSeparator != null?lineSeparator:System.getProperty("line.separator"), -1);
    }

    static public File ifNotAbsolute(File baseDir, String path)
    {
	requireNonNull(baseDir, "baseDir can't be null");
	requireNonNull(path, "path can't be null");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path can't be empty");
	final File file = new File(path);
	if (file.isAbsolute())
	    return file;
	return new File(baseDir, path);
    }
}
