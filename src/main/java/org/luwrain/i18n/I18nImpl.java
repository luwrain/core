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

package org.luwrain.i18n;

import java.util.*;
import org.apache.logging.log4j.*;

import org.luwrain.core.*;

import static java.util.Objects.*;

public final class I18nImpl implements I18n, I18nExtension
{
    static private final Logger log = LogManager.getLogger();

    static private final String
	EN_LANG = "en",
	NO_SELECTED_LANG = "#NO SELECTED LANGUAGE#";

    private Lang selectedLang = null;
    private String selectedLangName = "";

    private final List<CommandTitle> commandTitles = new ArrayList<>();
    private final List<StringsObj> stringsObjs = new ArrayList<>();
    private final Map<String, Lang> langs = new HashMap<>();

    @Override public Lang getActiveLang() { return selectedLang;}
    @Override public Map<String, Lang> getAllLangs() { return new HashMap<>(langs); }

    @Override public Lang getLang(String langName)
    {
	NullCheck.notEmpty(langName, "langName");
	return langs.containsKey(langName)?langs.get(langName):null;
    }

    public String getSpeakableText(String text, Luwrain.SpeakableTextType speakableTextType)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(speakableTextType, "speakableTextType");
	if (selectedLang == null)
	    return NO_SELECTED_LANG;
	try {
	    final String value = selectedLang.getSpeakableText(text, speakableTextType);
	    return value != null?value:text;
	}
	catch(Throwable e)
	{
	    log.error("Unable to make a speakable text of type " + speakableTextType + ": " + e.getClass().getName() + ": " + e.getMessage());
	    return text;
	}
    }

    @Override public String getPastTimeBrief(Date date)
    {
	NullCheck.notNull(date, "date");
	if (selectedLang == null)
	    return NO_SELECTED_LANG;
	final String value = selectedLang.pastTimeBrief(date);
	return value != null?value:"";
    }

    @Override public String getExceptionDescr(Exception e)
    {
	NullCheck.notNull(e, "e");
	if (e instanceof java.nio.file.NoSuchFileException)
	    return e.getMessage() + ":нет такого файла";
	if (e instanceof java.nio.file.AccessDeniedException)
	    return e.getMessage() + ":отказано в доступе";
	if (e instanceof java.nio.file.DirectoryNotEmptyException)
	    return e.getMessage() + ":каталог не пуст";
	if (e instanceof java.nio.file.DirectoryNotEmptyException)
	    return e.getMessage() + ":каталог не пуст";
	if (e instanceof java.nio.file.FileAlreadyExistsException)
	    return e.getMessage() + ":файл уже существует";
	if (e instanceof java.nio.file.InvalidPathException)
	    return e.getMessage() + ":неверно оформленный путь к файлу";
	if (e instanceof java.nio.file.NotDirectoryException)
	    return e.getMessage() + ":не является каталогом";
	if (e instanceof java.nio.file.NotLinkException)
	    return e.getMessage() + ":не является ссылкой";
	if (e instanceof java.nio.file.ReadOnlyFileSystemException)
	    return e.getMessage() + ":файловая система доступна только для чтения";
	return e.getMessage() + ":" + e.getClass().getName();
    }

    @Override public String getNumberStr(int count, String entities)
    {
	NullCheck.notNull(entities, "entities");
	if (selectedLang == null)
	    return NO_SELECTED_LANG;
	final String value = selectedLang.getNumberStr(count, entities);
	return value != null?value:"";
    }

    @Override public String staticStr(LangStatic id)
    {
	requireNonNull(id, "id can't be null");
	return getStaticStr(convertStaticValueName(id.toString()));
    }

    @Override public String getStaticStr(String id)
    {
	requireNonNull(id, "id can't be null");
	if (selectedLang == null)
	    return NO_SELECTED_LANG;
	final String value = selectedLang.getStaticStr(id);
	return value != null && !value.isEmpty()?value:"#" + id + "#";
    }

    @Override public String hasSpecialNameOfChar(char ch)
    {
	return selectedLang != null?selectedLang.hasSpecialNameOfChar(ch):null;
    }

    @Override public String getCommandTitle(String command)
    {
	NullCheck.notEmpty(command, "command");
	String selectedLangValue = null;
	String enLangValue = null;
	String anyLangValue = null;
	for(CommandTitle t: commandTitles)
	{
	    if (!t.command.equals(command))
		continue;
	    if (anyLangValue == null)
		anyLangValue = t.title;
	    if (t.lang.equals(selectedLangName))
		selectedLangValue = t.title;
	    if (t.lang.equals(EN_LANG))
		enLangValue = t.title;
	}
	if (selectedLangValue != null)
	    return selectedLangValue;
	if (enLangValue != null)
	    return enLangValue;
	return anyLangValue != null?anyLangValue:command;
    }

    @Override public void addCommandTitle(String lang, String command, String title)
    {
	NullCheck.notEmpty(lang, "lang");
	NullCheck.notEmpty(command, "command");
	NullCheck.notEmpty(title, "title");
	for(CommandTitle t: commandTitles)
	    if (t.lang.equals(lang) && t.command.equals(command))
		return;
	commandTitles.add(new CommandTitle(lang, command, title));
    }

    @Override public Object getStrings(String component)
    {
	NullCheck.notEmpty(component, "component");
	Object selectedLangObj = null;
	Object enLangObj = null;
	Object anyLangObj = null;
	for(StringsObj o: stringsObjs)
	{
	    if (!o.component.equals(component))
		continue;
	    if (anyLangObj == null)
		anyLangObj = o.obj;
	    if (o.lang.equals(selectedLangName))
		selectedLangObj = o.obj;
	    if (o.lang.equals(EN_LANG))
		enLangObj = o.obj;
	}
	if (selectedLangObj != null)
	    return selectedLangObj;
	if (enLangObj != null)
	    return enLangObj;
	return anyLangObj;
    }

        @Override public <E> E getStrings(Class<E> stringsClass)
    {
	requireNonNull(stringsClass, "stringsClass");
	Object selectedLangObj = new EmptyStringsObj().create(stringsClass.getClassLoader(), stringsClass);
	Object enLangObj = null;
	Object anyLangObj = null;
	for(StringsObj o: stringsObjs)
	{
	    if (!o.component.equals(stringsClass.getName()))
		continue;
	    anyLangObj = o.obj;
	    if (o.lang.equals(selectedLangName))
		selectedLangObj = o.obj;
	    if (o.lang.equals(EN_LANG))
		enLangObj = o.obj;
	}
	if (selectedLangObj != null)
	    return (E)selectedLangObj;
	if (enLangObj != null)
	    return (E)enLangObj;
	return (E)anyLangObj;
    }


    @Override public void addStrings(String lang, String component, Object obj)
    {
	requireNonNull(lang, "lang can't be null");
	requireNonNull(component, "component can't be null");
	requireNonNull(obj, "obj can't be null");
	for(StringsObj o: stringsObjs)
	    if (o.lang.equals(lang) && o.component.equals(component))
		return;
	log.trace("Adding the strings object " + component + " for the language  "+ lang);
	stringsObjs.add(new StringsObj(lang, component, obj));
    }

    @Override public boolean addLang(String name, Lang lang)
    {
	requireNonNull(name, "name can't be null");
	requireNonNull(lang, "lang can't be null");
	if (langs.containsKey(name))
	{
	    log.warn("Trying to add the language  "+ name + " twice");
	    return false;
	}
	    log.trace("Adding the lang " + name);
	langs.put(name, lang);
	return true;
    }

    public boolean selectLang(String name)
    {
	NullCheck.notEmpty(name, "name");
	if (langs.isEmpty())
	{
	    log.error("No langs registered, unable to choose the default");
	    return false;
	}
	for(Map.Entry<String, Lang> l: langs.entrySet())
	    log.trace("Lang \'" + l.getKey() + "\' loaded");
	Lang desiredLang = null;
	String desiredLangName = "";
	Lang anyLang = null;
	String anyLangName = "";
	Lang enLang = null;
	for(Map.Entry<String, Lang> l: langs.entrySet())
	{
	    if (anyLang == null)//Preferably taking the first one
	    {
		anyLang = l.getValue();
		anyLangName = l.getKey();
	    }
	    if (l.getKey().equals(name))
	    {
		desiredLang = l.getValue();
		desiredLangName = name;
	    }
	    if (l.getKey().equals(EN_LANG))
		enLang = l.getValue();
	}
	if (desiredLang == null)
	    log.warn("The desired language \'" + name + "\' not found");
	if (enLang == null)
	    log.warn("English language not found");
	if (desiredLang != null)
	{
	    selectedLang = desiredLang;
	    selectedLangName = desiredLangName;
	} else
	    if (enLang != null)
	    {
		selectedLang = enLang;
		selectedLangName = EN_LANG;
	    } else
	    {
		selectedLang = anyLang;
		selectedLangName = anyLangName;
	    }
	log.debug("the selected language is \'" + selectedLangName + "\'");
	return true;
    }

    String getSelectedLangName()
    {
	return selectedLangName;
    }

    static private String convertStaticValueName(String name)
    {
	final StringBuilder b = new StringBuilder();
	boolean nextCap = true;
	for(int i = 0;i < name.length();++i)
	{
	    final char c = name.charAt(i);
	    if (c == '_')
	    {
		nextCap = true;
		continue;
	    }
	    if (nextCap)
		b.append(Character.toUpperCase(c)); else
		b.append(Character.toLowerCase(c));
	    nextCap = false;
	}
	return b.toString();
    }

    static private final class CommandTitle
    {
	final String lang;
	final String command;
	final String title;
	CommandTitle(String lang, String command, String title)
	{
	    NullCheck.notEmpty(lang, "lang");
	    NullCheck.notEmpty(command, "command");
	    NullCheck.notEmpty(title, "title");
	    this.lang = lang;
	    this.command = command;
	    this.title = title;
	}
    }

    static private final class StringsObj
    {
	final String lang;
	final String component;
	final Object obj;
	StringsObj(String lang, String component, Object obj)
	{
	    NullCheck.notEmpty(lang, "lang");
	    NullCheck.notEmpty(component, "component");
	    NullCheck.notNull(obj, "obj");
	    this.lang = lang;
	    this.component = component;
	    this.obj = obj;
	}
    };
}
