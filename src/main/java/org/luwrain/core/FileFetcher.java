// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

public interface FileFetcher extends ExtensionObject
{
    public enum Flags { RESUMABLE, SPECIFIC };
    public enum Status { RUNNING, COMPLETED, CANCELLED };

    boolean canHandleUrl(String url);
    Set<Flags> getFlags();
    Fetching fetchUrl(String url, String destDir, String destFileName);

    public interface Fetching
    {
	float getProgress();
	boolean isCompleted();
	String getDestinationFile();
	String getSourceUrl();
    }

    public interface Listener
    {
	void onFetchingStatus(Fetching fetching, Status status, float progress);
    }
}
