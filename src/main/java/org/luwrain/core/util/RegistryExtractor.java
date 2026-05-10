// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.util;

import java.io.*;
import java.util.*;
import org.luwrain.core.*;
import static java.util.Objects.*;

final class RegistryExtractor
{
    static private final String DIR_PREFIX = "DIR ";
    static private final String FILE_PREFIX = "FILE ";

    private final File destDir;

    private File currentDir = null;
    private File currentFile = null;
    private final List<String> lines = new ArrayList<>();

    RegistryExtractor(File destDir)
    {
	requireNonNull(destDir, "destDir can't be null");
	this.destDir = destDir;
    }

    void extract(InputStream is) throws IOException
    {
	requireNonNull(is, "is can't be null");
	final BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	String line = reader.readLine();
	while(line != null)
	{
	    line = line.trim();
	    if (line.isEmpty() || line.charAt(0) == '#')
	    {
		line = reader.readLine();
		continue;
	    }
	    if (line.startsWith(DIR_PREFIX))
		onDir(line.substring(DIR_PREFIX.length()).trim()); else
		if (line.startsWith(FILE_PREFIX))
		    onFile(line.substring(FILE_PREFIX.length()).trim()); else
		    onValue(line);
	    line = reader.readLine();
	}
	saveLines();
    }

    private void onDir(String path) throws IOException
    {
	requireNonNull(path, "path can't be null");
	if (path.isEmpty())
	    return;
	saveLines();
	currentDir = new File(destDir, path);
	createDirectories(currentDir);
	new File(currentDir, "strings.txt").createNewFile();
	new File(currentDir, "integers.txt").createNewFile();
	new File(currentDir, "booleans.txt").createNewFile();
    }

    private void onFile(String fileName) throws IOException
    {
	requireNonNull(fileName, "fileName can't be null");
	if (fileName.isEmpty())
	    return;
	if (currentDir == null)
	    return;
	saveLines();
	currentFile = new File(currentDir, fileName);
	currentFile.createNewFile();
    }

    private void onValue(String line) throws IOException
    {
	requireNonNull(line, "line can't be null");
	if (line.isEmpty())
	    return;
	if (currentDir == null || currentFile == null)
	    return;
	lines.add(line);
    }

    private void saveLines() throws IOException
    {
	if (currentDir == null || currentFile == null)
	    return;
	try (final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(currentFile, true), "UTF-8"))){
	    for(String s: lines)
	    {
		writer.write(s);
		writer.newLine();
	    }
	}
	lines.clear();
    }

    static private void createDirectories(File file) throws IOException
    {
	requireNonNull(file, "file can't be null");
	final LinkedList<File> files = new LinkedList<>();
	File f = file;
	while(f != null)
	{
	    files.add(f);
	    f = f.getParentFile();
	}
	while(!files.isEmpty())
	{
	    final File dir = files.pollLast();
	    if (!dir.isDirectory())
		dir.mkdir();
	}
    }
}
