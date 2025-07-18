/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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
import java.util.concurrent.atomic.*;
import java.io.*;

import org.luwrain.core.events.*;
import org.luwrain.core.listening.*;
import org.luwrain.script.Hooks;

import static org.luwrain.core.NullCheck.*;
import static java.util.Objects.*;

final class Core extends EventDispatching
{
    private final ClassLoader classLoader;
    final OperatingSystem os;
    final Interaction interaction;
boolean standalone;
    private final org.luwrain.shell.Conversations conversations;
    org.luwrain.player.Player player = null;
        private Application desktop = null;
    final WavePlayers.Player wavePlayer = new WavePlayers.Player();
    private volatile boolean wasInputEvents = false;
    final UniRefProcManager uniRefProcs = new UniRefProcManager();//FIXME:

    Core(Config conf)
    {
	super(conf);
	this.classLoader = requireNonNull(conf.getCoreClassLoader(), "conf.coreClassLoader can't be null");
	this.os = requireNonNull(conf.getOperatingSystem(), "conf.operatingSystem can't be null");
	this.interaction = requireNonNull(conf.getInteraction());
	this.conversations = new org.luwrain.shell.Conversations(luwrain);
    }

    void run()
    {
	init();
    interaction.startInputEventsAccepting(this);
	windowManager.redraw();
	//soundManager.startingMode();
	workers.doWork(objRegistry.getWorkers());
	Hooks.chainOfResponsibilityNoExc(luwrain, Hooks.STARTUP, new Object[0]);
	eventLoop(mainStopCondition);
	workers.finish();
	soundManager.playIcon(Sounds.SHUTDOWN);
	    try {
		Thread.sleep(3000);//FIXME:
	    } catch (InterruptedException ie)
	    {
		Thread.currentThread().interrupt();
	    }
	interaction.stopInputEventsAccepting();
	extensions.close();
    }

        @Override public void onBeforeEventProcessing()
    {
		  stopAreaListening();
	  wasInputEvents = true;
    }

    @Override protected void processEventResponse(EventResponse eventResponse)
    {
	notNull(eventResponse, "eventResponse");
	//FIXME:access level
	final EventResponse.Speech s = new org.luwrain.core.speech.EventResponseSpeech(speech, i18n, speakingText);
	eventResponse.announce(luwrain, s);
    }

    Area getActiveArea(boolean speakMessages)
    {
	final Area activeArea = tiles.getActiveArea();
	if (activeArea == null)
	{
	    if (speakMessages)
		noAppsMessage();
	    return null;
	}
	return activeArea;
    }

    @Override public void onAltX()
    {
	final String cmdName = conversations.command(commands.getCommandNames());
	if (cmdName == null || cmdName.trim().isEmpty())
	    return;
	if (cmdName.trim().startsWith("app "))
	{
	    if (!runAppCommand(cmdName.trim()))
			    message(i18n.getStaticStr("NoCommand"), Luwrain.MessageType.ERROR);
	    return;
	}
	if (!commands.run(cmdName.trim()))
	    message(i18n.getStaticStr("NoCommand"), Luwrain.MessageType.ERROR);
    }

        private boolean runAppCommand(String command)
    {
	notEmpty(command, "command");
	if (!command.startsWith("app "))
	    return false;
	final String params = command.substring("app ".length());
	String shortcut = null;
	final List<String> args = new ArrayList<>();
	//FIXME:quotes
	for(String s: params.split(" ", -1))
	    if (!s.trim().isEmpty())
	    {
		if (shortcut == null)
		    shortcut = s.trim(); else
		    args.add(s.trim());
	    }
	if (shortcut == null)
	    return false;
	launchApp(shortcut, args.toArray(new String[args.size()]));
	return true;
    }

    private void init()
    {
	extensions.load((ext)->interfaces.requestNew(ext), this.classLoader);
	initObjects();
	for (ScriptFile f: extensions.getScriptFiles("core"))
	    try {
		loadScript(f);
	    }
	    catch(ExtensionException e)
	    {
		error(e, "unable to load script " + f.toString());
	    }
	initI18n();
	objRegistry.add(null, new StartingModeProperty());
	speech.init();
	//braille.init(null, os.getBraille(), this);
	globalKeys.load();
	fileTypes.load(configs);
	loadPlayer();
	loadDesktop();
	//	props.setProviders(objRegistry.getPropertiesProviders());
	//	uiSettings = null;//FIXME:newreg Settings.createUserInterface(registry);
    }

