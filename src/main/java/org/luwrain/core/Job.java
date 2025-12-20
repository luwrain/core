// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

public interface Job
{
    static public final int
	EXIT_CODE_OK = 0,
	EXIT_CODE_INVALID = -1,
	EXIT_CODE_INTERRUPTED = -2;

    public enum Status {RUNNING, FINISHED};

    	String getInstanceName();
	Status getStatus();
	int getExitCode();
	boolean isFinishedSuccessfully();
	List<String> getInfo(String infoType);
	void stop();

    public interface Listener
    {
	//The job must provide the new instance 
	void onStatusChange(Job job);
	void onInfoChange(Job job, String infoType, List<String> value);
    }
}
