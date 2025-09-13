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
import java.io.File;
import java.nio.file.*;//FIXME:

import org.luwrain.core.events.*;
import org.luwrain.speech.Channel;

/**
 * The main bridge for applications and extensions to communicate with the LUWRAIN core.
 * This class serves as the central hub for applications and extensions to invoke system routines.
 * It ensures that applications and extensions never gain access deeper than what this class provides.
 * Packages like {@link org.luwrain.controls} and {@link org.luwrain.popups} always encapsulate an instance of the {@code Luwrain} class,
 * guaranteeing that they cannot offer more access to the system core than allowed by the provided {@code Luwrain} instance.
 * <p>
 * The core generates a new instance of this class for each newly launched application or loaded extension.
 * This mechanism enables the environment to identify which application or extension has initiated a specific request.
 * Applications receive their associated object through the {@code onLaunch()} method,
 * while extensions obtain the corresponding instance through the arguments of the methods they override.
 * It is crucial to keep the provided instance confidential.
 * <p>
 * It might seem slightly confusing that an extension and the applications launched by it receive different instances of the {@code Luwrain} class.
 * However, this distinction is necessary to differentiate multiple instances of the same application,
 * as an extension can be loaded only once.
 * <p>
 * Different instances of the {@code Luwrain} class may offer varying levels of access.
 * This design is essential to ensure that extensions are more precise and transparent in their operations.
 */
public interface Luwrain extends PropertiesBase, HookContainer
{
    static public final String
	PATH_SYS_DATA_DIR = "SYS_DATA_DIR",
		PATH_SYS_JS_DIR = "SYS_JS_DIR",
	PROP_DIR_DATA = "luwrain.dir.data",
	PROP_DIR_JS = "luwrain.dir.js",
	PROP_DIR_USERHOME = "luwrain.dir.userhome",
	PROP_DIR_SOUNDS = "luwrain.dir.sounds";

    public enum AnnouncementType { ALERT, CHAT, REGULAR};
    public enum MessageType { 	ALERT, ANNOUNCEMENT, DONE, ERROR, NONE, OK, REGULAR, UNAVAILABLE };
    public enum AreaTextType { REGION, WORD, LINE, SENTENCE, URL };
    public enum SpeakableTextType { NONE, NATURAL, PROGRAMMING };
    public enum AreaAttr { DIRECTORY, UNIREF, URL, UNIREF_UNDER_HOT_POINT, URL_UNDER_HOT_POINT};
    public enum JobFlags { TRACKING };

    <C> void saveConf(C conf);
    <C> C loadConf(Class<C> cl);
    <C> void updateConf(Class<C> configClass, ConfigUpdate<C> func);
    String getDir(String type);
    void announceActiveArea();
    Object callUiSafely(java.util.concurrent.Callable callable);
    void closeApp();
    void crash(org.luwrain.app.crash.App app);
    void crash(Throwable e);
    boolean announcement(String text, AnnouncementType type, String component, String category, Map<String, String> args);


    //Never returns null, returns user home dir if area doesn't speak about that
    String getActiveAreaAttr(AreaAttr attr);
    String getActiveAreaText(AreaTextType type, boolean issueErrorMessage);
    String[] getAllShortcutNames();

    /**
     * Returns a path to the directory where the application may safely store
     * its auxiliary data. The returned directory, if it it isn't {@code null},
     * always exists and always belongs to the current user. Meaning,
     * different users get different directories for the same
     * application. Application must be identified with some short string. We
     * discourage using application names starting with "luwrain.", because
     * such names are usually used by the applications from LUWRAIN standard
     * distribution.
     *
     * @param appName A short string for application identification, the same application name will result in the same directory
     * @return The application data directory or {@code null} if the directory cannot be created
     */
    Path getAppDataDir(String appName);

    java.io.File createTempFile(String prefix);

    //May return -1 if area is not shown on the screen;
    int getAreaVisibleHeight(Area area);

    int getAreaVisibleWidth(Area area);
    Clipboard getClipboard();
    String[] xGetLoadedSpeechFactories();
    MediaResourcePlayer[] getMediaResourcePlayers();
    org.luwrain.player.Player getPlayer();
String getPath(String pathId);
    String getString(String strId);
    String loadScript(ScriptSource scriptSource) throws ExtensionException;
    int getScreenWidth();
    int getScreenHeight();