    String loadScript(ScriptSource script) throws ExtensionException
    {
	requireNonNull(script, "script can't be null");
	mainCoreThreadOnly();
	final var ext = new org.luwrain.script.core.ScriptExtension(script.toString()){
		@Override public void launchApp(Application app)
		{
		    requireNonNull(app, "app can't be null");
		    Core.this.launchApp(app);
		}
	    };
	ext.init(interfaces.requestNew(ext));
	try {
	    ext.getScriptCore().load(script);
	}
	catch(Throwable e)
	{
	    interfaces.release(ext.getLuwrainObj());
	    throw new ExtensionException(e);
	}
	final var entry = new ExtensionsManager.Entry(ext, ext.getLuwrainObj());
	extensions.extensions.add(entry);
	//	objRegistry.takeObjects(ext, ext.getLuwrainObj());
	objRegistry.takeObjects(ext, entry.extObjects);
	for(Command c: ext.getCommands(ext.getLuwrainObj()))
	    commands.add(ext.getLuwrainObj(), c);
	return entry.id;
    }

    private void initObjects()
    {
	for(Command sc: Commands.getCommands(this, conversations))
	    commands.add(luwrain, sc);//FIXME:
	if (!standalone)
	    for(Command sc: Commands.getNonStandaloneCommands(this, conversations))
		commands.add(luwrain, sc);//FIXME:
	final UniRefProc[] standardUniRefProcs = UniRefProcs.createStandardUniRefProcs(luwrain);
	for(UniRefProc proc: standardUniRefProcs)
	    uniRefProcs.add(luwrain, proc);//FIXME:
	for(final var e: extensions.extensions)
	{
	    //	    objRegistry.takeObjects(e.ext, e.luwrain);
	    objRegistry.takeObjects(e.ext, e.extObjects);
	    //	    final Extension ext = e.ext;
	    //FIXME:
	    for(UniRefProc p: e.ext.getUniRefProcs(e.luwrain))
		if (!uniRefProcs.add(luwrain, p))
		    warn("the uniRefProc \'" + p.getUniRefType() + "\' of extension " + e.getClass().getName() + " has been refused by  the uniRefProcs manager to be registered");
	    //FIXME:
	    for(Command c: e.ext.getCommands(e.luwrain))
		if (!commands.add(luwrain, c))
		    warn("command \'" + c.getName() + "\' of extension " + e.getClass().getName() + " has been refused by  the commands manager to be registered");
	}
    }

    private void initI18n()
    {
	for(final var e: extensions.extensions)
	    try {
		e.ext.i18nExtension(e.luwrain, i18n);
	    }
	    catch (Exception ex)
	    {
		error(ex, "extension " + e.getClass().getName() + " thrown an exception on i18n");
	    }
	if (!i18n.selectLang(lang))
	{
	    Log.fatal("core", "unable to choose matching language for i18n, requested language is \'" + lang + "\'");
	    return;
	}
    }

    private void loadPlayer()
    {
	final var factories = new ArrayList<org.luwrain.player.Factory>();
	for(var f: ServiceLoader.load(org.luwrain.player.Factory.class))
	    factories.add(f);
	if (factories.isEmpty())
	{
	    LOGGER.warn("No loaded player factories, no player");
	    player = null;
	    return;
	}
	LOGGER.trace("Loaded player factory class is " + factories.get(0).getClass().getName());
	try {
	    final var params = new org.luwrain.player.Factory.Params();
	    params.luwrain = luwrain;
	    player = factories.get(0).newPlayer(params);
	    if (player == null)
	    {
		LOGGER.error("Player factory of the class " + factories.get(0).getClass().getName() + " has returned null, no player");
		return;
	    }
	    LOGGER.info("Loaded player class is " + player.getClass().getName());
	    /*
	    for (PropertiesProvider p: props.getBasicProviders())
		if (p instanceof org.luwrain.core.properties.Player)
		{
		    player.addListener((org.luwrain.player.Listener)p);
		    break;
		}
	    **/
	}
	catch(Throwable ex)
	{
	    LOGGER.error("Unable to load the player implementation", ex);
	    player = null;
	    return;
	}
    }

    private void loadDesktop()
    {
	final var desktops = new ArrayList<Desktop>();
	for (var d: ServiceLoader.load(Desktop.class))
	    desktops.add(d);
	if (desktops.isEmpty())
	    throw new RuntimeException("No desktop providers");
	desktop = desktops.get(0);
	LOGGER.trace("Loaded desktop class is " + desktop.getClass().getName());
	final var initRes = desktop.onLaunchApp(interfaces.requestNew(desktop));
	if (!initRes.isOk())
	{
	    LOGGER.error("Unable to init the desktop app: " + initRes.toString());
	    throw new RuntimeException("Unable to init the desktop app of the class " + desktop.getClass().getName());
	}
	apps.setDesktopApp(desktop);
    }

