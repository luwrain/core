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

import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;

import org.luwrain.core.util.*;

import static java.util.Objects.*;

import static org.luwrain.core.Base.*;
import static org.luwrain.core.NullCheck.*;

final class Launch
{
    static private final Logger log = LogManager.getLogger();

    private final Args args;
    private final Config conf;
    private final ClassLoader classLoader;
    private final File dataDir, userDataDir, userHomeDir;
    private final String lang;
    private OperatingSystem os = null;
    private Interaction interaction = null;

    Launch(Args args, File dataDir, File userDataDir, File userHomeDir, String lang)
    {
	this.args = requireNonNull(args, "args can't be null");
	this.dataDir = requireNonNull(dataDir, "dataDir can't be null");
	this.userDataDir = requireNonNull(userDataDir, "userDataDir");
	this.userHomeDir = requireNonNull(userHomeDir, "userHomeDir can't be null");
	this.lang = lang;//Checks.detectLang(this.cmdLine);
	log.debug("starting LUWRAIN: Java " + System.getProperty("java.version") + " by " + System.getProperty("java.vendor") + " (installed in " + System.getProperty("java.home") + ")");
	new JniLoader().autoload(this.getClass().getClassLoader());
	if (lang.isEmpty())
	{
	    log.fatal("unable to select a language to use");
	    System.exit(1);
	}
this.conf = new Config();
	conf.setDataDir(dataDir);
	conf.setUserHomeDir(userHomeDir);
	conf.setUserDataDir(userDataDir);
	conf.setLang(lang);
	this.classLoader = this.getClass().getClassLoader();
    }

    void run()
    {
	try {
	    final UserProfile userProfile = new UserProfile(dataDir, userDataDir, "default", lang);
	    userProfile.userProfileReady();
		userProfile.registryDirReady();
	    init();
	    new Core(conf).run();
	    interaction.close();
	    info("exiting LUWRAIN normally");
	    System.exit(0);
	}
	catch(Throwable e)
	{
	    error(e, "top level exception");
	    fatal("terminating LUWRAIN abnormally");
	    System.exit(1);
	}
    }

    private void init()
    {
	//time zone
	{
	    final Settings.DateTime sett = Settings.createDateTime(null); //FIXME:newreg
	    final String value = sett.getTimeZone("");
	    if (!value.trim().isEmpty())
	    {
		final TimeZone timeZone = TimeZone.getTimeZone(value.trim());
		if (timeZone != null)
		{
		    TimeZone.setDefault(timeZone);
		} else
		    warn("time zone " + value.trim() + " is unknown");
	    }
	}
	initOs();

	//Interaction
	/*
	final InteractionParamsLoader interactionParams = new InteractionParamsLoader();
	interactionParams.loadFromRegistry(null);
	final String interactionClass = props.getProperty("luwrain.class.interaction");
	if (interactionClass.isEmpty())
	{
	    fatal("unable to load the interaction:no luwrain.class.interaction property in loaded properties");
	    System.exit(1);
	}
	interaction = (Interaction)org.luwrain.util.ClassUtils.newInstanceOf(this.classLoader, interactionClass, Interaction.class);
	if (interaction == null)
	{
	    fatal("Unable to create an instance of  the interaction class " + interactionClass);
	    System.exit(1);
	}
	if (!interaction.init(interactionParams,os))
	{
	    fatal("interaction initialization failed");
	    System.exit(1);
	}
	*/

	//Network
	final Settings.Network network = Settings.createNetwork(null);
	//	System.getProperties().put("socksProxyHost", network.getSocksProxyHost(""));
	//	System.getProperties().put("socksProxyPort", network.getSocksProxyPort(""));
	if (!network.getHttpProxyHost("").isEmpty())
	{
	    System.setProperty("java.net.useSystemProxies", "true");
	    System.setProperty("http.proxyHost", network.getHttpProxyHost(""));
	    System.setProperty("https.proxyHost", network.getHttpProxyHost(""));
	    debug("using system proxy: " + System.getProperty("java.net.useSystemProxies"));
	    debug("HTTP proxy host is " + System.getProperty("http.proxyHost"));
	    debug("HTTPS proxy host is " + System.getProperty("https.proxyHost"));
	}
	if (!network.getHttpProxyPort("").isEmpty())
	{
	    System.setProperty("http.proxyPort", network.getHttpProxyPort(""));
	    System.setProperty("https.proxyPort", network.getHttpProxyPort(""));
	    debug("HTTP proxy port is " + System.getProperty("http.proxyPort"));
	    debug("HTTPS proxy port is " + System.getProperty("https.proxyPort"));
	}
	System.getProperties().put("http.proxyUser", network.getHttpProxyUser(""));
	System.getProperties().put("http.proxyPassword",network.getHttpProxyPassword("") );
    }

    private void initOs()
    {
	final String osClass = "";//FIXME:props.getProperty("luwrain.class.os");
	if (osClass.isEmpty())
	{
	    fatal("unable to load the operating system interface:no luwrain.class.os property in loaded core properties");
	    System.exit(1);
	}
	os = (OperatingSystem)org.luwrain.util.ClassUtils.newInstanceOf(classLoader, osClass, OperatingSystem.class);
	if (os == null)
	{
	    fatal("unable to create a new instance of the operating system class " + osClass);
	    System.exit(1);
	}
	final InitResult initRes = os.init(null);
	if (initRes == null || !initRes.isOk())
	{
	    if (initRes != null)
		fatal("unable to initialize operating system with " + os.getClass().getName() + ":" + initRes.toString()); else
		fatal("unable to initialize operating system with " + os.getClass().getName());
	    System.exit(1);
	}
    }


}
