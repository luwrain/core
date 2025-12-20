// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

public interface MonoApp extends Application
{
    public enum Result {DO_NOTHING, SECOND_INSTANCE_PERMITTED, BRING_FOREGROUND};

    Result onMonoAppSecondInstance(Application app);
}