    void quit()
    {
	    mainStopCondition.stop();
    }

    //It is admissible situation if shortcut returns null
    void launchApp(String shortcutName, String[] args)
    {
	notEmpty(shortcutName, "shortcutName");
	notNullItems(args, "args");
	debug("launching the app \'" + shortcutName + "\' with " + args.length + " argument(s)");
	mainCoreThreadOnly();
	for(int i = 0;i < args.length;++i)
	    debug("args[" + i + "]: " + args[i]);
	final Shortcut shortcut = objRegistry.getShortcut(shortcutName);
	if (shortcut == null)
	{
	    message("Нет приложения с именем " + shortcutName, Luwrain.MessageType.ERROR);//FIXME:
	    return;
	}
	final var appRef = new AtomicReference<Application[]>();
	unsafeAreaOperation(()->{
		appRef.set(shortcut.prepareApp(args));
	    });
	final Application[] app = appRef.get();
	if (app == null)
	{
	    message("Приложение " + shortcutName + " не готово к запуску", Luwrain.MessageType.ERROR);//FIXME:
	    return;
	}
	for(Application a: app)
	    if (a == null)
	    {
		message("Приложение " + shortcutName + " не готово к запуску", Luwrain.MessageType.ERROR);//FIXME:
		return;
	    }
	soundManager.stopStartingMode();
	for(Application a: app)
	    launchApp(a);
    }

    void launchApp(Application app)
    {
	notNull(app, "app");
	mainCoreThreadOnly();
	//Checking if it is a mono app
	if (app instanceof MonoApp)
	{
	    //	    final Application[] launchedApps = apps.getLaunchedApps();
	    for(Application a: apps.getLaunchedApps())
		if (a instanceof MonoApp && a.getClass().equals(app.getClass()))
		{
		    final MonoApp ma = (MonoApp)a;
		    final var ref = new AtomicReference<MonoApp.Result>();
		    unsafeAreaOperation(()->{
			    final MonoApp.Result value = ma.onMonoAppSecondInstance(app);
			    if (value != null)
				ref.set(value);
			});
		    if (ref.get() == null)
			continue;
		    final MonoApp.Result res = ref.get();
		    switch(res)
		    {
		    case SECOND_INSTANCE_PERMITTED:
			break;
		    case BRING_FOREGROUND:
			apps.setActiveApp(a);
			onNewAreasLayout();
			this.announcement = AnnouncementType.APP;
						return;
		    }
		}
	}
	final Luwrain o = interfaces.requestNew(app);
	Luwrain toRelease = o;//Must be cleaned to null when we sure the app is completely acceptable
	final InitResult initResult;
	try {
	    try {
		initResult = app.onLaunchApp(o);
	    }
	    catch (OutOfMemoryError e)
	    {
		error("no enough memory to launch the app of the class " + app.getClass().getName());
		message(i18n.getStaticStr("AppLaunchNoEnoughMemory"), Luwrain.MessageType.ERROR);
		return;
	    }
	    catch (Throwable e)
	    {
		error(e, "application " + app.getClass().getName() + " has thrown an exception on onLaunch()");
		launchAppCrash(new org.luwrain.app.crash.App(e, app, null));
		return;
	    }
if (initResult.getType() != InitResult.Type.OK)
{
    launchAppCrash(new org.luwrain.app.crash.App(new org.luwrain.app.crash.InitResultException(initResult), app, null));
		return;
	    }
	    if (initResult == null || !initResult.isOk())
	    {
		//FIXME:message
		return;
	    }
	    if (!apps.newApp(app))
		return;
	    toRelease = null;//We sure that the app is completely accepted
	}
	finally {
	    if (toRelease != null)
		interfaces.release(toRelease);
	}
	soundManager.stopStartingMode();
	onNewAreasLayout();
	this.announcement = AnnouncementType.APP;
	    }

    void launchAppCrash(Luwrain instance, Throwable e)
    {
	notNull(instance, "instance");
	notNull(e, "e");
	final Application app = interfaces.findApp(instance);
	if (app != null)
	    launchAppCrash(new org.luwrain.app.crash.App(e, app, null));
    }

