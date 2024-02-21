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

import org.luwrain.core.*;

import static org.luwrain.core.NullCheck.*;

public final class JobsManager 
{
    static private final String LOG_COMPONENT = Core.LOG_COMPONENT;

    private final Luwrain luwrain;
    private final ObjRegistry objRegistry;
    public final List<Entry> entries = new ArrayList<>();

    JobsManager(Luwrain luwrain, ObjRegistry objRegistry)
    {
	notNull(luwrain, "luwrain");
	notNull(objRegistry, "objRegistry");
	this.luwrain = luwrain;
	this.objRegistry = objRegistry;
    }

    Job.Instance run(String name, String[] args, String dir, Job.Listener listener)
    {
	notEmpty(name, "name");
	notNullItems(args, "args");
	notNull(dir, "dir");
	notNull(listener, "listener");
	final Job job = objRegistry.getJob(name);
	if (job == null)
	    throw new IllegalArgumentException("No such job: " + name);
	Log.debug(LOG_COMPONENT, "starting the job '" + name + "' and arguments " + Arrays.toString(args));
	final Entry entry = new Entry(listener);
	final Job.Instance instance = job.launch(entry, args, dir.isEmpty()?null:dir);
	if (instance == null)
	    return null;
	entry.setInstance(instance);
	entries.add(entry);
	return entry;
    }

    private void onFinish(Entry entry)
    {
	notNull(entry, "entry");
	luwrain.runUiSafely(()->{
		luwrain.playSound(entry.isFinishedSuccessfully()?Sounds.DONE:Sounds.ERROR);
	    });
    }

    //Public for the control app
    public final class Entry implements Job.Listener, Job.Instance
    {
	private final Job.Listener listener;
	private Job.Instance instance = null;
	Entry(Job.Listener listener)
	{
	    notNull(listener, "listener");
	    this.listener = listener;
	}

	@Override public String getInstanceName() { return instance.getInstanceName(); }
	@Override public Job.Status getStatus() { return instance.getStatus(); }
	@Override public int getExitCode() { return instance.getExitCode(); }
	@Override public boolean isFinishedSuccessfully() { return instance.isFinishedSuccessfully(); }
	@Override public String getSingleLineState() { return instance.getSingleLineState(); }
	@Override public String[] getMultilineState() { return instance.getMultilineState(); }
	@Override public String[] getNativeState() { return instance.getNativeState(); }
	@Override public void stop() { instance.stop(); }

			@Override public void onInfoChange(Job.Instance instance, String type, List<String> value){}
	@Override public void onStatusChange(Job.Instance instance)
	{
	    listener.onStatusChange(this);
	    if (instance.getStatus() == Job.Status.FINISHED)
		onFinish(this);
	}
	@Override public void onSingleLineStateChange(Job.Instance instance)
	{
	    listener.onSingleLineStateChange(this);
	}
	@Override public void onMultilineStateChange(Job.Instance instance)
	{
	    listener.onMultilineStateChange(this);	    
	}
	@Override public void onNativeStateChange(Job.Instance instance)
	{
	    listener.onNativeStateChange(this);
	}
	@Override public String toString()
	{
	    return getInstanceName();
	}
	void setInstance(Job.Instance instance)
	{
	    notNull(instance, "instance");
	    if (this.instance == null)
		this.instance = instance;
	}
    }
}
