// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;
import java.io.*;

public final class Standalone
{
    static private final File STANDALONE = new File("standalone");
    static private final String ENV_APP_DATA = "APPDATA";
    static private final String ENV_USER_PROFILE = "USERPROFILE";

    private final boolean standalone;
    private final File dataDir;

    public Standalone(String unixDataDirName, String winDataDirName)
    {
	NullCheck.notEmpty(unixDataDirName, "unixDataDirName");
	NullCheck.notNull(winDataDirName, "winDataDirName");
	standalone = STANDALONE.exists() && STANDALONE.isFile();
	// Windows
	if(System.getenv().containsKey(ENV_APP_DATA) && !System.getenv().get(ENV_APP_DATA).trim().isEmpty())
	{
	    final File appData = new File(System.getenv().get(ENV_APP_DATA));
	    dataDir =new File(appData, winDataDirName);
	} else
	    if(System.getenv().containsKey(ENV_USER_PROFILE) && !System.getenv().get(ENV_USER_PROFILE).trim().isEmpty())
	    {
		final File userProfile = new File(System.getenv().get(ENV_USER_PROFILE));
		dataDir = new File(new File(new File(userProfile, "Local Settings"), "Application Data"), winDataDirName);
	    } else
	    {
		// UNIX
		final File f = new File(System.getProperty("user.home"));
		dataDir = new File(f, "." + unixDataDirName);
	    }
    }

    public boolean isStandalone()
    {
	return this.standalone;
    }

    public File getDataDir()
    {
	return this.dataDir;
    }
}
