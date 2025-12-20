// SPDX-License-Identifier: BUSL-1.1
// Copyright 2012-2025 Michael Pozhidaev <msp@luwrain.org>

package org.luwrain.core;

import java.util.*;

public interface Shortcut extends ExtensionObject
{
    public enum Flags { MULTIPLE_ARGS, URL_ARGS };

    Application[] prepareApp(String[] args);
    Set<Flags> getShortcutFlags();
    String[] getFileExtensions();
}
