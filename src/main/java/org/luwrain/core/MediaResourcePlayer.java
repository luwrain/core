// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.net.*;
import java.util.*;

public interface MediaResourcePlayer extends ExtensionObject 
{
    public enum Flags {}

    static public final class Result
    {
	public enum Type {OK, INACCESSIBLE_SOURCE};
	private final Type type;
	public Result()
	{
	    this.type = Type.OK;
	}
		public Result(Type type)
	{
	    NullCheck.notNull(type, "type");
	    this.type = Type.OK;
	}
	public Type  getType()
	{
	    return type;
	}
	public boolean isOk()
	{
	    return type == Type.OK;
	}
    }

    public interface Listener
    {
	void onPlayerTime(Instance instance, long msec);
	void onPlayerFinish(Instance instance);
	void onPlayerError(Exception e);//FIXME:instance
    }

    static public final class Params
    {
	public long playFromMsec = 0;
	public int volume = 100;
	public Set<Flags> flags = EnumSet.noneOf(Flags.class);
    }

    public interface Instance
    {
	Result play(URL url, Params params);
	void setVolume(int volume);
	void stop();
    }

    Instance newMediaResourcePlayer(Listener listener);
    String getSupportedMimeType();//FIXME:multiple types
}
