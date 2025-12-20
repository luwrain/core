// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

public interface JobLauncher extends ExtensionObject
{
    public enum Flags {WITH_SHORTCUT, INTERACTIVE_SHORTCUT};


    public enum Status {RUNNING, FINISHED};

        Job launch(Job.Listener listener, String[] args, String dir);
    Set<Flags> getJobFlags();
}
