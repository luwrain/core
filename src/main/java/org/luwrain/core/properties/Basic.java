// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.properties;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public final class Basic implements PropertiesProvider
{
    private final File dataDir;
    private final File userDataDir;
    private final File userHomeDir;

    private PropertiesProvider.Listener listener = null;

    public Basic(File dataDir,
	 File userDataDir,
	 File userHomeDir)
    {
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(userDataDir, "userDataDir");
	NullCheck.notNull(userHomeDir, "userHomeDir");
	this.dataDir = dataDir;
	this.userDataDir = userDataDir;
	this.userHomeDir = userHomeDir;
    }

    @Override public String getExtObjName()
    {
	return this.getClass().getName();
    }

    @Override public String[] getPropertiesRegex()
    {
	return new String[0];
    }

    @Override public Set<PropertiesProvider.Flags> getPropertyFlags(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	final String value = getProperty(propName);
	if (value != null)
	    return EnumSet.of(PropertiesProvider.Flags.PUBLIC,
			      PropertiesProvider.Flags.FILE);
	return null;
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notNull(propName, "propName");
	switch(propName)
	{
	case "luwrain.dir.userhome":
	    return userHomeDir.getAbsolutePath();
	case "luwrain.dir.data":
	    return dataDir.getAbsolutePath();
	case "luwrain.dir.scripts":
	    return new File(dataDir, "scripts").getAbsolutePath();
	case "luwrain.dir.js":
	    return new File(dataDir, "js").getAbsolutePath();
	    	case "luwrain.dir.textext":
		    return new File(dataDir, "text").getAbsolutePath();
	case "luwrain.dir.properties":
	    return new File(dataDir, "properties").getAbsolutePath();
	case "luwrain.dir.sounds":
	    return new File(dataDir, "sounds").getAbsolutePath();
	case "luwrain.dir.userdata":
	    return userDataDir.getAbsolutePath();
	case "luwrain.dir.appdata":
	    return new File(userDataDir, "var").getAbsolutePath();
	    	case "luwrain.dir.packs":
		    return new File(userDataDir, "extensions").getAbsolutePath();
	default:
	    return null;
	}
    }

    @Override public boolean setProperty(String propName, String value)
    {
	NullCheck.notEmpty(propName, "propName");
	NullCheck.notNull(value, "value");
	return false;
    }

    @Override public void setListener(PropertiesProvider.Listener listener)
    {
	this.listener = listener;
    }
}
