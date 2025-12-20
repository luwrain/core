// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.io.*;
import java.net.*;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;



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
	final Args a;
	try {
a = Args.parse(args);
	}
	catch(com.beust.jcommander.ParameterException ex)
	{
	    System.err.println("ERROR: luwrain:" + ex.getMessage());
	    System.exit(1);
	    return;
	}
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

    /*
    void setLogFileName()
    {
	Logger logger = LogManager.getLogger();
		        Appender appender = logger.getAppender("file");

        if (appender instanceof FileAppender) {
            FileAppender fileAppender = (FileAppender) appender;
            fileAppender.setFile("new/path/to/logfile.log");
            try {
                fileAppender.activateOptions();
            }
	    catch (Exception e)
	    {
                e.printStackTrace();
            }
        } else
	{
            System.out.println("Appender not found or not a FileAppender");
        }
    }
    */
}


