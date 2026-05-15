// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2026 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.io.json;

import java.util.*;
import java.lang.reflect.*;

import com.google.gson.*;
import com.google.gson.annotations.*;
import com.google.gson.reflect.*;

import org.luwrain.core.*;

public class DesktopItem
{
    static public final String TYPE_UNIREF = "uniref";
    static public final Type LIST_TYPE = new TypeToken<List<DesktopItem>>(){}.getType();

    static private Gson gson = null;

    @SerializedName("type")
    private String type = null;

    @SerializedName("value")
    private String value = null;

    private transient UniRefInfo uniRefInfo = null;

    public DesktopItem()
    {
    }

    public DesktopItem(String type, String value)
    {
	this.type = type;
	this.value = value;
    }

        public DesktopItem(UniRefInfo uniRefInfo)
    {
	NullCheck.notNull(uniRefInfo, "uniRefInfo");
	this.type = TYPE_UNIREF;
	this.value = uniRefInfo.getValue();
	this.uniRefInfo = uniRefInfo;
    }


        public UniRefInfo getUniRefInfo(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	if (this.uniRefInfo != null)
	    return this.uniRefInfo;
	this.uniRefInfo = luwrain.getUniRefInfo(getValueNotNull());
	return this.uniRefInfo;
    }

    public String getType()
    {
	return this.type;
    }

    public String getTypeNotNull()
    {
	return type != null?type:"";
    }

    public void setType(String type)
    {
	this.type = type;
    }

    public String getValue()
    {
	return this.value;
    }

    public String getValueNotNull()
    {
	return value != null?value:"";
    }

    public void setValue(String value)
    {
	 this.value = value;
	 }

    static public String toJson(DesktopItem[] items)
    {
	if (gson == null)
	    gson = new Gson();
	return gson.toJson(items);
    }

    static public DesktopItem[]  fromJson(String s)
    {
	NullCheck.notNull(s, "s");
	if (gson == null)
	    gson = new Gson();
final List<DesktopItem> res = new Gson().fromJson(s, LIST_TYPE);
return res != null?res.toArray(new DesktopItem[res.size()]):new DesktopItem[0];
    }
}
