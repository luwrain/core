// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core.properties;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public final class PropertiesFiles implements PropertiesProvider
{
    static private final String LOG_COMPONENT = "properties";

    private final Properties props = new Properties();
    private PropertiesProvider.Listener listener = null;

    public void load(File propsDir)
    {
	NullCheck.notNull(propsDir, "propsDir");
	final File[] systemPropertiesFiles = propsDir.listFiles();
	if (systemPropertiesFiles != null)
	    for (File f: systemPropertiesFiles)
		if (f != null && !f.isDirectory())
		    readProps(f);
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
	return EnumSet.of(PropertiesProvider.Flags.PUBLIC);
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
return props.getProperty(propName);
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

    private void readProps(File file)
    {
	NullCheck.notNull(file, "file");
	if (file.isDirectory() || !file.getName().endsWith(".properties"))
	    return;
	try {
	    final InputStream s = new FileInputStream(file);
	    try {
		props.load(new InputStreamReader(new BufferedInputStream(s), "UTF-8"));
	    }
	    finally {
		s.close();
	    }
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to read properties file " + file.getAbsolutePath() + ":" + e.getClass().getName() + ":" + e.getMessage());
	}
	}
}
