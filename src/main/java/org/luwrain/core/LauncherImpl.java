/*
   Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.io.*;
import java.net.*;

public final class LauncherImpl implements Launcher
{
static private final String
    ENV_WIN_APP_DATA = "APPDATA",
    	ENV_WIN_USER_PROFILE = "USERPROFILE",
    WIN_APP_DATA = System.getenv(ENV_WIN_APP_DATA),
        WIN_USER_PROFILE = System.getenv(ENV_WIN_USER_PROFILE),
WIN_USER_DATA_DIR_NAME = "Luwrain";

    @Override public void launch(String[] args)
    {
	final var a = Args.parse(args);
	final File
	appDir = new File(a.appDir),
	dataDir = getDataDir(a, appDir),
	userHomeDir = getUserHomeDir(a),
	userDataDir = getUserDataDir(a, userHomeDir);
	if (a.printDirs)
	{
	    System.out.println("App: " + appDir.getAbsolutePath());
	    System.out.println("Data: " + dataDir.getAbsolutePath());
	    System.out.println("User home: " + userHomeDir.getAbsolutePath());
	    	    System.out.println("User data: " + userDataDir.getAbsolutePath());
		    return;
	}
	new     Launch(a, appDir, dataDir, userDataDir, userHomeDir, a.lang != null?a.lang.trim():"en").run();
    }

    private File getDataDir(Args args, File appDir)
    {
	return new File(appDir, "data");
    }

    private File getUserHomeDir(Args args)
    {
	return new File(System.getProperty("user.home"));
    }

    private File getUserDataDir(Args args, File userHomeDir)
    {
	//Windows: in Application Data
	if(WIN_APP_DATA != null && !WIN_APP_DATA.trim().isEmpty())
	    return new File(new File(WIN_APP_DATA), WIN_USER_DATA_DIR_NAME);
	if(WIN_USER_PROFILE != null && !WIN_USER_PROFILE.trim().isEmpty())
	    return new File(new File(new File(new File(WIN_USER_PROFILE), "Local Settings"), "Application Data"), WIN_USER_DATA_DIR_NAME);
	//We are likely on UNIX
	return new File(new File(new File(userHomeDir, ".local"), "luwrain"), "default");
    }

}
