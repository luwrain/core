// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import java.nio.file.*;
import org.apache.logging.log4j.*;

//import com.google.gson.*;

import org.luwrain.io.json.FileType;

import static java.util.Objects.*;

final class FileTypes
{
    static private final Logger log = LogManager.getLogger();
    	static private final String JOB_PREFIX = "job:";

    //    private final Gson gson = new Gson();
    private final Map<String, FileType> fileTypes = new HashMap<>();

    void load(Configs configs)
    {
	requireNonNull(configs, "configs");
	fileTypes.clear();
	final var conf = configs.load(org.luwrain.io.json.FileTypes.class);
	if (conf != null && conf.getTypes() != null)
	    fileTypes.putAll(conf.getTypes());
    }

    void launch(Core core, String[] files)
    {
	requireNonNull(core, "core can't be null");
	requireNonNull(files, "files can't be null");
	final String[] shortcuts = selectShortcuts(files);
	final Map<String, List<String> > lists = new HashMap<>();
	for(int i = 0;i < files.length;++i)
	{
	    final String s = shortcuts[i];
	    final String f = files[i];
	    if (s.isEmpty())
		continue;
	    if (lists.containsKey(s))
	    {
		lists.get(s).add(f);
		continue;
	    }
	    final List<String> l = new ArrayList<>();
	    l.add(f);
	    lists.put(s, l);
	}
	for(Map.Entry<String, List<String> > e: lists.entrySet())
	{
	    if (runJob(core, e.getKey(), e.getValue()))
		continue;
	    final String shortcut = e.getKey();
	    //FIXME: Query shortcut
	    final boolean takesMultiple = false;//FIXME:
	    final boolean takesUrls = false; //FIXME:
	    final String[] toOpen = e.getValue().toArray(new String[e.getValue().size()]);
	    if (takesUrls)
		for(int i = 0;i < toOpen.length;++i)
		{
		    final Path p = Paths.get(toOpen[i]);
		    try {
			toOpen[i] = p.toUri().toURL().toString();
		    }
		    catch(java.net.MalformedURLException exc)
		    {
			log.warn("Unable to generate URL for path " + toOpen[i] + " which is requested to open");
		    }
		}
	    if (!takesMultiple)
	    {
		for(String f: toOpen)
		    core.launchApp(shortcut, new String[]{f});
	    } else
		core.launchApp(shortcut, toOpen);
	}
    }

    private boolean runJob(Core core, String exp, List<String> args)
    {
	/*
	requireNonNull(core, "core can't be null");
	NullCheck.notEmpty(exp, "exp");
	NullCheck.notNull(args, "args");
	if (!exp.startsWith(JOB_PREFIX))
	    return false;
	final JobValue jobValue = gson.fromJson(exp.substring(JOB_PREFIX.length()), JobValue.class);
						if (jobValue == null)
						{
						    log.warn("Unable to parse a job value for file types: " + exp.substring(JOB_PREFIX.length()));
						    return false;
						}
						if (jobValue.name == null || jobValue.name.trim().isEmpty())
						{
						    log.warn("No job value in file types job expression: " + exp.substring(JOB_PREFIX.length()));
						    return false;
						}
						if (jobValue.escaping == null)
						    jobValue.escaping = "cmd";
						if (jobValue.args == null)
						{
						    core.luwrain.newJob(jobValue.name.trim(), new String[0], "", EnumSet.noneOf(Luwrain.JobFlags.class), null);
						    return true;
						}
						final StringBuilder b = new StringBuilder();
						for(String s: args)
						    b.append(" ").append(jobValue.escaping.isEmpty()?s:core.os.escapeString(jobValue.escaping, s));
						final String bashArgs = new String(b).trim();
						for(int i = 0;i < jobValue.args.length;i++)
						    jobValue.args[i] = jobValue.args[i].replaceAll("lwr.args.bash", Matcher.quoteReplacement(bashArgs));
						core.luwrain.newJob(jobValue.name, jobValue.args, "", EnumSet.noneOf(Luwrain.JobFlags.class), null);
						&*/
						return true;
    }

    private String[] selectShortcuts(String[] fileNames)
    {
	NullCheck.notEmptyItems(fileNames, "fileNames");
	final LinkedList<String> res = new LinkedList<String>();
	for(String s: fileNames)
	{
	    if (s.isEmpty())
	    {
		res.add("");
		continue;
	    }
	    final Path path = Paths.get(s);
	    if (!Files.exists(path))
	    {
		res.add("notepad");
		continue;
	    }
	    if (Files.isDirectory(path))
	    {
		res.add("commander");
		continue;
	    }
	    final String ext = getExt(s).trim().toLowerCase();
	    if (ext.trim().isEmpty() || !fileTypes.containsKey(ext))
	    {
		res.add("notepad");
		continue;
	    }
	    final var fileType = fileTypes.get(ext);
	    res.add(fileType != null?fileType.getName():"notepad");
	}
	return res.toArray(new String[res.size()]);
    }

    private String getExt(String fileName)
    {
	requireNonNull(fileName, "fileName can't be null");
	final String name = new File(fileName).getName();
	if (name.isEmpty())
	    return "";
	int dotPos = name.lastIndexOf(".");
	if (dotPos < 1 || dotPos + 1 >= name.length())
	    return "";
	return name.substring(dotPos + 1);
    }

    private String getExtension(URL url)
    {
	requireNonNull(url, "url can't be null");
	final String name = url.getFile();
	if (name.isEmpty())
	    return "";
	final int dotPos = name.lastIndexOf(".");
	if (dotPos < 1 || dotPos + 1 >= name.length())
	    return "";
	return name.substring(dotPos + 1);
    }

    /*
    static private final class JobValue
    {
	String
	    name = null,
	    escaping = null;
	String[] args = null;
    }
    */
}
