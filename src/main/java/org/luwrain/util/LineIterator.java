// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.util;

import java.util.*;
import java.util.stream.*;
import java.io.*;
import java.nio.file.*;

import static java.util.Objects.*;
import static java.util.stream.Collectors.*;
import static java.nio.file.Files.*;

public class LineIterator implements Iterator<String>
{
    static public final String UTF_8 = "UTF_8";

    
    protected final BufferedReader r;
    protected String nextLine;

    public LineIterator(BufferedReader r) throws IOException
    {
	this.r = requireNonNull(r, "r can't be null");
        this.nextLine = r.readLine();
    }

    public LineIterator(Reader r) throws IOException
    {
	this(new BufferedReader(r));
    }

    public LineIterator(InputStream is, String charset) throws IOException
    {
	this(new InputStreamReader(is, charset));
    }

    @Override public boolean hasNext()
    {
        return nextLine != null;
    }
    
    @Override public String next() {
        final String currentLine = nextLine;
        try {
            nextLine = r.readLine();
        }
	catch (IOException e)
	{
	    throw new RuntimeException(e);
        }
        return currentLine;
    }

    public List<String> toList()
    {
	final var a = new ArrayList<String>();
	while (hasNext())
	    a.add(next());
	return a;
    }

    public Stream<String> stream()
    {
	final var a = new ArrayList<String>();
	while(hasNext())
	    a.add(next());
	return a.stream();
    }

    public String join(String sep)
    {
	requireNonNull(sep, "sep can't be null");
	return stream().collect(joining(sep));
    }

            static public List<String> toList(Path path, String charset) throws IOException
    {
	try (final var is = newInputStream(path)) {
	    return new LineIterator(is, charset).toList();
	}
    }

    static public String join(Path path, String charset, String sep) throws IOException
    {
	try (final var is = newInputStream(path)) {
	    return new LineIterator(is, charset).join(sep);
	}
    }

    static public List<String> toList(File file, String charset) throws IOException
    {
	return toList(file.toPath(), charset);
    }



    static public String join(File file, String charset, String sep) throws IOException
    {
	return join(file.toPath(), charset, sep);
    }
    
}
