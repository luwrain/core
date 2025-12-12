// SPDX-License-Identifier: Apache-2.0
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.i18n;

import java.util.*;
import java.util.function.*;
import java.net.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.nlp.*;
import org.luwrain.script.*;
import org.luwrain.script.hooks.*;

import static java.util.Objects.*;

abstract public class LangBase implements Lang
{
    protected final String langName;
    protected final Luwrain luwrain;
protected final Map<String, String> staticStrings;
protected final Map<String, String> chars;

    public LangBase(String langName, Luwrain luwrain, Map<String, String> staticStrings, Map<String, String> chars)
    {
requireNonNull(langName, "langName can't be null");
	requireNonNull(luwrain, "luwrain can't be null");
	requireNonNull(staticStrings, "staticStrings can't be null");
requireNonNull(chars, "chars can't be null");
if (langName.isEmpty())
    throw new IllegalArgumentException("langName can't be empty");
	this.langName = langName;
	this.luwrain = luwrain;
	this.staticStrings = staticStrings;
	this.chars = chars;
    }

    @Override public String getStaticStr(String id)
    {
	NullCheck.notEmpty(id, "id");
	return staticStrings.get(id);
    }

    @Override public String hasSpecialNameOfChar(char ch)
    {
	if (Character.isLetterOrDigit(ch))
	    return null;
	final String name = Character.getName(ch);
	if (name == null || name.isEmpty())
	    return null;
	final String newName = name.toLowerCase().replaceAll(" ", "_").replaceAll("-", "_");
	return chars.containsKey(newName)?chars.get(newName):newName;
    }

        @Override public String getNumberStr(Number num, GrammaticalAttr gramAttr, Word depWord)
    {
	return null;
    }

    @Override public Word[] getWord(String word)
    {
	return new Word[0];
    }

    @Override public InputStream getResource(String resourceName)
    {
	NullCheck.notEmpty(resourceName, "resourceName");
	final URL url = getClass().getClassLoader().getResource("org/luwrain/i18n/" + langName + "/" + resourceName);
	if (url == null)
	    return null;
	try {
	    return url.openStream();
	}
	catch(IOException e)
	{
	    Log.error(langName, "unable to open stream for the lang resource '" + resourceName + "\':" + e.getClass().getName() + ":" + e.getMessage());
return null;
	}
    }

    @Override public String getTextExp(String expName, Function<Object, Object> args)
    {
	NullCheck.notEmpty(expName, "expName");
	NullCheck.notNull(args, "args");
	try {
	    /*	    final EmptyHookObject argsObj = new EmptyHookObject(){
		    @Override public Object getMember(String name)
		    {
			 NullCheck.notEmpty(name, "name");
			 Object res = args.apply(name);
			 if (res == null)
			     return null;
			 if (res instanceof String  || res instanceof Number || res instanceof Boolean)
			     return res;
			 return null;
			 }
		};
	    final ProviderHook hook = new ProviderHook(luwrain);
	    final Object res = hook.run("luwrain.i18n." + langName + ".text", new Object[]{expName, argsObj});
	    if (res == null)
		return null;
	    return res.toString();
	    */
	    return null;
	}
	catch(RuntimeException e)
	{
	    Log.error(langName, "unable to run the luwrain.i18n." + langName + ".text hook for " + expName + ":" + e.getClass().getName() + ":" + e.getMessage());
	    return null;
	}
    }
}
