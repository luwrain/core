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

import static java.util.Objects.*;

final class Launch
{
    static private final Logger log = LogManager.getLogger();

    private final Args args;
    private final Config conf = new Config();
    private final ClassLoader classLoader;
    private OperatingSystem os = null;
    private Interaction interaction = null;

    Launch(Args args, File appDir, File dataDir, File userDataDir, File userHomeDir, String lang)
    {
	this.args = requireNonNull(args, "args can't be null");
	conf.setAppDir(requireNonNull(appDir, "appDir can't be null"));
	conf.setDataDir(requireNonNull(dataDir, "dataDir can't be null"));
	conf.setUserDataDir(requireNonNull(userDataDir, "userDataDir"));
	conf.setUserHomeDir(requireNonNull(userHomeDir, "userHomeDir can't be null"));
	conf.setJsDir(new File(appDir, "js"));
	conf.setPacksDir(new File(userDataDir, "packs"));
	conf.setSoundsDir(new File(dataDir, "sounds"));
	log.debug("starting LUWRAIN: Java " + System.getProperty("java.version") + " by " + System.getProperty("java.vendor") + " (installed in " + System.getProperty("java.home") + ")");
	new JniLoader().autoload(this.getClass().getClassLoader());
	if (lang.isEmpty())
	{
	    log.fatal("unable to select a language to use");
	    System.exit(1);
	}
	conf.setLang(lang);
	conf.setArgs(args);
	this.classLoader = this.getClass().getClassLoader();
	conf.setCoreClassLoader(this.classLoader);
    }

    void run()
    {
	try {
	    final Configs configs = new Configs(new File(conf.getUserDataDir(), "conf"));
	    conf.setConfigs(configs);
	    try {
		try {
		    initOs();
		    conf.setOperatingSystem(os);
		    initInteraction();
		    conf.setInteraction(interaction);
		    initTimeZone();
		    new Core(conf).run();
		}
		finally {
		    if (interaction != null)
			interaction.close();
		}
	    }
	    finally {
		configs.close();
	    }
	    System.exit(0);
	}
	catch(Throwable e)
	{
	    log.fatal("Fatal LUWRAIN error , exiting", e);
	    System.err.println();
	    System.err.println("FATAL: " + e.getMessage());
	    System.exit(1);
	}
    }

    private void initOs()
    {
	log.trace("Loading operating system");
	final var instances = new ArrayList<OperatingSystem>();
	ServiceLoader<OperatingSystem> interactionLoader = ServiceLoader.load(OperatingSystem.class);
	for (var instance: interactionLoader)
	{
	    log.trace("Found operating system instance of class " + instance.getClass().getName());
	    instances.add(instance);
	}
	if (instances.isEmpty())
	    throw new IllegalStateException("No operating system instances");
	if (instances.size() > 1)
	    throw new IllegalStateException("There are " + instances.size() + " operating system instances, please explicitly choose which to use");
	this.os = instances.get(0);
	log.trace("Using operating system from the class " + this.os.getClass().getName());
	final InitResult initRes = os.init(null);
	if (initRes == null || !initRes.isOk())
	{
	    if (initRes != null)
		throw new RuntimeException("Unable to initialize operating system");
	}
    }

    private void initInteraction()
    {
	log.trace("Loading interaction");
	final var instances = new ArrayList<Interaction>();
	ServiceLoader<Interaction> interactionLoader = ServiceLoader.load(Interaction.class);
	for (var instance: interactionLoader)
	{
	    log.trace("Found interaction instance of class " + instance.getClass().getName());
	    instances.add(instance);
	}
	if (instances.isEmpty())
	    throw new IllegalStateException("No interaction instances");
	if (instances.size() > 1)
	    throw new IllegalStateException("There are " + instances.size() + " interaction instances, please explicitly choose which to use");
	this.interaction = instances.get(0);
	log.trace("Using interaction from the class " + this.interaction.getClass().getName());
	Interaction.Params params = conf.getConfigs().load(Interaction.Params.class);
	if (params == null)
	{
	    log.trace("No interaction params in configs, using default");
	    params = new Interaction.Params();
	    conf.getConfigs().save(params);
	}
	if (!interaction.init(params,os))
	    throw new RuntimeException("Interaction init failed");
    }

    private void initTimeZone()
    {
	final var sett = conf.getConfigs().load(org.luwrain.io.json.CommonSettings.class);
	if (sett == null || requireNonNullElse(sett.getTimeZone(), "").trim().isEmpty())
	{
	    log.trace("No time zone information, skipping setting the time zone");
	    return;
	}
	final String value = sett.getTimeZone().trim();
	log.trace("Setting the time zone  "+ value);
	final TimeZone timeZone = TimeZone.getTimeZone(value);
	if (timeZone != null)
	{
	    TimeZone.setDefault(timeZone);
	    return;
	}
	log.warn("time zone " + value + " is unknown");
    }

    /*
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
    */
}