    void launchAppCrash(org.luwrain.app.crash.App app)
    {
	notNull(app, "app");
	final Luwrain o = interfaces.requestNew(app);
	final InitResult initResult;
	try {
	    initResult = app.onLaunchApp(o);
	}
	catch (OutOfMemoryError ee)
	{
	    interfaces.release(o);
	    return;
	}
	if (initResult == null || !initResult.isOk())
	    {
		interfaces.release(o);
		return;
	    }
	if (!apps.newApp(app))
	{
	    interfaces.release(o);
	    return; 
	}
	onNewAreasLayout();
	this.announcement = AnnouncementType.APP;
    }

    void closeApp(Luwrain instance)
    {
	notNull(instance, "instance");
	mainCoreThreadOnly();
	if (instance == luwrain)
	    throw new IllegalArgumentException("Trying to close an application through the special interface object");
	final Application app = interfaces.findApp(instance);
	if (app == null)
	    throw new IllegalArgumentException("Trying to close an application through an illegal interface object");
	if (desktop != null && app == desktop)
	{
	    quit();
	    return;
	}
	if (apps.hasPopupOfApp(app))
	{
	    message(i18n.getStaticStr("AppCloseHasPopup"), Luwrain.MessageType.ERROR);
	    return;
	}
	try {
	    app.onAppClose();//FIXME: An ansafe operation
	}
	catch(Throwable e)
	{
	    error(e, "closing the app " + app.getClass().getName());
	}
	apps.removeApp(app);
	interfaces.release(instance);
	onNewAreasLayout();
	setAppIntroduction();
    }

    void onSwitchNextAppCommand()
    {
	mainCoreThreadOnly();
	apps.switchNextApp();
	onNewAreasLayout();
	this.announcement = AnnouncementType.APP;
	    }

    void announceActiveAreaIface()
    {
	announceActiveArea();
    }

    void onNewAreaLayout(Luwrain instance)
    {
		notNull(instance, "instance");
	mainCoreThreadOnly();
	final Application app = interfaces.findApp(instance);
	if (app == null)
	{
	    warn("trying to update area layout with the unknown app instance");
	    return;
	}
	apps.updateAppAreaLayout(app);
	onNewAreasLayout();
    }

    void onSwitchNextAreaCommand()
    {
	mainCoreThreadOnly();
	tiles.activateNextArea();
	onNewAreasLayout();
	announceActiveArea();
    }

    void popup(Application app, Area area, Popup.Position pos, StopCondition stopCondition, Set<Popup.Flags> flags)
    {
	notNull(area, "area");
	notNull(pos, "pos");
	notNull(stopCondition, "stopCondition");
	mainCoreThreadOnly();
	if (flags.contains(Popup.Flags.NO_MULTIPLE_COPIES))
	    apps.onNewPopupOpening(app, area.getClass());
	final PopupStopCondition popupStopCondition = new PopupStopCondition(mainStopCondition, stopCondition);
	apps.addNewPopup(app, area, pos, popupStopCondition, flags);
	tiles.setPopupActive();
	onNewAreasLayout();
	announceActiveArea();
	eventLoop(popupStopCondition);
	apps.closeLastPopup();
onNewAreasLayout();
	setAreaIntroduction();
    }



