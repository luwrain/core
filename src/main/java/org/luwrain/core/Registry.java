// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface Registry
{
    public static final int INVALID = 0;
    public static final int INTEGER = 1;
    public static final int STRING = 2;
    public static final int BOOLEAN = 3;

    //Returns false if the directory alreayd exists
    boolean addDirectory(String path);
    boolean deleteDirectory(String path);
    boolean deleteValue(String path);
    boolean getBoolean(String path);
    String[] getDirectories(String path);
    int getInteger(String path);
    String getString(String path);
    String getStringDesignationOfType(int type);
    int getTypeOf(String path);
    String[] getValues(String path);
    boolean hasDirectory(String path);
    boolean hasValue(String path);
    boolean setBoolean(String path, boolean value);
    boolean setInteger(String path, int value);
    boolean setString(String path, String value);

    static public String join(String part1, String part2)
    {
	NullCheck.notNull(part1, "part1");
	NullCheck.notNull(part2, "part2");
	if (part1.isEmpty())
	    throw new IllegalArgumentException("part1 may not be empty");
	if (part2.isEmpty())
	    throw new IllegalArgumentException("part2 may not be empty");
	if (part2.charAt(0) == '/')
	    throw new IllegalArgumentException("part2 may not begin with a slash");
	if (part1.endsWith("/"))
	    return part1 + part2;
	return part1 + "/" + part2;
    }

    static public int nextFreeNum(Registry registry, String path)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(path, "path");
	final String[] values = registry.getDirectories(path);
	int res = 0;
	for(String s: values)
	{
	    if (s.isEmpty())
		continue;
	    int value = 0;
	    try {
		value = Integer.parseInt(s);
	    }
	    catch (NumberFormatException e)
	    {
		e.printStackTrace();
		continue;
	    }
	    if (value > res)
		res = value;
	}
	return res + 1;
    }
    }
