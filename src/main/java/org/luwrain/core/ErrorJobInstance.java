// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

import static org.luwrain.core.NullCheck.*;

public final class ErrorJobInstance implements Job
{
    private final String name;
    private final String message;

    public ErrorJobInstance(String name, String message)
    {
	notEmpty(name, "name");
	notEmpty(message, "message");
	this.name = name;
	this.message = message;
    }

    @Override public void stop()
    {
    }

    @Override public String getInstanceName()
    {
	return name;
    }

    @Override public Job.Status getStatus()
    {
	return Job.Status.FINISHED;
    }

    @Override public int getExitCode()
    {
	return 1;
    }

    @Override public boolean isFinishedSuccessfully()
    {
	return false;
    }

    @Override public List<String> getInfo(String infoType)
    {
	notEmpty(infoType, "infoType");
	if (infoType.equals("main"))
	    return Arrays.asList(message);
	return Arrays.asList();
    }
}