    //May return -1
    int getAreaVisibleHeightIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	mainCoreThreadOnly();
	Area effectiveArea = null;
	if (instance != null)
	{
	    final Application app = interfaces.findApp(instance);
	    if (app != null)
	    {
		if (!apps.isAppLaunched(app))
		    return -1;
		effectiveArea = apps.getCorrespondingEffectiveArea(app, area);
	    }
	}
	if (effectiveArea == null)
	    effectiveArea = apps.getCorrespondingEffectiveArea(area);
	if (effectiveArea == null)
	    return -1;
	return windowManager.getAreaVisibleHeight(effectiveArea);
    }

    int getScreenWidthIface()
    {
	mainCoreThreadOnly();
	return interaction.getWidthInCharacters();
    }

    int getScreenHeightIface()
    {
	mainCoreThreadOnly();
	return interaction.getHeightInCharacters();
    }

    //May return -1
    int getAreaVisibleWidthIface(Luwrain instance, Area area)
    {
	NullCheck.notNull(area, "area");
	mainCoreThreadOnly();
	Area effectiveArea = null;
	if (instance != null)
	{
	    final Application app = interfaces.findApp(instance);
	    if (app != null)
	    {
		if (!apps.isAppLaunched(app))
		    return -1;
		effectiveArea = apps.getCorrespondingEffectiveArea(app, area);
	    }
	}
	if (effectiveArea == null)
	    effectiveArea = apps.getCorrespondingEffectiveArea(area);
	if (effectiveArea == null)
	    return -1;
	return windowManager.getAreaVisibleWidth(effectiveArea);
    }

    void message(String text, Luwrain.MessageType messageType)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(messageType, "messageType");
	mainCoreThreadOnly();
	switch(messageType)
	{
	case ERROR:
	    message(text, Sounds.ERROR);
	    break;
	case OK:
	    message(text, Sounds.OK);
	    break;
	case DONE:
	    message(text, Sounds.DONE);
	    break;
	case ANNOUNCEMENT:
	    message(text, Sounds.ANNOUNCEMENT);
	    break;
	case ALERT:
	    message(text, Sounds.ALERT);
	    break;
	case UNAVAILABLE:
	    message(text, Sounds.BLOCKED);
	    break;
	case NONE:
	    message(text, (Sounds)null);
	    break;
	case REGULAR:
	default:
	    message(text,Sounds.MESSAGE);
	}
    }

    void message(String text, Sounds sound)
    {
	mainCoreThreadOnly();
	if (text == null || text.trim().isEmpty())
	    return;
	this.announcement = null;
	if (sound != null)
	    soundManager.playIcon(sound);
	speech.speak(i18n.getSpeakableText(text, Luwrain.SpeakableTextType.NATURAL), Speech.PITCH_MESSAGE, 0);
	interaction.startDrawSession();
	interaction.clearRect(0, interaction.getHeightInCharacters() - 1, interaction.getWidthInCharacters() - 1, interaction.getHeightInCharacters() - 1);
	interaction.drawText(0, interaction.getHeightInCharacters() - 1, text, true);
	interaction.endDrawSession();
    }

    void fontSizeInc()
    {
	mainCoreThreadOnly();
	interaction.setDesirableFontSize(interaction.getFontSize() + 5); 
	windowManager.redraw();
	apps.sendBroadcastEvent(new SystemEvent(SystemEvent.Type.BROADCAST, SystemEvent.Code.FONT_SIZE_CHANGED));
	message(i18n.getStaticStr("FontSize") + " " + interaction.getFontSize(), Luwrain.MessageType.REGULAR);
    }

    void fontSizeDec()
    {
	mainCoreThreadOnly();
	if (interaction.getFontSize() < 15)
	    return;
	interaction.setDesirableFontSize(interaction.getFontSize() - 5); 
	windowManager.redraw();
	apps.sendBroadcastEvent(new SystemEvent(SystemEvent.Type.BROADCAST, SystemEvent.Code.FONT_SIZE_CHANGED));
	message(i18n.getStaticStr("FontSize") + " " + interaction.getFontSize(), Luwrain.MessageType.REGULAR);
    }

    void openFiles(String[] fileNames)
    {
	NullCheck.notEmptyItems(fileNames, "fileNames");
	mainCoreThreadOnly();
	if (fileNames.length < 1)
	    return;
	fileTypes.launch(this, null, fileNames);
    }

            boolean runCommand(String command)
    {
	notNull(command, "command");
	mainCoreThreadOnly();
	if (command.trim().isEmpty())
	    return false;
	return commands.run(command.trim());
    }

    void startAreaListening()
    {
	final Area activeArea = getActiveArea(true);
	if (activeArea == null)
	    return;
	stopAreaListening();
	speech.silence();
	this.listening = null;//FIXME:newregnew Listening(luwrain, speech, activeArea, ()->listeningProp.setStatus(false));
	final AtomicBoolean res = new AtomicBoolean();
	unsafeAreaOperation(()->res.set(listening.start()));
	if (res.get())
	    //	    listeningProp.setStatus(true); else
	    eventNotProcessedMessage();
    }

    void stopAreaListening()
    {
	if (listening == null)
	    return;
	listening.cancel();
	listening = null;
    }

    private final class StartingModeProperty implements PropertiesProvider
    {
	static private final String PROP_NAME = "luwrain.startingmode";
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
	    if (propName.equals(PROP_NAME))
		return EnumSet.of(PropertiesProvider.Flags.PUBLIC,
				  PropertiesProvider.Flags.READ_ONLY);
	    return null;
	}
	@Override public String getProperty(String propName)
	{
	    NullCheck.notEmpty(propName, "propName");
	    if (propName.equals(PROP_NAME))
		return wasInputEvents?"0":"1";
	    return null;
	}
	@Override public boolean setProperty(String propName, String value)
	{
	    NullCheck.notEmpty(propName, "propName");
	    NullCheck.notNull(value, "value");
	    return false;
	}
	@Override public void setListener(PropertiesProvider.Listener listener)
	{
	}
    }
}