    //Never returns null, doesn't take empty strings
    UniRefInfo getUniRefInfo(String uniRef);
    void executeBkg(Runnable runnable);
    org.luwrain.i18n.I18n i18n();
    void launchApp(String shortcutName);
    void launchApp(String shortcutName, String[] args);
    org.luwrain.speech.Channel loadSpeechChannel(String engineName, String params) throws org.luwrain.speech.SpeechException;
    void message(String text);
    void message(String text, MessageType messageType);
    void message(String text, Sounds sound);
    <T> T createInstance(Class<T> c);
        <T> List<T> createInstances(Class<T> c);
    FileFetcher[] findFetchers(String url);
    void onAreaNewBackgroundSound(Area area);

    /**
     * Notifies the environment that the area gets new position of the hot
     * point. This method causes updating of the visual position of the hot
     * point on the screen for low vision users.  Please keep in mind that
     * this method doesn't produce any speech announcement of the new
     * position and you should do that on your own, depending on the
     * behaviour of your application.
     *
     * @param area The area which gets new position of the hot point
     */
    void onAreaNewHotPoint(Area area);

    /**
     * Notifies the environment that the area gets new content. This method
     * causes updating of the visual representation of the area content on
     * the screen for low vision users.  Please keep in mind that this method
     * doesn't produce any speech announcement of the changes and you should
     * do that on your own, depending on the behaviour of your application.
     *
     * @param area The area which gets new content
     */
    void onAreaNewContent(Area area);

    /**
     * Notifies the environment that the area gets new name. This method
     * causes updating of the visual title of the area on the screen for low
     * vision users.  Please keep in mind that this method doesn't produce
     * any speech announcement of name changes and you should do that on your
     * own, depending on the behaviour of your application.
     *
     * @param area The area which gets new name
     */
    void onAreaNewName(Area area);

    //Doesn't produce any announcement
    void onNewAreaLayout();
    void openFile(String fileName);
    void openFiles(String[] fileNames);
    boolean openHelp(String sectName);
    boolean openUniRef(String uniRef);
    boolean openUniRef(UniRefInfo uniRefInfo);

    boolean openUrl(String url);

    /**
       /**
       * Plays one of the system sounds.  This method takes an identifier of
       * the system sound, stops any previous playing, if there was any, and
       * plays. The exact sound is selected depending on user's
       * settings. Please node that sounds playing isn't interfering with
       * speech. 
       *
       * @param sound The identifier of the sound to play
       */
    void playSound(Sounds sound);
    void playSound(java.io.File file);
    void popup(Popup popup);

    /**
     * Registers new extension object.  This operation is allowed for highly
     * privileged interfaces only, what, for example, can be useful for
     * custom startup and shutdown procedures. Custom objects may be of any
     * kind, they are registered as they would be a part of the core.
     *
     * @param extObj The object to register
     * @return True if the object was successfully registered, false otherwise
     * @throws RuntimeException on any attempt to do this operation without enough privileged level
     */
    boolean registerExtObj(ExtensionObject extObj);
    boolean runCommand(String command);
    Job newJob(String name, String[] args, String dir, Set<JobFlags> flags, Job.Listener listener);
    //    void runInMainThread(Runnable runnable);
    Object runLaterSync(java.util.concurrent.Callable callable);
    void runUiSafely(Runnable runnable);
    boolean runWorker(String workerName);
    void speak(String text);
    void speak(String text, Sounds sound);
    void speakLetter(char letter);
    void speak(String text, int pitch);
    void speak(String text, int pitch, int rate);
    void speakLetter(char letter, int pitch);
        void sendBroadcastEvent(SystemEvent event);
    void sendInputEvent(InputEvent event);

    /**
     * Sets the new active area of the application. This method asks the
     * environment to choose another visible area as an active area of the
     * application. This operation is applicable only to regular areas, not
     * for popup areas, for which it is pointless. In contrast to
     * {@code onAreaNewHotPoint()}, {@code onAreaNewName()} and 
     * {@code onAreaNewContent()} methods, this one produces proper introduction of
     * the area being activated.
     *
     * @param area The area to choose as an active
     */
    void setActiveArea(Area area);
    void setEventResponse(EventResponse eventResponse);
    void silence();
    void speakLetter(char letter, int pitch, int rate);
    String staticStr(org.luwrain.i18n.LangStatic id);//FIXME:

        //never returns null
    String suggestContentType(java.net.URL url, ContentTypes.ExpectedType expectedType);
    String getSpeakableText(String text, SpeakableTextType type);

        //never returns null
    String suggestContentType(java.io.File file, ContentTypes.ExpectedType expectedType);
    boolean unloadDynamicExtension(String extId);
    boolean xQuit();
    void xSetSpeechRate(int value);
    int xGetSpeechRate();
    int xGetSpeechPitch();
    void xSetSpeechPitch(int value);
    void showGraphical(Interaction.GraphicalMode graphicalMode);
    ScriptFile[] getScriptFilesList(String componentName);
    String escapeString(String style, String text);
}
